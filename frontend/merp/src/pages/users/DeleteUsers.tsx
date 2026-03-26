import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import type { User } from "../../types/User";
import api from "../../utils/api";
import { getCurrentUsername, extractFirstError } from "../../utils/methods";

function DeleteUsers() {
  const navigate = useNavigate();
  const currentUsername = getCurrentUsername();
  const [users, setUsers] = useState<User[]>([]);
  const [result, setResult] = useState("");
  const [isSuccess, setIsSuccess] = useState<boolean | null>(null);

  useEffect(() => {
    document.title = "MERP - Delete Users";

    const fetchUsers = async () => {
      try {
        const response = await api.get("/users");
        setUsers(response.data);
      } catch (error: unknown) {
        setResult(extractFirstError(error));
        setIsSuccess(false);
      }
    };

    fetchUsers();
  }, []);

  const deleteUser = async (userId: number, username: string) => {
    try {
      await api.delete(`/users/${userId}`);

      setResult("User deleted successfully");
      setIsSuccess(true);
      setUsers(users.filter((user) => user.id !== userId));

      if (username === currentUsername) {
        localStorage.clear();
        navigate("/");
      }
    } catch (error: unknown) {
      setResult(extractFirstError(error));
      setIsSuccess(false);
    }
  };

  return (
    <div className="max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-3xl font-extrabold text-gray-800">Delete Users</h1>
        <button
          onClick={() => navigate("/manage-users")}
          className="text-gray-500 hover:text-primary flex items-center gap-1 font-medium cursor-pointer transition-colors"
        >
          <span className="material-icons">arrow_back</span> Return
        </button>
      </div>

      {result && (
        <div
          className={`p-4 mb-6 rounded-xl font-medium text-center ${isSuccess ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"}`}
        >
          {result}
        </div>
      )}

      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-gray-50 text-gray-800 border-b border-gray-100 text-sm uppercase tracking-wider">
              <th className="p-6 font-semibold">Id</th>
              <th className="p-6 font-semibold">Username</th>
              <th></th>
            </tr>
          </thead>

          <tbody className="divide-y divide-gray-100">
            {users.map((user) => (
              <tr key={user.id} className="hover:bg-gray-50 transition-colors">
                <td className="p-6 text-gray-600 font-medium">#{user.id}</td>

                <td className="p-6 text-gray-800 font-medium">
                  {user.username}
                </td>

                <td className="p-6 text-right">
                  <button
                    onClick={() => deleteUser(user.id, user.username)}
                    className="text-red-500 hover:text-red-700 hover:bg-red-50 px-4 py-2 rounded-xl transition-colors font-medium text-sm flex items-center justify-end gap-1 ml-auto cursor-pointer"
                  >
                    <span className="material-icons text-sm">delete</span>
                  </button>
                </td>
              </tr>
            ))}

            {users.length === 0 && (
              <tr>
                <td colSpan={3} className="p-8 text-center text-gray-600">
                  There are no registered users
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default DeleteUsers;
