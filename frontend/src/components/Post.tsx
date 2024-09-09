import { decode } from "html-entities";
import { PostT } from "../interfaces";

const Post = ({ post }: { post: PostT }) => {
  return (
    <div className="w-full bg-slate-100 p-4 mb-2 rounded-lg">
      <h1 className="text-2xl w-min border-2 border-gray-400 px-4 py-1 rounded-lg">
        <b>{post.username}</b>
      </h1>
      <p className="pt-4 pl-1 text-lg">{decode(post.content)}</p>
    </div>
  );
};

export default Post;
