import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import type { AccountOutput } from "../../types/accounting/AccountOutput";
import api from "../../utils/api";
import { extractFirstError } from "../../utils/methods";

function DeleteAccounts() {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState("");
  const [foundAccount, setFoundAccount] = useState<AccountOutput | null>(null);
  const [result, setResult] = useState("");
  const [isSuccess, setIsSuccess] = useState<boolean | null>(null);
  const [isSearching, setIsSearching] = useState(false);

  useEffect(() => {
    document.title = "MERP - Delete Accounts";
  }, []);

  const handleSearch = async (event: React.SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();

    setResult("");
    setFoundAccount(null);
    setIsSuccess(null);

    if (searchQuery.trim() === "") {
      setResult("Please enter a valid account code or name");
      setIsSuccess(false);
      return;
    }

    setIsSearching(true);

    const isCode = /^[0-9.]+$/.test(searchQuery.trim());

    const safeSearchQuery = encodeURIComponent(searchQuery.trim());

    const url = isCode
      ? `/accounts/code/${safeSearchQuery}`
      : `/accounts?name=${safeSearchQuery}`;

    try {
      const response = await api.get(url);

      const account = isCode ? response.data : response.data[0];
      setFoundAccount(account);

      setFoundAccount(account);

      setResult("Account found");
      setIsSuccess(true);
    } catch (error: unknown) {
      setResult(extractFirstError(error));
      setIsSuccess(false);
    } finally {
      setIsSearching(false);
    }
  };

  const handleCancel = () => {
    setFoundAccount(null);
    setSearchQuery("");
    setFoundAccount(null);
    setResult("Edition cancelled");
    setIsSuccess(null);
  };

  const handleDelete = async (id: number) => {
    try {
      await api.delete(`/accounts/${id}`);

      setResult("Account successfully deleted");
      setIsSuccess(true);
      setFoundAccount(null);
      setSearchQuery("");
    } catch (error: unknown) {
      setResult(extractFirstError(error));
      setIsSuccess(false);
    }
  };

  return (
    <div className="max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-3xl font-extrabold text-gray-800">
          Delete Accounts
        </h1>

        <button
          onClick={() => navigate("/manage-accounts")}
          className="text-gray-500 hover:text-primary flex items-center gap-1 font-medium cursor-pointer transition-colors"
        >
          <span className="material-icons">arrow_back</span> Return
        </button>
      </div>

      <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 max-w-2xl mx-auto">
        <form onSubmit={handleSearch} className="flex flex-col gap-6">
          <div className="flex-1 flex flex-col gap-2">
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Search
            </label>

            <div className="relative">
              <span className="material-icons absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400">
                search
              </span>

              <input
                name="search"
                type="text"
                placeholder="Account code or name"
                value={searchQuery}
                onChange={(event) => setSearchQuery(event.target.value)}
                disabled={foundAccount !== null}
                className={`w-full px-4 py-3 pl-10 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors ${isSearching || foundAccount !== null ? "cursor-not-allowed" : ""}`}
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={isSearching || foundAccount !== null}
            className={`w-full py-3 rounded-xl font-bold flex justify-center items-center gap-2 transition-colors mb-6 ${
              isSearching || foundAccount !== null
                ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                : "bg-primary hover:bg-primary-dark text-white cursor-pointer"
            }`}
          >
            {isSearching ? (
              <span className="material-icons animate-spin text-sm">sync</span>
            ) : (
              <span className="material-icons text-sm">search</span>
            )}
            Search
          </button>
        </form>

        {foundAccount && (
          <div className="border-2 border-red-100 rounded-2xl overflow-hidden bg-white">
            <div className="bg-red-50 p-4 border-b border-red-100 flex items-center gap-2">
              <span className="material-icons text-red-500">warning_amber</span>

              <h3 className="text-red-800 font-bold">
                Ready for permanent deletion
              </h3>
            </div>

            <div className="p-6">
              <div className="grid grid-cols-2 gap-6">
                <div>
                  <p className="block text-sm font-semibold text-gray-700 mb-2">
                    Code
                  </p>

                  <p className="cursor-not-allowed w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors">
                    {foundAccount.code}
                  </p>
                </div>

                <div>
                  <p className="block text-sm font-semibold text-gray-700 mb-2">
                    Name
                  </p>

                  <p className="cursor-not-allowed w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors">
                    {foundAccount.name}
                  </p>
                </div>

                <div>
                  <p className="block text-sm font-semibold text-gray-700 mb-2">
                    Type
                  </p>

                  <p className="cursor-not-allowed w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors">
                    {foundAccount.type}
                  </p>
                </div>

                <div>
                  <p className="block text-sm font-semibold text-gray-700 mb-2">
                    Status
                  </p>

                  <span className="cursor-not-allowed w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors flex items-center gap-2">
                    <span
                      className={`w-2 h-2 rounded-full ${foundAccount.state ? "bg-green-500" : "bg-red-500"}`}
                    ></span>
                    {foundAccount.state ? "Active" : "Inactive"}
                  </span>
                </div>
              </div>

              <div className="flex gap-4 mt-6">
                <button
                  type="button"
                  onClick={handleCancel}
                  className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-700 font-bold py-4 rounded-xl shadow-md transition-colors duration-300 cursor-pointer flex justify-center items-center gap-2"
                >
                  Cancel
                </button>

                <button
                  onClick={() => handleDelete(foundAccount.id)}
                  className="flex-1 bg-red-500 hover:bg-red-600 text-white font-bold py-4 rounded-xl shadow-md transition-colors duration-300 cursor-pointer flex justify-center items-center gap-2"
                >
                  <span className="material-icons text-sm">delete_forever</span>
                  Delete
                </button>
              </div>
            </div>
          </div>
        )}
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

export default DeleteAccounts;
