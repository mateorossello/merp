import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import type { AccountOutput } from "../../../types/accounting/AccountOutput";
import api from "../../../utils/api";
import { extractFirstError } from "../../../utils/methods";

function EditAccounts() {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState("");
  const [foundAccount, setFoundAccount] = useState<AccountOutput | null>(null);
  const [formData, setFormData] = useState({
    name: "",
    description: "",
  });
  const [result, setResult] = useState("");
  const [isSuccess, setIsSuccess] = useState<boolean | null>(null);
  const [isSearching, setIsSearching] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    document.title = "MERP - Edit Accounts";
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

      setFormData({
        name: account.name,
        description: account.description,
      });

      setResult("Account found");
      setIsSuccess(true);
    } catch (error: unknown) {
      setResult(extractFirstError(error));
      setIsSuccess(false);
    } finally {
      setIsSearching(false);
    }
  };

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [event.target.name]: event.target.value,
    });
  };

  const handleCancel = () => {
    setFoundAccount(null);
    setSearchQuery("");
    setFormData({ name: "", description: "" });
    setResult("Edition cancelled");
    setIsSuccess(null);
  };

  const editAccount = async (event: React.SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!foundAccount) return;

    setIsSaving(true);
    setResult("");

    try {
      const queryParams = new URLSearchParams({
        name: formData.name,
        description: formData.description,
      });

      await api.put(`/accounts/${foundAccount.id}?${queryParams}`);

      setResult("Account successfully updated");
      setIsSuccess(true);

      setTimeout(() => {
        setFoundAccount(null);
        setSearchQuery("");
        setResult("");
        setIsSuccess(null);
      }, 2000);
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
        <h1 className="text-3xl font-extrabold text-gray-800">Edit Accounts</h1>

        <button
          onClick={() => navigate("/manage-accounts")}
          className="text-gray-500 hover:text-primary flex items-center gap-1 font-medium cursor-pointer transition-colors"
        >
          <span className="material-icons">arrow_back</span> Return
        </button>
      </div>

      <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 max-w-2xl mx-auto">
        <form onSubmit={handleSearch} className="flex flex-col gap-6">
          <div className="flex flex-col gap-2">
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Search
            </label>

            <div className="relative">
              <span className="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
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
            name="search"
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
          <form onSubmit={editAccount} className="flex flex-col gap-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="flex flex-col gap-2">
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Code
                </label>

                <input
                  name="code"
                  type="text"
                  value={foundAccount.code}
                  disabled
                  className="cursor-not-allowed w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors"
                />
              </div>

              <div className="flex flex-col gap-2">
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Type
                </label>

                <input
                  name="type"
                  type="text"
                  value={foundAccount.type}
                  disabled
                  className="cursor-not-allowed w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors"
                />
              </div>

              <div className="flex flex-col gap-2 md:col-span-2">
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Name
                </label>

                <input
                  name="name"
                  type="text"
                  placeholder="Name"
                  value={formData.name}
                  onChange={(event) => handleChange(event)}
                  required
                  className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors"
                />
              </div>

              <div className="flex flex-col gap-2 md:col-span-2">
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Description
                </label>

                <input
                  name="description"
                  type="text"
                  placeholder="Description"
                  value={formData.description}
                  onChange={(event) => handleChange(event)}
                  required
                  className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors"
                />
              </div>
            </div>

            <div className="flex gap-4 mt-6">
              <button
                type="button"
                onClick={handleCancel}
                className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-700 font-bold py-4 rounded-xl shadow-md transition-colors duration-300 cursor-pointer"
              >
                Cancel
              </button>

              <button
                name="save"
                type="submit"
                disabled={isSaving}
                className={`flex-1 rounded-xl font-bold py-4 shadow-md transition-colors duration-300 flex justify-center items-center gap-2 ${
                  isSaving
                    ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                    : "bg-primary hover:bg-primary-dark text-white cursor-pointer"
                }`}
              >
                {isSaving ? (
                  <span className="material-icons animate-spin text-sm">
                    sync
                  </span>
                ) : (
                  <span className="material-icons">save</span>
                )}{" "}
                Save
              </button>
            </div>
          </form>
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

export default EditAccounts;
