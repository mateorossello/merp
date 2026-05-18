import React, { useState, useEffect } from "react";
import type { AccountOutput } from "../../types/accounting/AccountOutput";
import type { JournalEntryInput } from "../../types/accounting/JournalEntryInput";
import api from "../../utils/api";
import { extractFirstError } from "../../utils/methods";

function ManageJournalEntries() {
  const today = new Date();
  const localDate = new Date(
    today.getTime() - today.getTimezoneOffset() * 60000,
  )
    .toISOString()
    .split("T")[0];
  const [accounts, setAccounts] = useState<AccountOutput[]>([]);
  const [formData, setFormData] = useState<JournalEntryInput>({
    entryDate: localDate,
    description: "",
    journalEntryLinesInput: [
      { accountId: null, amount: null, debit: null, reference: "" },
      { accountId: null, amount: null, debit: null, reference: "" },
    ],
  });
  const totalDebit = formData.journalEntryLinesInput.reduce(
    (sum, line) => sum + (line.debit === true ? Number(line.amount) : 0),
    0,
  );
  const totalCredit = formData.journalEntryLinesInput.reduce(
    (sum, line) => sum + (line.debit === false ? Number(line.amount) : 0),
    0,
  );
  const isBalanced = totalDebit > 0 && totalDebit === totalCredit;
  const allLinesFilled = formData.journalEntryLinesInput.every(
    (line) =>
      line.accountId !== null &&
      line.amount !== null &&
      line.amount > 0 &&
      line.debit !== null,
  );
  const [result, setResult] = useState("");
  const [isSuccess, setIsSuccess] = useState<boolean | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    document.title = "MERP - Manage Journal Entries";

    const fetchAccounts = async () => {
      try {
        const response = await api.get("/accounts?receiveBalance=true");

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

  const handleEntryChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [event.target.name]: event.target.value });
  };

  const handleLineChange = (
    index: number,
    field: "accountId" | "amount" | "reference",
    value: string,
    isDebitSelection?: boolean,
  ) => {
    const newLines = [...formData.journalEntryLinesInput];
    const currentLine = { ...newLines[index] };

    if (field === "accountId") {
      currentLine.accountId = value ? parseInt(value) : null;
    } else if (field === "reference") {
      currentLine.reference = value;
    } else if (field === "amount") {
      const numberValue = value === "" ? null : Number(value);
      currentLine.amount = numberValue;

      if (isDebitSelection !== undefined) {
        currentLine.debit =
          numberValue && numberValue > 0 ? isDebitSelection : null;
      }
    }

    newLines[index] = currentLine;
    setFormData({ ...formData, journalEntryLinesInput: newLines });
  };

  const addTransaction = () => {
    setFormData({
      ...formData,
      journalEntryLinesInput: [
        ...formData.journalEntryLinesInput,
        { accountId: null, amount: null, debit: null, reference: "" },
      ],
    });
  };

  const removeTransaction = (index: number) => {
    setFormData({
      ...formData,
      journalEntryLinesInput: formData.journalEntryLinesInput.filter(
        (_, filterIndex) => filterIndex !== index,
      ),
    });
  };

  const createJournalEntry = async (
    event: React.SyntheticEvent<HTMLFormElement>,
  ) => {
    event.preventDefault();
    setResult("");
    setIsSuccess(null);

    const selectedAccounts = formData.journalEntryLinesInput
      .map((line) => line.accountId)
      .filter((id) => id !== null);

    const hasDuplicates =
      new Set(selectedAccounts).size !== selectedAccounts.length;

    if (hasDuplicates) {
      setResult("You cannot use the same account multiple times in one entry");
      setIsSuccess(false);
      return;
    }

    if (!isBalanced) {
      setResult(
        "The journal entry is not balanced, total Debit must equal total Credit",
      );
      setIsSuccess(false);
      return;
    }

    setIsSaving(true);

    try {
      await api.post(`/journal-entries`, formData);

      setResult("Journal entry registered successfully");
      setIsSuccess(true);

      setFormData({
        entryDate: localDate,
        description: "",
        journalEntryLinesInput: [
          { accountId: null, amount: null, debit: null, reference: "" },
          { accountId: null, amount: null, debit: null, reference: "" },
        ],
      });
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
        <div>
          <h1 className="text-3xl font-extrabold text-gray-800">
            Manage Journal Entries
          </h1>

          <p className="text-gray-600 mt-1">Create journal entries</p>
        </div>
      </div>

      <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100">
        <form onSubmit={createJournalEntry}>
          <div className="p-8 border-b border-gray-100 bg-gray-50/50">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
              <div className="flex flex-col gap-2 md:col-span-1">
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Date
                </label>

                <input
                  name="entryDate"
                  type="date"
                  value={formData.entryDate}
                  onChange={handleEntryChange}
                  required
                  className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors"
                />
              </div>

              <div className="flex flex-col gap-2 md:col-span-3">
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Description
                </label>

                <input
                  name="description"
                  type="text"
                  placeholder="Description"
                  value={formData.description}
                  onChange={handleEntryChange}
                  required
                  className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors"
                />
              </div>
            </div>
          </div>

          <div className="p-8">
            <div className="overflow-x-auto rounded-xl border border-gray-200">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="bg-gray-50 text-gray-800 border-b border-gray-200 text-sm uppercase tracking-wider">
                    <th className="p-6 font-semibold text-center border-r border-gray-100 font-mono">
                      Account
                    </th>

                    <th className="p-6 font-semibold text-center border-r border-gray-100 font-mono">
                      Debit
                    </th>

                    <th className="p-6 font-semibold text-center border-r border-gray-100 font-mono">
                      Credit
                    </th>

                    <th></th>
                  </tr>
                </thead>

                <tbody className="divide-y divide-gray-100 text-sm">
                  {formData.journalEntryLinesInput.map((line, index) => (
                    <tr key={index} className="hover:bg-gray-50 align-top">
                      <td className="p-2">
                        <select
                          value={line.accountId || ""}
                          onChange={(event) =>
                            handleLineChange(
                              index,
                              "accountId",
                              event.target.value,
                            )
                          }
                          required
                          className="font-mono w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors cursor-pointer"
                        >
                          <option value="" disabled>
                            Account
                          </option>

                          {accounts.map((account) => (
                            <option key={account.id} value={account.id}>
                              [{account.code}] {account.name}
                            </option>
                          ))}
                        </select>
                      </td>

                      <td className="p-2">
                        <input
                          name={`debit-${index}`}
                          type="number"
                          min="0"
                          step="0.01"
                          placeholder="0.00"
                          value={
                            line.debit === true && line.amount !== null
                              ? line.amount
                              : ""
                          }
                          onKeyDown={(event) =>
                            ["e", "E", "+", "-"].includes(event.key) &&
                            event.preventDefault()
                          }
                          onChange={(event) =>
                            handleLineChange(
                              index,
                              "amount",
                              event.target.value,
                              true,
                            )
                          }
                          disabled={
                            line.debit === false && line.amount !== null
                          }
                          className="font-mono w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors"
                        />
                      </td>

                      <td className="p-2">
                        <input
                          name={`credit-${index}`}
                          type="number"
                          min="0"
                          step="0.01"
                          placeholder="0.00"
                          value={
                            line.debit === false && line.amount !== null
                              ? line.amount
                              : ""
                          }
                          onKeyDown={(event) =>
                            ["e", "E", "+", "-"].includes(event.key) &&
                            event.preventDefault()
                          }
                          onChange={(event) =>
                            handleLineChange(
                              index,
                              "amount",
                              event.target.value,
                              false,
                            )
                          }
                          disabled={line.debit === true && line.amount !== null}
                          className="font-mono w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors"
                        />
                      </td>

                      <td className="p-2 text-center">
                        <button
                          name={`remove-${index}`}
                          type="button"
                          onClick={() => removeTransaction(index)}
                          disabled={
                            formData.journalEntryLinesInput.length <= 2 ||
                            isSaving
                          }
                          className={`p-2 rounded-lg transition-all ${
                            formData.journalEntryLinesInput.length <= 2
                              ? "text-gray-300 cursor-not-allowed"
                              : "text-red-400 hover:bg-red-50 hover:text-red-600 cursor-pointer"
                          }`}
                        >
                          <span className="material-icons text-sm">
                            delete_outline
                          </span>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>

                <tfoot className="bg-gray-50 text-gray-800 border-t border-gray-200 text-sm uppercase tracking-wider">
                  <tr>
                    <td className="p-6 font-semibold text-center border-r border-gray-100 font-mono">
                      Total
                    </td>

                    <td className="p-6 font-semibold text-center border-r border-gray-100 font-mono">
                      ${totalDebit.toFixed(2)}
                    </td>

                    <td className="p-6 font-semibold text-center border-r border-gray-100 font-mono">
                      ${totalCredit.toFixed(2)}
                    </td>

                    <td></td>
                  </tr>
                </tfoot>
              </table>
            </div>

            <div className="flex items-center justify-between mt-4">
              <button
                name="addLine"
                type="button"
                onClick={addTransaction}
                disabled={isSaving}
                className="bg-primary hover:bg-primary-dark text-white px-6 py-2.5 rounded-xl shadow-sm font-bold transition-all flex items-center gap-2 cursor-pointer text-sm"
              >
                <span className="material-icons text-sm">
                  add_circle_outline
                </span>
                Add Line
              </button>

              <div
                className={`px-4 py-2 rounded-xl font-bold flex items-center gap-2 text-sm ${
                  isBalanced
                    ? "bg-green-100 text-green-700"
                    : "bg-red-100 text-red-700"
                }`}
              >
                <span className="material-icons text-sm">
                  {isBalanced ? "balance" : "warning"}
                </span>

                {isBalanced ? "Balanced" : "Not Balanced"}
              </div>
            </div>

            <button
              name="save"
              type="submit"
              disabled={!isBalanced || !allLinesFilled || isSaving}
              className={`w-full mt-8 py-4 rounded-xl font-bold flex items-center justify-center gap-2 transition-all shadow-md ${
                isBalanced && allLinesFilled && !isSaving
                  ? "bg-primary hover:bg-primary-dark text-white cursor-pointer"
                  : "bg-gray-200 text-gray-400 cursor-not-allowed"
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

export default ManageJournalEntries;
