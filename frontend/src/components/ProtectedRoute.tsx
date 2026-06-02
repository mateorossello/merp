import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { isExpired, usePermissions } from "../utils/methods";

interface ProtectedRouteProps {
  children?: React.ReactNode;
  requiredTask?: string;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requiredTask,
}) => {
  const { hasPermission } = usePermissions();

  if (isExpired()) {
    return <Navigate to="/" />;
  }

  if (requiredTask && !hasPermission(requiredTask)) {
    return <Navigate to="/main-menu" />;
  }

  return children ? <>{children}</> : <Outlet />;
};

export default ProtectedRoute;
