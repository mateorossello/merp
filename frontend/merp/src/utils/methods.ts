import { useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import type { JwtPayload } from "../types/Auth";
import axios from "axios";

// Methods that return values contained in the token

const handleTokenError = (error: unknown) => {
  console.error(
    "Error decoding the token: ",
    error,
    ", contact the administrator",
  );
  return null;
};

const getDecodedToken = (): JwtPayload | null => {
  const token = localStorage.getItem("token");
  if (!token) return null;

  try {
    return jwtDecode<JwtPayload>(token);
  } catch (error) {
    return handleTokenError(error);
  }
};

export const getCurrentUsername = (): string | null => {
  const decoded = getDecodedToken();
  return decoded?.sub || null;
};

export const getCurrentProfile = (): string | null => {
  const decoded = getDecodedToken();
  return decoded?.profile || null;
};

export const getCurrentTasks = (): string[] | null => {
  const decoded = getDecodedToken();
  return decoded?.tasks || null;
};

export const getIssuedAtTime = (): number | null => {
  const decoded = getDecodedToken();
  return decoded?.iat || null;
};

export const getExpirationTime = (): number | null => {
  const decoded = getDecodedToken();
  return decoded?.exp || null;
};

export const isExpired = (): boolean => {
  const expirationTime = getExpirationTime();
  return expirationTime ? Date.now() >= expirationTime * 1000 : true;
};

// Method that returns the first error in a list of errors

export const extractFirstError = (error: unknown): string => {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data;

    if (typeof data === "object" && data !== null) {
      const errors = Object.values(data);
      if (errors.length > 0) {
        return String(errors[0]);
      }
    }

    if (typeof data === "string" && data.trim() !== "") {
      return data;
    }

    return error.message;
  }

  return "Unknown validation error, contact the administrator";
};

// Method that returns if the user is authorized to perform a action

export function usePermissions() {
  const [tasks, setTasks] = useState<string[]>(() => getCurrentTasks() || []);

  const updatePermissions = () => {
    const expirationTime = getExpirationTime();
    const isExpired = expirationTime
      ? Date.now() >= expirationTime * 1000
      : true;

    if (isExpired) {
      setTasks([]);
      localStorage.clear();
      window.location.href = "/";
      return;
    }

    const currentTasks = getCurrentTasks();
    setTasks(currentTasks || []);
  };

  useEffect(() => {
    window.addEventListener("storage", updatePermissions);
    return () => window.removeEventListener("storage", updatePermissions);
  }, []);

  const hasPermission = (task: string) => tasks.includes(task);

  return { tasks, hasPermission };
}
