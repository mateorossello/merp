import { Navigate, Outlet } from "react-router-dom";
import { isExpired } from "../utils/methods";

const PublicRoute = () => {
  if (!isExpired()) {
    return <Navigate to="/main-menu" replace />;
  }

  return <Outlet />;
};

export default PublicRoute;
