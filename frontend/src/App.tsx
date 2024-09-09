import { BrowserRouter, Route, Routes } from "react-router-dom";
import Chatroom from "./pages/Chatroom";
import Login from "./pages/Login";
import ProtectedRoutes from "./components/ProtectedRoutes";
import { useCookies } from "react-cookie";
import Register from "./pages/Register";

function App() {
  const [cookies] = useCookies(["jwt", "name"]);

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" Component={Login} />
        <Route path="/register" Component={Register} />
        <Route
          path=""
          element={
            <ProtectedRoutes>
              <Chatroom />
            </ProtectedRoutes>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
