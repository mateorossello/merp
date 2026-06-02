import { useState, useEffect } from "react";
import api from "../../utils/api";
import { extractFirstError } from "../../utils/methods";
import type { ItemOutput } from "../../types/sales/ItemOutput";

function SalesMainMenu() {
  const [lowStockItems, setLowStockItems] = useState<ItemOutput[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    document.title = "MERP - Main Menu - Sales";

    const fetchLowStockItems = async () => {
      try {
        const response = await api.get("/sales/items/below-minimum-stock");
        setLowStockItems(response.data);
      } catch (error: unknown) {
        setError(extractFirstError(error));
      } finally {
        setIsLoading(false);
      }
    };

    fetchLowStockItems();
  }, []);

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-full">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto">
      <div className="flex flex-col md:flex-row items-center justify-between mb-8 gap-4">
        <div>
          <h1 className="text-3xl font-extrabold text-gray-800">
            Sales System Dashboard
          </h1>

          <p className="text-gray-600 mt-1">Overview of the Sales System</p>
        </div>
      </div>

      {error && (
        <div className="bg-red-100 text-red-700 p-4 rounded-xl mb-6 font-medium">
          {error}
        </div>
      )}

      {lowStockItems.length === 0 ? (
        <div className="bg-white p-12 rounded-2xl border border-gray-100 shadow-sm flex flex-col items-center justify-center text-center">
          <div className="w-24 h-24 bg-green-50 text-green-500 rounded-full flex items-center justify-center mb-6">
            <span className="material-icons text-5xl">check_circle</span>
          </div>

          <h2 className="text-2xl font-bold text-gray-800 mb-2">All Good!</h2>

          <p className="text-gray-500 max-w-md">
            You have no items with stock below the minimum threshold.
          </p>
        </div>
      ) : (
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          <div className="bg-red-50 p-6 border-b border-red-100 flex items-center gap-4">
            <div className="w-12 h-12 bg-red-100 text-red-500 rounded-full flex items-center justify-center">
              <span className="material-icons text-2xl">warning</span>
            </div>
            <div>
              <h2 className="text-xl font-bold text-red-700">
                Low Stock Alert
              </h2>

              <p className="text-red-600 text-sm">
                The following items are below their minimum stock threshold
              </p>
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-gray-50 text-gray-900 border-b border-gray-100 text-sm uppercase tracking-wider">
                  <th className="p-4 text-center font-semibold">Code</th>

                  <th className="p-4 text-center font-semibold">Name</th>

                  <th className="p-4 text-center font-semibold">
                    Current Stock
                  </th>

                  <th className="p-4 text-center font-semibold">
                    Minimum Stock
                  </th>
                </tr>
              </thead>

              <tbody className="text-sm">
                {lowStockItems.map((item) => (
                  <tr
                    key={item.id}
                    className="border-b border-gray-50 hover:bg-gray-50 transition-colors"
                  >
                    <td className="p-4 text-center text-gray-900">
                      {item.code}
                    </td>

                    <td className="p-4 text-center text-gray-900">
                      {item.name}
                    </td>

                    <td className="p-4 text-center font-bold text-red-600">
                      {item.currentStock}
                    </td>

                    <td className="p-4 text-center text-gray-900">
                      {item.minimumStock}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="bg-gray-50 p-4 border-t border-gray-100 flex justify-end">
            <div className="flex items-center gap-2 text-red-600 font-bold bg-red-100 px-4 py-2 rounded-lg">
              {lowStockItems.length}

              <span className="material-icons text-sm">error</span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default SalesMainMenu;
