import { Outlet, Navigate } from "react-router-dom";
import { useCookies } from "react-cookie";

const ProtectedRoutes = ({ children }: { children: any }) => {
  const [cookies] = useCookies(["jwt", "name"]);
  return cookies.jwt !== undefined ? children : <Navigate to="/login" />;
};

export default ProtectedRoutes;
