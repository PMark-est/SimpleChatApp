import { useRef, useState, useEffect } from "react";
import { useCookies } from "react-cookie";
import Users from "../components/Users/Users";
import Groups from "../components/Groups/Groups";
import TextChat from "../components/TextChat";
import { GroupT, UserT, PostT } from "../interfaces";
import { config } from "process";

const Chatroom = () => {
  const [cookies] = useCookies(["jwt", "name"]);
  const [posts, setPosts] = useState<PostT[]>([]);
  const [WSClient, setWSClient] = useState<WebSocket>();
  const [groups, setGroups] = useState<GroupT[]>([]);
  const [currentGroup, setCurrentGroup] = useState<GroupT>({
    id: 1,
    name: "my chat",
    owner: "admin",
    userToDM: null,
  });
  const [users, setUsers] = useState<UserT[]>([]);
  const usersRef = useRef<UserT[]>([]);
  const currentGroupRef = useRef<GroupT>({
    id: 1,
    name: "my chat",
    owner: "admin",
    userToDM: null,
  });

  const States = {
    users: users,
    setUsers: setUsers,
    usersRef: usersRef,
    posts: posts,
    setPosts: setPosts,
    groups: groups,
    setGroups: setGroups,
    currentGroup: currentGroup,
    setCurrentGroup: setCurrentGroup,
    currentGroupRef: currentGroupRef,
  };

  const AuthHeaders = {
    Authorization: `Bearer ${cookies.jwt}`,
  };
  useEffect(() => {
    const client = new WebSocket(`ws://localhost:8080/myHandler`);
    setWSClient(client);

    client.onmessage = (message) => {
      // 1 - message
      // 2 - dm
      // 3 - invite
      // 4 - connection
      // 5 - connections of users that that owner of a group added
      const parts: string[] = message.data.split("/");
      const type: number = Number(parts[0]);
      var parsedMessage = JSON.parse(parts[1]);
      if (type !== 3 && type !== 5) parsedMessage = parseValues(parsedMessage);
      if (type === 1) {
        handleMessage(parsedMessage);
      } else if (type === 2) {
        handleDM(parsedMessage);
      } else if (type === 3) {
        handleInvite(parsedMessage);
      } else if (type === 4) {
        handleUserConnect(parsedMessage);
      } else if (type === 5) {
        notifyOwnerNewUsersConnected(parsedMessage);
      }
    };

    client.onopen = (event) => {
      // tee nii, et iga kord ei peaks id-d vaatama andmebaasist
      getUsersOnMount();
      getGroupsOnMount();
    };
  }, []);

  useEffect(() => {
    usersRef.current = users;

    return () => {};
  }, [users]);

  const parseValues = (msg: any) => {
    msg = {
      ...msg,
      senderGroupID: Number(msg.senderGroupID),
      senderID: Number(msg.senderID),
      messageID: Number(msg.messageID),
      isOnline: msg.isOnline === "true",
    };
    return msg;
  };

  const notifyOwnerNewUsersConnected = (msg: any) => {
    for (let key in msg.invitedUsers) {
      setUsers((existingUsers) => [...existingUsers, msg.invitedUsers[key]]);
    }
  };

  const handleDM = (msg: any) => {
    const newGroup: GroupT = {
      id: msg.senderGroupID,
      name: msg.senderUsername,
      owner: null,
      userToDM: msg.userToDM,
    };

    if (currentGroupRef.current.id === -1) setCurrentGroup(newGroup);
    setGroups((existingGroups) => [...existingGroups, newGroup]);

    if (msg.senderUsername === cookies.name) currentGroupRef.current = newGroup;

    if (currentGroupRef.current.id !== msg.senderGroupID) {
      // mingisugune notifcation systeem teha, et naidata
      // mitu lugemata sonumit on. voi akki et lihtsalt on lugemata sonumeid?

      return;
    }

    const newPost: PostT = {
      id: msg.messageID,
      username: msg.username,
      content: msg.message,
    };

    setPosts((prevPosts) => [newPost, ...prevPosts]);
  };

  const handleInvite = (msg: any) => {
    const newGroup: GroupT = {
      id: msg.groupID,
      name: msg.groupName,
      owner: msg.owner,
      userToDM: null,
    };
    setGroups((existingGroups) => [...existingGroups, newGroup]);
  };

  const handleMessage = (msg: any) => {
    if (currentGroupRef.current.id !== msg.senderGroupID) {
      // mingisugune notifcation systeem teha, et naidata
      // mitu lugemata sonumit on. voi akki et lihtsalt on lugemata sonumeid?
      // NAH
      // console.log("pole sulle");
      return;
    }

    const newPost: PostT = {
      id: msg.messageID,
      username: msg.senderUsername,
      content: msg.message,
    };
    setPosts((prevPosts) => [newPost, ...prevPosts]);
  };

  const handleUserConnect = (msg: any) => {
    const userIndex = usersRef.current.findIndex(
      (user) => user.id === msg.senderID
    );

    if (userIndex === -1) {
      const newUser: UserT = {
        id: msg.senderID,
        name: msg.senderUsername,
        isOnline: msg.isOnline,
      };
      setUsers((prevUsers) => [...prevUsers, newUser]);
    } else {
      setUsers((prevUsers) => [
        ...prevUsers.slice(0, userIndex),
        { ...prevUsers[userIndex], isOnline: msg.isOnline },
        ...prevUsers.slice(userIndex + 1, prevUsers.length),
      ]);
    }
  };

  const getUsersOnMount = (): void => {
    fetch(`http://localhost:8080/users/id?groupID=${currentGroup.id}`, {
      headers: AuthHeaders,
    })
      .then((response) => response.json())
      .then((usersFromResponse) => {
        usersRef.current = usersFromResponse;
        setUsers(usersFromResponse);
      });
  };

  const getGroupsOnMount = (): void => {
    fetch(`http://localhost:8080/groups/all?username=${cookies.name}`, {
      headers: AuthHeaders,
    })
      .then((response) => response.json())
      .then((groupsFromResponse) => {
        setGroups(groupsFromResponse);
      });
  };

  return (
    <div className="h-full flex gap-32">
      <Groups states={States} />
      <TextChat states={States} WSClient={WSClient} />
      <Users states={States} />
    </div>
  );
};

export default Chatroom;
