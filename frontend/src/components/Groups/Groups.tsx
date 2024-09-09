import { FC, useRef } from "react";
import { useCookies } from "react-cookie";
import Popup from "reactjs-popup";
import Group from "./Group";
import { GroupT, StateProps } from "../../interfaces";

const AddGroupPopup = ({
  inputRef,
  createNewGroup,
}: {
  inputRef: React.RefObject<HTMLInputElement>;
  createNewGroup: () => void;
}) => {
  return (
    <Popup
      trigger={
        <button className="absolute w-full left-0 top-0 mb-2 bg-gray-400 p-4 rounded-lg">
          MAKE NEW GROUP
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
          <h1 className="mb-4 text-5xl">Group name: </h1>
          <input
            ref={inputRef}
            className="w-1/3 p-2 rounded-md mb-12"
            type="text"
            name=""
            id=""
          />
          <button onClick={createNewGroup}>CREATE GROUP</button>
        </div>
      </div>
    </Popup>
  );
};

const Groups: FC<StateProps> = ({ states }) => {
  const [cookies] = useCookies(["jwt", "name"]);
  const inputRef = useRef<HTMLInputElement>(null);

  const createNewGroup = () => {
    // send websocket message to create new group
    fetch("http://localhost:8080/groups", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${cookies.jwt}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username: cookies.name,
        groupName: inputRef.current!.value,
      }),
    })
      .then((response) => response.json())
      .then((newGroup) => {
        states.setGroups((existingGroups: GroupT[]) => [
          ...existingGroups,
          newGroup,
        ]);
      });
  };

  return (
    <div className="overflow-y-scroll flex-1 bg-gray-200 relative pt-16 m-8">
      <ul>
        {states.groups.map((group, i) => (
          <Group key={i} group={group} states={states} />
        ))}
      </ul>
      <AddGroupPopup inputRef={inputRef} createNewGroup={createNewGroup} />
    </div>
  );
};

export default Groups;
