import { useState, useEffect } from "react";
import { jsPDF } from "jspdf";
import autoTable from "jspdf-autotable";
import type { RowInput } from "jspdf-autotable";
import type { AccountOutput } from "../../../types/accounting/AccountOutput";
import type { GeneralLedgerOutput } from "../../../types/accounting/GeneralLedgerOutput";
import api from "../../../utils/api";
import { extractFirstError } from "../../../utils/methods";

interface ExtendedGeneralLedger extends GeneralLedgerOutput {
  accountCode: string;
  finalBalance: number;
}

declare module "jspdf" {
  interface jsPDF {
    lastAutoTable?: {
      finalY: number;
    };
  }
}

function GeneralLedger() {
  const today = new Date();
  const localDate = new Date(
    today.getTime() - today.getTimezoneOffset() * 60000,
  )
    .toISOString()
    .split("T")[0];
  const [startDate, setStartDate] = useState(localDate);
  const [endDate, setEndDate] = useState(localDate);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedAccounts, setSelectedAccounts] = useState<AccountOutput[]>([]);
  const [previewData, setPreviewData] = useState<ExtendedGeneralLedger[]>([]);
  const [result, setResult] = useState("");
  const [isSuccess, setIsSuccess] = useState<boolean | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    document.title = "MERP - General Ledger";
  }, []);

  const handleClear = () => {
    setSearchQuery("");
    setStartDate(localDate);
    setEndDate(localDate);
    setResult("");
    setSelectedAccounts([]);
    setPreviewData([]);
    setIsSuccess(null);
  };

  const handleSearchAccount = async () => {
    if (searchQuery.trim() === "") {
      setResult("Please enter a valid account code or name");
      setIsSuccess(false);
      return;
    }

    setResult("");
    setIsSuccess(null);

    const isCode = /^[0-9.]+$/.test(searchQuery.trim());
    const safeSearchQuery = encodeURIComponent(searchQuery.trim());

    const url = isCode
      ? `/accounts/code/${safeSearchQuery}`
      : `/accounts?name=${safeSearchQuery}`;

    try {
      const response = await api.get(url);

      const account: AccountOutput = isCode ? response.data : response.data[0];

      if (!account) {
        setResult("Account not found");
        setIsSuccess(false);
        return;
      }

      if (
        selectedAccounts.some(
          (selectedAccount) => selectedAccount.id === account.id,
        )
      ) {
        setResult(`Account "${account.name}" is already selected`);
        setIsSuccess(false);
      } else {
        setSelectedAccounts((prev) => [...prev, account]);
        setSearchQuery("");
        setResult("");
        setIsSuccess(null);
      }
    } catch (error: unknown) {
      setResult(extractFirstError(error));
      setIsSuccess(false);
    }
  };

  const removeSelectedAccount = (id: number) => {
    setSelectedAccounts(
      selectedAccounts.filter((account) => account.id !== id),
    );

    setPreviewData([]);
  };

  const handlePreview = async () => {
    if (!startDate || !endDate) {
      setResult("Please select both start and end dates");
      setIsSuccess(false);
      return;
    }

    if (new Date(startDate) > new Date(endDate)) {
      setResult("Start date cannot be after end date");
      setIsSuccess(false);
      return;
    }

    if (selectedAccounts.length === 0) {
      setResult("Please select at least one account");
      setIsSuccess(false);
      return;
    }

    setResult("");
    setIsLoading(true);

    const results = await Promise.all(
      selectedAccounts.map(async (account) => {
        try {
          const response = await api.get(
            `/reports/general-ledger?accountId=${account.id}&startDate=${startDate}&endDate=${endDate}`,
          );

          const data: GeneralLedgerOutput = response.data;

          const finalBalance =
            data.accountMovementsOutput.length > 0
              ? data.accountMovementsOutput[
                  data.accountMovementsOutput.length - 1
                ].balance
              : data.initialBalance;

          return {
            ok: true as const,
            ledger: {
              ...data,
              accountCode: account.code,
              finalBalance,
            },
          };
        } catch (error: unknown) {
          return {
            ok: false as const,
            accountName: account.name,
            reason: extractFirstError(error),
          };
        }
      }),
    );

    const generalLedgers = results
      .filter(
        (result): result is { ok: true; ledger: ExtendedGeneralLedger } =>
          result.ok,
      )
      .map((result) => result.ledger);

    const errors = results
      .filter(
        (
          result,
        ): result is { ok: false; accountName: string; reason: string } =>
          !result.ok,
      )
      .map((result) => `${result.accountName}: ${result.reason}`);

    if (errors.length > 0) {
      setResult(`Failed to load data for:\n- ${errors.join("\n- ")}`);
      setIsSuccess(false);
    } else {
      setResult("General Ledgers generated successfully");
      setIsSuccess(true);
    }

    setPreviewData(generalLedgers);
    setIsLoading(false);
  };

  const handleExportPDF = () => {
    if (previewData.length === 0) return;

    const doc = new jsPDF();
    doc.setFontSize(18);
    doc.setTextColor(31, 41, 55);
    doc.text("General Ledger", 14, 20);
    doc.setFontSize(10);
    doc.setTextColor(107, 114, 128);
    doc.text(`Period: ${startDate} to ${endDate}`, 14, 28);

    let currentY = 40;

    previewData.forEach((ledger) => {
      doc.setFontSize(12);
      doc.setTextColor(31, 41, 55);

      doc.text(
        `Account: [${ledger.accountCode}] ${ledger.accountName}`,
        14,
        currentY,
      );

      const tableBody: RowInput[] = [];

      tableBody.push([
        "-",
        "INITIAL BALANCE",
        "-",
        "-",
        {
          content: formatCurrency(ledger.initialBalance),
          styles: { fontStyle: "bold" },
        },
      ]);

      ledger.accountMovementsOutput.forEach((movement) => {
        tableBody.push([
          new Date(movement.date).toLocaleDateString("es-ES", {
            timeZone: "UTC",
          }),
          movement.description,
          {
            content: movement.debit > 0 ? formatCurrency(movement.debit) : "-",
            styles: { halign: "right" },
          },
          {
            content:
              movement.credit > 0 ? formatCurrency(movement.credit) : "-",
            styles: { halign: "right" },
          },
          {
            content: formatCurrency(movement.balance),
            styles: { halign: "right" },
          },
        ]);
      });

      tableBody.push([
        {
          content: "-",
          styles: { fontStyle: "bold", textColor: [8, 100, 248] },
        },
        {
          content: "FINAL BALANCE",
          styles: { fontStyle: "bold", textColor: [8, 100, 248] },
        },
        {
          content: "-",
          styles: { fontStyle: "bold", textColor: [8, 100, 248] },
        },
        {
          content: "-",
          styles: { fontStyle: "bold", textColor: [8, 100, 248] },
        },
        {
          content: formatCurrency(ledger.finalBalance),
          styles: { fontStyle: "bold", textColor: [8, 100, 248] },
        },
      ]);

      autoTable(doc, {
        head: [["Date", "Description", "Debit", "Credit", "Balance"]],
        body: tableBody,
        startY: currentY + 5,
        theme: "grid",
        headStyles: {
          fillColor: [244, 244, 246],
          textColor: [30, 40, 55],
          fontStyle: "bold",
          halign: "center",
          lineColor: [200, 200, 200],
          lineWidth: 0.1,
        },
        styles: {
          fontSize: 8,
          cellPadding: 3,
          textColor: [31, 41, 55],
          lineColor: [200, 200, 200],
          lineWidth: 0.1,
        },
        columnStyles: {
          0: { halign: "center", cellWidth: 24 },
          1: { halign: "center", cellWidth: 74 },
          2: { halign: "right", cellWidth: 28 },
          3: { halign: "right", cellWidth: 28 },
          4: { halign: "right", cellWidth: 28 },
        },
        didParseCell: (hookData) => {
          if (hookData.section !== "head") return;
          if (
            hookData.column.index === 2 ||
            hookData.column.index === 3 ||
            hookData.column.index === 4
          ) {
            hookData.cell.styles.halign = "right";
          }
        },
      });

      const finalY = doc.lastAutoTable?.finalY ?? currentY;
      currentY = finalY + 15;
    });

    doc.save(`GeneralLedger(${startDate}to${endDate}).pdf`);
  };

  const formatCurrency = (val: number) =>
    new Intl.NumberFormat("es-AR", {
      style: "currency",
      currency: "ARS",
    }).format(val);

  return (
    <div className="max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-extrabold text-gray-800">
            General Ledger
          </h1>

          <p className="text-gray-600 mt-1">
            View account movements and balances
          </p>
        </div>
      </div>

      <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 mb-8">
        <div className="flex flex-col gap-2 mb-6">
          <label className="block text-sm font-semibold text-gray-700 mb-2">
            Add accounts
          </label>

          <div className="flex gap-4">
            <div className="relative flex-1">
              <span className="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
                search
              </span>

              <input
                name="searchQuery"
                type="text"
                placeholder="Account code or name"
                value={searchQuery}
                onChange={(event) => setSearchQuery(event.target.value)}
                onKeyDown={(event) =>
                  event.key === "Enter" && handleSearchAccount()
                }
                className={`w-full px-4 py-3 pl-10 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors`}
              />
            </div>

            <button
              type="button"
              onClick={handleSearchAccount}
              className={`px-6 py-3 rounded-xl font-bold text-white bg-primary hover:bg-primary-dark transition-colors flex items-center justify-center gap-2 cursor-pointer`}
            >
              <span className="material-icons text-sm">add</span>
              Add
            </button>
          </div>
        </div>

        {selectedAccounts.length > 0 && (
          <div className="mb-6 p-4 bg-blue-50 rounded-xl border border-blue-100">
            <p className="text-xs font-bold text-primary uppercase font-mono tracking-wider mb-3">
              Selected Accounts
            </p>

            <div className="flex flex-wrap gap-2">
              {selectedAccounts.map((cuenta) => (
                <div
                  key={cuenta.id}
                  className="font-mono bg-white border border-blue-200 text-primary px-3 py-1.5 rounded-lg flex items-center gap-2 text-sm font-medium"
                >
                  <span className="text-blue-500 font-bold">
                    [{cuenta.code}]
                  </span>

                  {cuenta.name}

                  <button
                    name="remove"
                    type="button"
                    onClick={() => removeSelectedAccount(cuenta.id)}
                    className="ml-1 text-blue-400 hover:text-red-500 transition-colors flex items-center cursor-pointer"
                  >
                    <span className="material-icons">cancel</span>
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        <div className="flex flex-col md:flex-row items-end justify-between gap-4 pt-6 border-t border-gray-100">
          <div className="flex-1 grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="flex flex-col gap-2 flex-1">
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                Start date
              </label>

              <input
                name="startDate"
                type="date"
                value={startDate}
                onChange={(event) => setStartDate(event.target.value)}
                className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors cursor-pointer"
              />
            </div>

            <div className="flex flex-col gap-2 flex-1">
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                End date
              </label>

              <input
                name="endDate"
                type="date"
                value={endDate}
                onChange={(event) => setEndDate(event.target.value)}
                className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent focus:bg-white transition-colors cursor-pointer"
              />
            </div>
          </div>

          <div className="flex items-center gap-3 w-full md:w-auto">
            <button
              name="clear"
              type="button"
              onClick={handleClear}
              className="px-6 py-3 rounded-xl font-bold text-gray-700 bg-gray-200 hover:bg-gray-300 transition-colors flex items-center justify-center gap-2 cursor-pointer"
            >
              <span className="material-icons text-sm">clear</span>
              Clear
            </button>

            <button
              name="search"
              type="button"
              onClick={handlePreview}
              disabled={isLoading || selectedAccounts.length === 0}
              className={`flex-1 md:flex-none px-8 py-3 rounded-xl font-bold flex items-center justify-center gap-2 transition-colors duration-300 ${
                isLoading || selectedAccounts.length === 0
                  ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                  : "bg-primary hover:bg-primary-dark text-white cursor-pointer"
              }`}
            >
              {isLoading ? (
                <span className="material-icons animate-spin text-sm">
                  sync
                </span>
              ) : (
                <span className="material-icons text-sm">visibility</span>
              )}
              Search
            </button>
          </div>
        </div>
      </div>

      {result && (
        <div
          className={`max-w-md mx-auto mt-4 p-4 mb-6 rounded-xl font-medium text-center ${isSuccess ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"}`}
        >
          {result}
        </div>
      )}

      {previewData.map((ledger) => (
        <div
          key={ledger.accountCode}
          className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden mb-8"
        >
          <div className="bg-blue-50/30 p-4 flex items-center gap-3">
            <div className="bg-white border border-gray-200 text-primary font-mono font-bold px-3 py-1 rounded-lg">
              {ledger.accountCode}
            </div>

            <h2 className="text-lg font-bold text-gray-800">
              {ledger.accountName}
            </h2>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-gray-50 text-gray-800 border-b-2 border-t-2 border-gray-200 text-sm uppercase tracking-wider">
                  <th className="p-6 font-semibold text-center border-r border-gray-100 font-mono">
                    Date
                  </th>

                  <th className="p-6 font-semibold text-center border-r border-gray-100">
                    Description
                  </th>

                  <th className="p-6 font-semibold text-right border-r border-gray-100">
                    Debit
                  </th>

                  <th className="p-6 font-semibold text-right border-r border-gray-100">
                    Credit
                  </th>

                  <th className="p-6 font-semibold text-right border-r border-gray-100">
                    Balance
                  </th>
                </tr>
              </thead>

              <tbody className="divide-y divide-gray-100 text-sm">
                <tr className="hover:bg-blue-50/10 transition-colors">
                  <td className="text-center p-6 text-gray-800 align-top bg-white border-r border-gray-100 font-mono">
                    -
                  </td>

                  <td className="text-center p-6 text-gray-800 align-top bg-white border-r border-gray-100">
                    INITIAL BALANCE
                  </td>

                  <td className="text-right p-6 text-gray-800 align-top bg-white border-r border-gray-100 font-mono">
                    -
                  </td>

                  <td className="text-right p-6 text-gray-800 align-top bg-white border-r border-gray-100 font-mono">
                    -
                  </td>

                  <td className="text-right p-6 text-gray-800 align-top font-mono">
                    {formatCurrency(ledger.initialBalance)}
                  </td>
                </tr>

                {ledger.accountMovementsOutput.length > 0 ? (
                  ledger.accountMovementsOutput.map((movement) => (
                    <tr
                      key={`${ledger.accountCode}-${movement.date}-${movement.description}-${movement.debit}-${movement.credit}-${movement.balance}`}
                      className={`hover:bg-blue-50/10 transition-color`}
                    >
                      <td className="text-center p-6 text-gray-800 align-top bg-white border-r border-gray-100">
                        {new Date(movement.date).toLocaleDateString("es-ES", {
                          timeZone: "UTC",
                        })}
                      </td>

                      <td className="text-center p-6 text-gray-800 align-top bg-white border-r border-gray-100">
                        {movement.description}
                      </td>

                      <td className="text-right p-6 text-gray-800 align-top bg-white border-r border-gray-100 font-mono">
                        {movement.debit > 0
                          ? formatCurrency(movement.debit)
                          : "-"}
                      </td>

                      <td className="text-right p-6 text-gray-800 align-top bg-white border-r border-gray-100 font-mono">
                        {movement.credit > 0
                          ? formatCurrency(movement.credit)
                          : "-"}
                      </td>

                      <td className="text-right p-6 text-gray-800 align-top font-mono">
                        {formatCurrency(movement.balance)}
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td
                      colSpan={5}
                      className="text-center p-6 text-gray-800 align-top"
                    >
                      No movements registered for this period
                    </td>
                  </tr>
                )}

                <tr className="bg-blue-50/30 border-t-2 border-gray-200">
                  <td
                    colSpan={4}
                    className="text-left p-6 font-bold text-primary text-lg font-mono"
                  >
                    Final Balance
                  </td>

                  <td className="text-right p-6 font-bold text-primary text-lg font-mono">
                    {formatCurrency(ledger.finalBalance)}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      ))}

      {previewData.length > 0 && (
        <div className="flex justify-end">
          <button
            name="exportPDF"
            type="button"
            onClick={handleExportPDF}
            className="px-6 py-3 rounded-xl font-bold text-white bg-red-500 hover:bg-red-600 transition-colors duration-300 flex items-center justify-center gap-2 shadow-md cursor-pointer"
          >
            <span className="material-icons text-sm">picture_as_pdf</span>
            Export PDF
          </button>
        </div>
      )}
    </div>
  );
}

export default GeneralLedger;
