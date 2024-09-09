import { useRef, useState, useEffect, useLayoutEffect, FC } from "react";
import Post from "./Post";
import InfiniteScroll from "react-infinite-scroll-component";
import Popup from "reactjs-popup";
import { useCookies } from "react-cookie";
import Fuse from "fuse.js";
import { GroupT, UserT, StateProps } from "../interfaces";

const AddUsersPopup = ({
  WSClient,
  currentGroup,
}: {
  currentGroup: GroupT;
  WSClient: WebSocket | undefined;
}) => {
  const [allUsers, setAllUsers] = useState<UserT[]>([]);
  const [selectedUsers, setSelectedUsers] = useState<UserT[]>([]);
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [cookies] = useCookies(["jwt", "name"]);
  const options = {
    includeScore: true,
    includeMatches: true,
    threshold: 0.55,
    keys: ["name"],
  };
  const fuse = new Fuse(allUsers, options);

  const sendInvites = () => {
    setSelectedUsers([]);
    WSClient!.send(
      "3/" +
        JSON.stringify({
          owner: currentGroup.owner,
          invitedUsers: selectedUsers.map((user) => user.name),
          groupName: currentGroup.name,
          groupID: currentGroup.id,
        })
    );
  };

  useEffect(() => {
    fetch("http://localhost:8080/users/all", {
      headers: {
        Authorization: `Bearer ${cookies.jwt}`,
      },
    })
      .then((resp) => resp.json())
      .then((users) => setAllUsers(users));
    return () => {};
  }, []);

  return (
    <Popup
      trigger={
        <button className="absolute top-8 bg-gray-400 p-4 rounded-lg">
          ADD USERS
        </button>
      }
      overlayStyle={{
        position: "fixed",
        width: "100%",
        height: "100%",
        top: 0,
        left: 0,
        backgroundColor: "#fff",
        opacity: "80%",
      }}
    >
      <div className="fixed w-1/2 h-1/2 top-1/2 left-1/2 bg-gray-200 -translate-x-1/2 -translate-y-1/2">
        <div className="flex flex-col h-full justify-center items-center">
          <input
            type="text"
            className="w-1/2 mb-4"
            onChange={(input) => setSearchTerm(input.target.value)}
          />
          <ul className="overflow-y-scroll w-1/2 h-1/2 mb-8">
            {searchTerm.length === 0 &&
              allUsers.map((user, i) => {
                if (user.name === cookies.name) return;
                return (
                  <li key={i} className="list-none">
                    <button
                      className="border-black border rounded w-6 h-6"
                      onClick={(event: any) => {
                        event.target.classList.toggle("bg-black");
                        if (selectedUsers.includes(user)) {
                          const existingUserIndex = selectedUsers.indexOf(user);
                          setSelectedUsers((existingUsers) => [
                            ...existingUsers.splice(0, existingUserIndex),
                            ...existingUsers.splice(
                              existingUserIndex + 1,
                              existingUsers.length
                            ),
                          ]);
                        } else
                          setSelectedUsers((existingUsers) => [
                            ...existingUsers,
                            user,
                          ]);
                      }}
                    ></button>
                    <span className="ml-4 text-lg">{user.name}</span>
                  </li>
                );
              })}

            {searchTerm.length !== 0 &&
              fuse.search(searchTerm).map((user, i) => {
                if (user.item.name === cookies.name) return;
                return (
                  <li key={i} className="list-none">
                    <button
                      className="border-black border rounded w-6 h-6"
                      onClick={(event: any) => {
                        event.target.classList.toggle("bg-black");
                      }}
                    ></button>
                    <span className="ml-4 text-lg">{user.item.name}</span>
                  </li>
                );
              })}
          </ul>

          <button onClick={sendInvites}>ADD USERS</button>
        </div>
      </div>
    </Popup>
  );
};

const TextChat: FC<StateProps & { WSClient: WebSocket | undefined }> = ({
  states,
  WSClient,
}) => {
  const [cookies] = useCookies(["jwt", "name"]);
  const [atOldestMessage, setAtOldestMessage] = useState<boolean>(false);
  const inputRef = useRef<HTMLInputElement>(null);
  const scrollDiv = useRef<HTMLDivElement>(null);

  const AuthHeaders = {
    Authorization: `Bearer ${cookies.jwt}`,
  };

  const checkIfEnterKeyPressed = (e: React.KeyboardEvent): void => {
    if (e.key !== "Enter") return;
    addPostToChat();
  };

  const getMorePosts = (): void => {
    const lastPostID = states.posts[states.posts.length - 1].id;
    if (lastPostID === null) return;
    fetch(
      `http://localhost:8080/messages/get?postID=${lastPostID - 1}&groupID=${
        states.currentGroup.id
      }`,
      {
        headers: AuthHeaders,
      }
    )
      .then((response) => response.json())
      .then((newPosts) =>
        states.setPosts((prevPosts) => [...prevPosts, ...newPosts.reverse()])
      );
  };

  const addPostToChat = () => {
    if (states.currentGroup.id !== -1) {
      WSClient!.send(
        "1/" +
          JSON.stringify({
            message: inputRef.current!.value,
            senderUsername: cookies.name,
            isOnline: true,
            senderGroupID: states.currentGroupRef.current.id,
          })
      );
    } else {
      const otherUser: string | null = states.users.find((user) => {
        if (user.name !== cookies.name) return user;
      })!.name;
      WSClient!.send(
        "2/" +
          JSON.stringify({
            message: inputRef.current!.value,
            senderUsername: cookies.name,
            isOnline: true,
            senderGroupID: states.currentGroupRef.current.id,
            userToDM: otherUser,
          })
      );
    }
  };

  const getPostsOnMount = (): void => {
    fetch(`http://localhost:8080/messages?groupID=${states.currentGroup.id}`, {
      headers: AuthHeaders,
    })
      .then((response) => response.json())
      .then((postsFromResponse) => {
        states.setPosts(postsFromResponse);
      });
  };

  // Check whether infinite scroll has reached the end. That is, there are
  // no more posts to get from the statesbase.
  useEffect(() => {
    if (
      states.posts.length !== 0 &&
      states.posts[states.posts.length - 1].id === 1
    )
      setAtOldestMessage(true);
    return () => {};
  }, [states.posts]);

  useEffect(() => {
    if (states.currentGroup.id === -1) {
      states.setPosts([]);
      return;
    }
    getPostsOnMount();
    return () => {};
  }, [states.currentGroup]);

  // Move scrollbar to bottom of the text chat on mount
  useLayoutEffect(() => {
    scrollDiv.current!.scrollTop = scrollDiv.current!.scrollHeight;

    return () => {};
  }, []);

  return (
    <div className="h-full w-3/5 flex flex-col">
      <div className="h-full relative flex flex-col-reverse mb-40">
        <div className="w-full justify-center bottom-0">
          <input
            type="text"
            className="h-14 w-full rounded-lg text-black text-center text-xl font-mono"
            onKeyDown={checkIfEnterKeyPressed}
            ref={inputRef}
          />
        </div>
        {states.currentGroup.owner === cookies.name && (
          <AddUsersPopup
            WSClient={WSClient}
            currentGroup={states.currentGroup}
          />
        )}
        <div
          id="scroll"
          ref={scrollDiv}
          className="basis-0 grow shrink flex flex-col-reverse w-full m-auto mt-32 mb-20 pr-4 overflow-y-scroll"
        >
          <InfiniteScroll
            next={getMorePosts}
            dataLength={states.posts.length}
            hasMore={!atOldestMessage}
            inverse={true}
            loader={<h1>LOADING MORE POSTS</h1>}
            scrollableTarget="scroll"
            className="flex flex-col-reverse"
          >
            {states.posts.map((post, i) => (
              <Post post={post} key={i} />
            ))}
          </InfiniteScroll>
        </div>
      </div>
    </div>
  );
};

export default TextChat;
