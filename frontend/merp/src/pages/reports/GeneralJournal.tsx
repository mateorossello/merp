import React, { useState, useEffect } from "react";
import { jsPDF } from "jspdf";
import autoTable from "jspdf-autotable";
import type { GeneralJournalOutput } from "../../types/accounting/GeneralJournalOutput";
import api from "../../utils/api";
import { extractFirstError } from "../../utils/methods";

interface FlattenedJournalLine {
  lineId: number;
  entryId: number;
  date: string;
  description: string;
  accountName: string;
  debitValue: number;
  creditValue: number;
  isFirstLineOfEntry: boolean;
  totalLinesInEntry: number;
}

function GeneralJournal() {
  const today = new Date();
  const localDate = new Date(
    today.getTime() - today.getTimezoneOffset() * 60000,
  )
    .toISOString()
    .split("T")[0];
  const [startDate, setStartDate] = useState(localDate);
  const [endDate, setEndDate] = useState(localDate);
  const [previewData, setPreviewData] = useState<FlattenedJournalLine[]>([]);
  const [result, setResult] = useState("");
  const [isSuccess, setIsSuccess] = useState<boolean | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    document.title = "MERP - General Journal";
  }, []);

  const handleClear = () => {
    setStartDate(localDate);
    setEndDate(localDate);
    setResult("");
    setPreviewData([]);
    setIsSuccess(null);
  };

  const handleSearch = async () => {
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

    setResult("");
    setIsSuccess(null);
    setIsLoading(true);

    try {
      const response = await api.get(
        `/reports/general-journal?startDate=${startDate}&endDate=${endDate}`,
      );

      const data: GeneralJournalOutput = response.data;

      if (data.journalEntriesOutput && data.journalEntriesOutput.length > 0) {
        const flattenedData: FlattenedJournalLine[] = [];

        data.journalEntriesOutput.forEach((entry) => {
          const dateFormatted = new Date(entry.entryDate).toLocaleDateString(
            "es-ES",
            { timeZone: "UTC" },
          );

          const totalLines = entry.journalEntryLinesOutput.length;

          const sortedLines = [...entry.journalEntryLinesOutput].sort((a, b) =>
            a.debit === b.debit ? 0 : a.debit ? -1 : 1,
          );

          sortedLines.forEach((line, index) => {
            flattenedData.push({
              lineId: line.id,
              entryId: entry.id,
              date: dateFormatted,
              description: entry.description,
              accountName: line.accountName,
              debitValue: line.debit ? line.amount : 0,
              creditValue: !line.debit ? line.amount : 0,
              isFirstLineOfEntry: index === 0,
              totalLinesInEntry: totalLines,
            });
          });
        });

        setPreviewData(flattenedData);

        setResult(
          `General Journal generated successfully with ${data.journalEntriesOutput.length} journal entries`,
        );

        setIsSuccess(true);
      } else {
        setPreviewData([]);
        setResult("No journal entries found for the selected date range");
        setIsSuccess(false);
      }
    } catch (error: unknown) {
      setResult(extractFirstError(error));
      setIsSuccess(false);
    } finally {
      setIsLoading(false);
    }
  };

  const handleExportPDF = () => {
    if (previewData.length === 0) return;

    const doc = new jsPDF();
    doc.setFontSize(18);
    doc.setTextColor(31, 41, 55);
    doc.text("General Journal", 14, 20);
    doc.setFontSize(10);
    doc.setTextColor(107, 114, 128);
    doc.text(`Period: ${startDate} to ${endDate}`, 14, 28);

    const tableColumn = [
      "Date",
      "Entry",
      "Description",
      "Account",
      "Debit",
      "Credit",
    ];

    const tableRows: any[] = [];

    previewData.forEach((row) => {
      const displayAccount =
        row.creditValue > 0 ? ` to ${row.accountName}` : row.accountName;

      if (row.isFirstLineOfEntry) {
        tableRows.push([
          {
            content: row.date,
            rowSpan: row.totalLinesInEntry,
            styles: { valign: "middle" },
          },
          {
            content: row.entryId,
            rowSpan: row.totalLinesInEntry,
            styles: { halign: "center", valign: "middle" },
          },
          {
            content: row.description,
            rowSpan: row.totalLinesInEntry,
            styles: { valign: "middle" },
          },
          {
            content: displayAccount,
            styles: { halign: row.debitValue > 0 ? "left" : "center" },
          },
          {
            content: row.debitValue > 0 ? formatCurrency(row.debitValue) : "-",
            styles: { halign: "right" },
          },
          {
            content:
              row.creditValue > 0 ? formatCurrency(row.creditValue) : "-",
            styles: { halign: "right" },
          },
        ]);
      } else {
        tableRows.push([
          {
            content: displayAccount,
            styles: { halign: row.debitValue > 0 ? "left" : "center" },
          },
          {
            content: row.debitValue > 0 ? formatCurrency(row.debitValue) : "-",
            styles: { halign: "right" },
          },
          {
            content:
              row.creditValue > 0 ? formatCurrency(row.creditValue) : "-",
            styles: { halign: "right" },
          },
        ]);
      }
    });

    autoTable(doc, {
      head: [tableColumn],
      body: tableRows,
      startY: 35,
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
        1: { halign: "center", cellWidth: 18 },
        2: { halign: "center", cellWidth: 40 },
        3: { cellWidth: 50 },
        4: { halign: "right", cellWidth: 28 },
        5: { halign: "right", cellWidth: 28 },
      },
      didParseCell: (hookData) => {
        if (hookData.section !== "head") return;
        if (hookData.column.index === 4 || hookData.column.index === 5) {
          hookData.cell.styles.halign = "right";
        }
      },
    });

    doc.save(`GeneralJournal(${startDate}to${endDate}).pdf`);
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
            General Journal
          </h1>

          <p className="text-gray-600 mt-1">
            View journal entries and movements
          </p>
        </div>
      </div>

      <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 mb-8">
        <div className="flex flex-col md:flex-row items-end gap-4">
          <div className="flex-1 grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="flex flex-col gap-2">
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

            <div className="flex flex-col gap-2">
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
              onClick={handleSearch}
              disabled={isLoading}
              className={`flex-1 md:flex-none px-8 py-3 rounded-xl font-bold flex items-center justify-center gap-2 transition-colors duration-300 ${
                isLoading
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

      {previewData.length > 0 && (
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden mb-8">
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-gray-50 text-gray-800 border-b border-gray-100 text-sm uppercase tracking-wider">
                  <th className="p-6 font-semibold text-center border-r border-gray-100">
                    Date
                  </th>

                  <th className="p-6 font-semibold text-center border-r border-gray-100">
                    ID
                  </th>

                  <th className="p-6 font-semibold text-center border-r border-gray-100">
                    Description
                  </th>

                  <th className="p-6 font-semibold text-center border-r border-gray-100">
                    Account
                  </th>

                  <th className="p-6 font-semibold text-right border-r border-gray-100">
                    Debit
                  </th>

                  <th className="p-6 font-semibold text-right border-r border-gray-100">
                    Credit
                  </th>
                </tr>
              </thead>

              <tbody className="divide-y divide-gray-100 text-sm">
                {previewData.map((row) => {
                  const isCreditLine = row.creditValue > 0;

                  return (
                    <React.Fragment key={row.lineId}>
                      <tr
                        className={`hover:bg-blue-50/10 transition-colors ${row.isFirstLineOfEntry ? "border-t-2 border-gray-200" : ""}`}
                      >
                        {row.isFirstLineOfEntry && (
                          <>
                            <td
                              rowSpan={row.totalLinesInEntry}
                              className="text-center p-6 text-gray-800 align-top bg-white border-r border-gray-100"
                            >
                              {row.date}
                            </td>

                            <td
                              rowSpan={row.totalLinesInEntry}
                              className="text-center p-6 text-gray-800 align-top bg-white border-r border-gray-100"
                            >
                              #{row.entryId}
                            </td>

                            <td
                              rowSpan={row.totalLinesInEntry}
                              className="text-center p-6 text-gray-800 align-top bg-white border-r border-gray-100"
                            >
                              {row.description}
                            </td>
                          </>
                        )}

                        <td
                          className={`p-6 ${isCreditLine ? "pl-12 text-gray-600" : "text-gray-800"} bg-white border-r border-gray-100`}
                        >
                          {isCreditLine
                            ? `to ${row.accountName}`
                            : row.accountName}
                        </td>

                        <td className="p-6 text-gray-800 text-right font-mono bg-white border-r border-gray-100">
                          {row.debitValue > 0
                            ? formatCurrency(row.debitValue)
                            : "-"}
                        </td>

                        <td className="p-6 text-gray-800 text-right font-mono">
                          {row.creditValue > 0
                            ? formatCurrency(row.creditValue)
                            : "-"}
                        </td>
                      </tr>
                    </React.Fragment>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}

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

export default GeneralJournal;
