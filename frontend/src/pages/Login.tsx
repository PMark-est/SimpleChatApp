import { useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useCookies } from "react-cookie";

const Login = () => {
  const usernameInputRef = useRef<HTMLInputElement>(null);
  const passwordInputRef = useRef<HTMLInputElement>(null);
  const [cookies, setCookie] = useCookies(["jwt", "name"]);
  const navigate = useNavigate();
  const MyHeaders = {
    Accept: "application/json",
    "Content-Type": "application/json",
  };

  const MyBody = () => {
    return JSON.stringify({
      name: usernameInputRef.current!.value,
      password: passwordInputRef.current!.value,
    });
  };

  const showError = (errorMessage: string) => {
    //todo
    console.log(errorMessage);
  };

  const redirect = (response: Response) => {
    response.json().then((token) => {
      setCookie("jwt", token.token, {
        path: "/",
        expires: new Date(Date.now() + 24 * 60 * 60 * 1000),
      });
      setCookie("name", usernameInputRef.current!.value);
      navigate("/");
    });
  };

  const attemptLogin = async () => {
    const response = await fetch("http://localhost:8080/auth/login", {
      method: "POST",
      headers: MyHeaders,
      body: MyBody(),
    });
    if (response.status !== 200) {
      showError("No such user or wrong password");
      return;
    }
    redirect(response);
  };

  return (
    <>
      <h1 className="absolute w-full text-center text-6xl mt-20 ml-36 text-orange">
        Hey, let's start chatting
      </h1>
      <div className="flex w-full h-full">
        <div className="grid grid-cols-3 w-full h-full items-center">
          <div className="flex flex-col items-center bg-slate-100 mx-16 p-12">
            <input
              ref={usernameInputRef}
              type="text"
              placeholder="Username"
              className="h-12 rounded-lg text-black text-center text-xl font-mono mb-6"
            />
            <input
              ref={passwordInputRef}
              type="text"
              placeholder="Password"
              className="h-12 rounded-lg text-black text-center text-xl font-mono mb-8"
            />
            <button
              className="bg-orange-300 px-8 h-12 rounded-lg text-black hover:bg-slate-400 mb-6"
              onClick={attemptLogin}
            >
              LOGIN
            </button>
            <button
              className="bg-orange-600 px-8 h-12 rounded-lg text-black hover:bg-slate-400"
              onClick={() => {
                navigate("/register");
              }}
            >
              CREATE NEW ACCOUNT
            </button>
          </div>
        </div>
        <div className=""></div>
      </div>
    </>
  );
};

export default Login;
