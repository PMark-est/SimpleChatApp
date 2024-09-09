import { FC, useState } from "react";
import { StateProps } from "../../interfaces";
import User from "./User";

const Users: FC<StateProps> = ({ states }) => {
  const [beingHovered, setBeingHovered] = useState<boolean>(false);
  const [hoveredElementID, setHoveredElementID] = useState<number>(0);

  const HoverStates = {
    beingHovered: beingHovered,
    setBeingHovered: setBeingHovered,
    hoveredElementID: hoveredElementID,
    setHoveredElementID: setHoveredElementID,
  };

  return (
    <ul className="overflow-y-scroll bg-gray-200 m-8 flex-1">
      {states.users.map((user, i) => (
        <User
          key={i}
          id={i}
          states={states}
          hoverStates={HoverStates}
          user={user}
        />
      ))}
    </ul>
  );
};

export default Users;
