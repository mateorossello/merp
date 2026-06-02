import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import type { Profile } from "../../../types/access/Profile";
import api from "../../../utils/api";
import { extractFirstError } from "../../../utils/methods";

function CreateUsers() {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [profiles, setProfiles] = useState<Profile[]>([]);
  const [profileId, setProfileId] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [result, setResult] = useState("");
  const [isSuccess, setIsSuccess] = useState<boolean | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    document.title = "MERP - Create Users";

    const fetchProfiles = async () => {
      try {
        const response = await api.get("/profiles");

        const normalizedProfiles = (
          Array.isArray(response.data) ? response.data : []
        ).map((profile) => ({
          ...profile,
          tasks: Array.isArray(profile.tasks) ? profile.tasks : [],
        }));

        setProfiles(normalizedProfiles);
      } catch (error: unknown) {
        setResult(extractFirstError(error));
        setIsSuccess(false);
      }
    };

    fetchProfiles();
  }, []);

  const createUser = async (event: React.SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    setResult("");
    setIsSuccess(null);

    if (password !== confirmPassword) {
      setResult("The passwords do not match");
      setIsSuccess(false);
      return;
    }

    const newUser = {
      username: username,
      password: password,
      profileId: profileId ? parseInt(profileId) : null,
    };

    setIsSaving(true);

    try {
      await api.post("/users", newUser);

      setResult("User created successfully");
      setIsSuccess(true);
      setUsername("");
      setPassword("");
      setConfirmPassword("");
      setProfileId("");
    } catch (error: unknown) {
      setResult(extractFirstError(error));
      setIsSuccess(false);
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-3xl font-extrabold text-gray-800">Create Users</h1>

        <button
          onClick={() => navigate("/manage-users")}
          disabled={isSaving}
          className="text-gray-500 hover:text-primary flex items-center gap-1 font-medium cursor-pointer transition-colors"
        >
          <span className="material-icons">arrow_back</span> Return
        </button>
      </div>

      <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 max-w-2xl mx-auto">
        <form onSubmit={createUser} className="flex flex-col gap-6">
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Username
            </label>

            <input
              name="username"
              type="text"
              placeholder="Username"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              disabled={isSaving}
              required
              className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors"
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                Password
              </label>

              <input
                name="password"
                type="password"
                placeholder="Password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                disabled={isSaving}
                required
                className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                Confirm Password
              </label>

              <input
                name="confirmPassword"
                type="password"
                placeholder="Confirm Password"
                value={confirmPassword}
                onChange={(event) => setConfirmPassword(event.target.value)}
                disabled={isSaving}
                required
                className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Profile
            </label>

            <select
              name="profile"
              value={profileId}
              onChange={(event) => setProfileId(event.target.value)}
              disabled={isSaving}
              required
              className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors cursor-pointer"
            >
              <option value="" disabled>
                Profile
              </option>

              {profiles.map((profile) => (
                <option key={profile.id} value={profile.id}>
                  {profile.name}
                </option>
              ))}
            </select>
          </div>

          <button
            type="submit"
            disabled={isSaving}
            className="mt-4 w-full bg-primary hover:bg-primary-dark text-white font-bold py-4 rounded-xl shadow-md transition-colors duration-300 cursor-pointer flex justify-center items-center gap-2"
          >
            {isSaving ? (
              <span className="material-icons animate-spin text-sm">sync</span>
            ) : (
              <span className="material-icons">save</span>
            )}{" "}
            Save
          </button>
        </form>
      </div>

      {result && (
        <div
          className={`max-w-md mx-auto mt-4 p-4 mb-6 rounded-xl font-medium text-center ${isSuccess ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"}`}
        >
          {result}
        </div>
      )}
    </div>
  );
}

export default CreateUsers;
