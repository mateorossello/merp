import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import type { AccountOutput } from "../../../types/accounting/AccountOutput";
import api from "../../../utils/api";
import { extractFirstError } from "../../../utils/methods";

function ShowAccounts() {
  const navigate = useNavigate();
  const [accounts, setAccounts] = useState<AccountOutput[]>([]);
  const [result, setResult] = useState("");
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    document.title = "MERP - Show Accounts";

    const fetchAccounts = async () => {
      try {
        const response = await api.get("/accounts");
        const sortedData = response.data.sort(
          (a: AccountOutput, b: AccountOutput) => a.code.localeCompare(b.code),
        );
        setAccounts(sortedData);
      } catch (error: unknown) {
        setResult(extractFirstError(error));
      } finally {
        setIsLoading(false);
      }
    };

    fetchAccounts();
  }, []);

  return (
    <div className="max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-3xl font-extrabold text-gray-800">Show Accounts</h1>

        <button
          onClick={() => navigate("/manage-accounts")}
          className="text-gray-500 hover:text-primary flex items-center gap-1 font-medium cursor-pointer transition-colors"
        >
          <span className="material-icons">arrow_back</span> Return
        </button>
      </div>

      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-gray-50 text-gray-800 border-b border-gray-100 text-sm uppercase tracking-wider">
              <th className="p-6 font-semibold">Code</th>

              <th className="p-6 font-semibold">Name</th>

              <th className="p-6 font-semibold">Type</th>

              <th className="p-6 font-semibold">Status</th>

              <th className="p-6 font-semibold">Description</th>
            </tr>
          </thead>

          <tbody className="divide-y divide-gray-100">
            {accounts.map((account) => (
              <tr
                key={account.id}
                className="hover:bg-gray-50 transition-colors"
              >
                <td className="p-6 text-gray-600 font-medium">
                  {account.code}
                </td>

                <td className="p-6 text-gray-800 font-medium">
                  {account.name}
                </td>

                <td className="p-6 text-gray-800 font-medium">
                  <span className="px-2 py-1 bg-gray-100 text-gray-600 rounded-md font-bold uppercase tracking-tight">
                    {account.type.replace("_", " ")}
                  </span>
                </td>

                <td className="p-6 text-gray-800 font-medium">
                  <span
                    className={`px-2 py-1 ${account.state ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"} rounded-md font-bold uppercase tracking-tight`}
                  >
                    {account.state ? "Active" : "Inactive"}
                  </span>
                </td>

                <td className="p-6 text-gray-800 font-medium">
                  {account.description || "No description"}
                </td>
              </tr>
            ))}

            {accounts.length === 0 && (
              <tr>
                <td colSpan={5} className="p-8 text-center text-gray-600">
                  There are no registered accounts
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {result && (
        <div
          className={`max-w-md mx-auto mt-4 p-4 mb-6 rounded-xl font-medium text-center ${isLoading ? "" : "bg-red-100 text-red-700"}`}
        >
          {result}
        </div>
      )}

      <div className="flex justify-end mt-6">
        <div className="bg-blue-50 text-primary px-6 py-2.5 rounded-xl font-bold border border-blue-100 shadow-sm flex items-center gap-2">
          <span className="text-sm opacity-70 font-medium">Total Accounts</span>

          <span className="text-lg">{accounts.length}</span>
        </div>
      </div>
    </div>
  );
}

export default ShowAccounts;
