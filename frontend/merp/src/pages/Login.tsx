import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../utils/api";
import { extractFirstError } from "../utils/methods";

function Login() {
  const navigate = useNavigate();
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [result, setResult] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);

  useEffect(() => {
    document.title = "MERP - Login";
    localStorage.clear();
  }, []);

  const authenticate = async (event: React.SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    setResult("");
    setLoading(true);

    try {
      const response = await api.post("/authentication/login", {
        username,
        password,
      });

      const token = response.data.token;
      localStorage.setItem("token", token);

      navigate("/main-menu");
    } catch (error) {
      const errorMessage = extractFirstError(error);
      setResult(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <form
        onSubmit={authenticate}
        className="bg-white p-8 rounded-2xl shadow-md w-full max-w-md"
      >
        <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">
          MERP
        </h2>

        <div className="mb-4">
          <input
            type="text"
            placeholder="Username"
            autoComplete="username"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
            required
            className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-colors"
          />
        </div>

        <div className="mb-6">
          <input
            type="password"
            placeholder="Password"
            autoComplete="current-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
            className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className={`w-full bg-primary text-white font-bold py-4 rounded-xl shadow-md transition-colors duration-300 cursor-pointer hover:bg-primary-dark ${
            loading ? "opacity-70 cursor-not-allowed" : ""
          }`}
        >
          {loading ? "Logging in..." : "Log in"}
        </button>

        {result && (
          <p className="text-red-500 text-sm text-center mt-4 font-medium animate-fadeIn">
            {result}
          </p>
        )}
      </form>
    </div>
  );
}

export default Login;
