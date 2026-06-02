import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { ACCOUNT_TYPES } from "../../../types/accounting/AccountOutput";
import type { AccountOutput } from "../../../types/accounting/AccountOutput";
import type { AccountInput } from "../../../types/accounting/AccountInput";
import api from "../../../utils/api";
import { extractFirstError } from "../../../utils/methods";

function CreateAccounts() {
  const navigate = useNavigate();
  const [accounts, setAccounts] = useState<AccountOutput[]>([]);
  const [formData, setFormData] = useState<AccountInput>({
    parentAccountId: null,
    code: "",
    type: null,
    name: "",
    description: "",
  });
  const [result, setResult] = useState("");
  const [isSuccess, setIsSuccess] = useState<boolean | null>(null);

  useEffect(() => {
    document.title = "MERP - Create Accounts";

    const fetchAccounts = async () => {
      try {
        const response = await api.get("/accounts");

        setAccounts(
          response.data.sort((a: AccountOutput, b: AccountOutput) =>
            a.code.localeCompare(b.code),
          ),
        );
      } catch (error: unknown) {
        setResult(extractFirstError(error));
        setIsSuccess(false);
      }
    };

    fetchAccounts();
  }, []);

  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
  ) => {
    const { name, value } = event.target;

    setFormData((prev) => ({
      ...prev,
      [name]: name === "type" ? (value as AccountInput["type"]) : value,
    }));
  };

  const handleParentAccountIdChange = (
    event: React.ChangeEvent<HTMLSelectElement>,
  ) => {
    const parentAccountId = event.target.value
      ? parseInt(event.target.value)
      : null;

    setFormData((prev) => ({
      ...prev,
      parentAccountId: parentAccountId,
      type: null,
    }));
  };

  const createAccount = async (
    event: React.SyntheticEvent<HTMLFormElement>,
  ) => {
    event.preventDefault();
    setResult("");

    try {
      await api.post("/accounts", formData);

      setResult("Account created successfully");
      setIsSuccess(true);
      setFormData({
        parentAccountId: null,
        code: "",
        type: null,
        name: "",
        description: "",
      });

      setTimeout(() => {
        navigate(0);
      }, 2000);
    } catch (error: unknown) {
      setResult(extractFirstError(error));
      setIsSuccess(false);
    }
  };

  return (
    <div className="max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-3xl font-extrabold text-gray-800">
          Create Accounts
        </h1>

        <button
          onClick={() => navigate("/manage-accounts")}
          className="text-gray-500 hover:text-primary flex items-center gap-1 font-medium cursor-pointer transition-colors"
        >
          <span className="material-icons">arrow_back</span> Return
        </button>
      </div>

      <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 max-w-2xl mx-auto">
        <form onSubmit={createAccount} className="flex flex-col gap-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="flex flex-col gap-2">
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

            <div className="flex flex-col gap-2">
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                Code
              </label>

              <input
                name="code"
                type="text"
                placeholder="Code"
                value={formData.code}
                onChange={(event) => handleChange(event)}
                required
                className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors"
              />
            </div>
          </div>

          <div className="flex flex-col gap-2">
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

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="flex flex-col gap-2">
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                Parent Account
              </label>

              <select
                name="parentAccount"
                value={formData.parentAccountId || ""}
                onChange={(event) => handleParentAccountIdChange(event)}
                className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors cursor-pointer"
              >
                <option value="">None</option>

                {accounts.map((account) => (
                  <option key={account.id} value={account.id}>
                    [{account.code}] {account.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex flex-col gap-2">
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                Type
              </label>

              <select
                name="type"
                value={formData.type || ""}
                onChange={(event) => handleChange(event)}
                disabled={!!formData.parentAccountId}
                required={!formData.parentAccountId}
                className={`w-full px-4 py-3 border rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-colors cursor-pointer ${
                  formData.parentAccountId
                    ? "bg-blue-50 border-blue-100 text-primary font-bold cursor-not-allowed"
                    : "bg-gray-50 border-gray-200 focus:border-transparent focus:bg-white"
                }`}
              >
                {formData.parentAccountId ? (
                  <option value="">Defined by parent</option>
                ) : (
                  <option value="" disabled>
                    Type
                  </option>
                )}

                {ACCOUNT_TYPES.map((type) => (
                  <option key={type} value={type}>
                    {type}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <button
            type="submit"
            className="mt-4 w-full bg-primary hover:bg-primary-dark text-white font-bold py-4 rounded-xl shadow-md transition-colors duration-300 cursor-pointer flex justify-center items-center gap-2"
          >
            <span className="material-icons">save</span> Save
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

export default CreateAccounts;
