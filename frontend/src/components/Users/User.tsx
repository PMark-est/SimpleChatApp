import { FC, MutableRefObject } from "react";
import { useCookies } from "react-cookie";
import { GoDotFill } from "react-icons/go";
import { GoDot } from "react-icons/go";
import { GroupT, StateProps, UserT } from "../../interfaces";

const UserIsOnlineIcon = ({ isOnline }: { isOnline: boolean | null }) => {
  if (isOnline) return <GoDotFill />;
  return <GoDot />;
};

const User: FC<
  StateProps & {
    user: UserT;
    id: number;
    hoverStates: {
      beingHovered: boolean;
      setBeingHovered: React.Dispatch<React.SetStateAction<boolean>>;
      hoveredElementID: number;
      setHoveredElementID: React.Dispatch<React.SetStateAction<number>>;
    };
  }
> = ({ states, hoverStates, user, id }) => {
  const [cookies] = useCookies(["jwt", "name"]);

  // For sending direct messages
  const sendMessage = () => {
    fetch(
      `http://localhost:8080/groups/dmExists?username1=${cookies.name}&username2=${user.name}`,
      {
        headers: {
          Authorization: `Bearer ${cookies.jwt}`,
        },
      }
    )
      .then((response) => {
        if (!response.ok) return null;
        return response.json();
      })
      .then((existingGroup) => {
        states.setUsers((existingUsers) => [
          ...existingUsers.filter((eUser) => {
            if (eUser.name === cookies.name) return eUser;
            if (eUser.name === user.name) return eUser;
          }),
        ]);
        if (existingGroup !== null) {
          states.setCurrentGroup(existingGroup);
          states.currentGroupRef.current = existingGroup;
          return;
        }
        const dmGroup: GroupT = {
          id: -1, // new dm group
          name: null,
          owner: null,
          userToDM: user.name,
        };
        states.setCurrentGroup(dmGroup);
        states.currentGroupRef.current = dmGroup;
      });
  };

  return (
    <>
      {user.isOnline ? (
        <li
          className="min-h-14 w-full bg-gray-500 text-2xl flex flex-col items-center justify-center mb-1"
          onMouseEnter={() => {
            hoverStates.setHoveredElementID(id);
            hoverStates.setBeingHovered(true);
          }}
          onMouseLeave={() => {
            hoverStates.setBeingHovered(false);
          }}
        >
          {user.name}
          {hoverStates.beingHovered && hoverStates.hoveredElementID === id && (
            <button onClick={sendMessage} className="text-sm">
              Direct message
            </button>
          )}
        </li>
      ) : (
        <li
          className="min-h-14 w-full bg-gray-300 text-2xl flex flex-col items-center justify-center mb-1"
          onMouseEnter={() => {
            hoverStates.setHoveredElementID(id);
            hoverStates.setBeingHovered(true);
          }}
          onMouseLeave={() => {
            hoverStates.setBeingHovered(false);
          }}
        >
          {user.name}
          {hoverStates.beingHovered && hoverStates.hoveredElementID === id && (
            <button onClick={sendMessage} className="text-sm">
              Direct message
            </button>
          )}
        </li>
      )}
    </>
  );
};

export default User;
