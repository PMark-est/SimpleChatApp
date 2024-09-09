export interface GroupT {
  id: number | null;
  name: string | null;
  owner: string | null;
  userToDM: string | null;
}

export interface UserT {
  id: number | null;
  name: string | null;
  isOnline: boolean | null;
}

export interface PostT {
  id: number | null;
  username: string | null;
  content: string | null;
}

export type StateProps = {
  states: {
    users: UserT[];
    setUsers: React.Dispatch<React.SetStateAction<UserT[]>>;
    usersRef: React.MutableRefObject<UserT[]>;
    posts: PostT[];
    setPosts: React.Dispatch<React.SetStateAction<PostT[]>>;
    groups: GroupT[];
    setGroups: React.Dispatch<React.SetStateAction<GroupT[]>>;
    currentGroup: GroupT;
    setCurrentGroup: React.Dispatch<React.SetStateAction<GroupT>>;
    currentGroupRef: React.MutableRefObject<GroupT>;
  };
};
