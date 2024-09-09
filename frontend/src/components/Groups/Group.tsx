import { FC } from "react";
import { useCookies } from "react-cookie";
import { GroupT, StateProps } from "../../interfaces";

const Group: FC<StateProps & { group: GroupT }> = ({ group, states }) => {
  const [cookies] = useCookies(["jwt", "name"]);
  const switchGroup = () => {
    group = {
      ...group,
      id: Number(group.id),
    };
    states.currentGroupRef.current = group;
    states.setCurrentGroup(group);
    fetch(`http://localhost:8080/users/id?groupID=${group.id}`, {
      headers: {
        Authorization: `Bearer ${cookies.jwt}`,
      },
    })
      .then((resp) => resp.json())
      .then((users) => {
        states.setUsers(users);
      });
  };

  return (
    <li>
      <button
        className="min-h-14 w-full bg-gray-300 text-2xl rounded-lg mb-0.5"
        onClick={switchGroup}
      >
        {group.name}
      </button>
    </li>
  );
};

export default Group;
