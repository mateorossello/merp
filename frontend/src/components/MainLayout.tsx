import {
  NavLink,
  Outlet,
  useNavigate,
  useLocation,
  type NavLinkProps,
} from "react-router-dom";
import { TASKS } from "../constants/tasks";
import { usePermissions } from "../utils/methods";

interface MenuItemProperties {
  to: string;
  icon: string;
  label: string;
}

const MenuItem = ({ to, icon, label }: MenuItemProperties) => {
  const getNavLinkClasses: NavLinkProps["className"] = ({ isActive }) =>
    `flex items-center gap-4 px-6 py-4 rounded-xl mx-2 transition-all duration-200 ${
      isActive
        ? "bg-primary text-white shadow-md"
        : "text-gray-500 hover:text-primary hover:bg-gray-50 cursor-pointer"
    }`;

  return (
    <NavLink to={to} className={getNavLinkClasses}>
      <span className="material-icons text-2xl">{icon}</span>
      <h3 className="text-sm font-medium">{label}</h3>
    </NavLink>
  );
};

function MainLayout() {
  const { hasPermission } = usePermissions();
  const navigate = useNavigate();
  const location = useLocation();

  const ACCOUNTING_TASKS = [
    TASKS.MANAGE_ACCOUNTS,
    TASKS.VIEW_ACCOUNTS,
    TASKS.MANAGE_JOURNAL_ENTRIES,
    TASKS.VIEW_JOURNAL_ENTRIES,
    TASKS.VIEW_REPORTS,
  ];

  const SALES_TASKS = [
    TASKS.MANAGE_ITEMS,
    TASKS.VIEW_ITEMS,
    TASKS.MANAGE_CUSTOMERS,
    TASKS.VIEW_CUSTOMERS,
    TASKS.MANAGE_FISCAL_CONFIGURATION,
    TASKS.VIEW_FISCAL_CONFIGURATION,
    TASKS.MANAGE_PAYMENT_METHODS,
    TASKS.VIEW_PAYMENT_METHODS,
    TASKS.MANAGE_TRANSACTIONS,
    TASKS.VIEW_TRANSACTIONS,
    TASKS.MANAGE_INVOICES,
    TASKS.VIEW_INVOICES,
    TASKS.MANAGE_DELIVERY_NOTES,
    TASKS.VIEW_DELIVERY_NOTES,
    TASKS.MANAGE_NOTES,
    TASKS.VIEW_NOTES,
  ];

  const hasAccessToAccounting = ACCOUNTING_TASKS.some((task) =>
    hasPermission(task),
  );
  const hasAccessToSales = SALES_TASKS.some((task) => hasPermission(task));

  const ACCOUNTING_PATHS = [
    "/main-menu",
    "/manage-users",
    "/manage-profiles",
    "/manage-accounts",
    "/manage-journal-entries",
    "/reports/general-journal",
    "/reports/general-ledger",
  ];

  const SALES_PATHS = [
    "/sales-menu",
    "/manage-items",
    "/manage-customers",
    "/manage-fiscal-configuration",
    "/manage-payment-methods",
    "/manage-transactions",
    "/sales-reports",
    "/manage-invoices",
    "/manage-delivery-notes",
    "/manage-notes",
  ];

  const activeModule = (() => {
    if (ACCOUNTING_PATHS.some((p) => location.pathname.startsWith(p))) {
      return "accounting";
    }

    if (SALES_PATHS.some((p) => location.pathname.startsWith(p))) {
      return "sales";
    }

    return null;
  })();

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/";
  };

  return (
    <div className="h-screen bg-gray-100 flex p-4 gap-6 font-['Poppins']">
      <aside className="w-64 flex flex-col h-full">
        <div className="flex items-center gap-3 px-4 py-6">
          <div className="bg-primary text-white p-2 rounded-xl flex items-center justify-center shadow-md">
            <span className="material-icons text-3xl">
              {activeModule === "accounting"
                ? "account_balance"
                : activeModule === "sales"
                  ? "store"
                  : "apps"}
            </span>
          </div>

          <h2 className="text-xl font-extrabold text-gray-800 tracking-tight">
            {activeModule === "accounting"
              ? "Accounting System"
              : activeModule === "sales"
                ? "Sales System"
                : "System"}
          </h2>
        </div>

        <div className="flex-1 bg-white rounded-2xl shadow-lg py-4 flex flex-col overflow-y-auto">
          {activeModule === "accounting" && (
            <>
              <MenuItem to="/main-menu" icon="home" label="Main Menu" />

              {hasPermission(TASKS.MANAGE_USERS) && (
                <MenuItem
                  to="/manage-users"
                  icon="person_outline"
                  label="Users"
                />
              )}
              {hasPermission(TASKS.MANAGE_PROFILES) && (
                <MenuItem
                  to="/manage-profiles"
                  icon="admin_panel_settings"
                  label="Profiles"
                />
              )}

              {hasPermission(TASKS.MANAGE_ACCOUNTS) && (
                <MenuItem
                  to="/manage-accounts"
                  icon="account_tree"
                  label="Accounts"
                />
              )}
              {hasPermission(TASKS.MANAGE_JOURNAL_ENTRIES) && (
                <MenuItem
                  to="/manage-journal-entries"
                  icon="receipt_long"
                  label="Journal Entries"
                />
              )}
              {hasPermission(TASKS.VIEW_REPORTS) && (
                <>
                  <MenuItem
                    to="/reports/general-journal"
                    icon="description"
                    label="General Journal"
                  />
                  <MenuItem
                    to="/reports/general-ledger"
                    icon="book"
                    label="General Ledger"
                  />
                </>
              )}
            </>
          )}

          {activeModule === "sales" && (
            <>
              <MenuItem to="/sales-menu" icon="home" label="Main Menu" />
            </>
          )}

          <div className="mt-auto pt-4 flex flex-col">
            {activeModule === "accounting" && hasAccessToSales && (
              <button
                onClick={() => {
                  navigate("/sales-menu");
                }}
                className="flex items-center gap-4 px-6 py-4 rounded-xl mx-2 transition-all duration-200 text-primary hover:bg-blue-50 cursor-pointer mb-2"
              >
                <span className="material-icons text-2xl">store</span>
                <h3 className="text-sm font-medium">Sales Module</h3>
              </button>
            )}

            {activeModule === "sales" && hasAccessToAccounting && (
              <button
                onClick={() => {
                  navigate("/main-menu");
                }}
                className="flex items-center gap-4 px-6 py-4 rounded-xl mx-2 transition-all duration-200 text-primary hover:bg-blue-50 cursor-pointer mb-2"
              >
                <span className="material-icons text-2xl">account_balance</span>
                <h3 className="text-sm font-medium">Accounting Module</h3>
              </button>
            )}

            <button
              onClick={handleLogout}
              className={
                "flex items-center gap-4 px-6 py-4 rounded-xl mx-2 transition-all duration-200 text-red-500 hover:bg-red-50 cursor-pointer"
              }
            >
              <span className="material-icons text-2xl">logout</span>
              <h3 className="text-sm font-medium">Log out</h3>
            </button>
          </div>
        </div>
      </aside>

      <main className="flex-1 bg-white rounded-2xl shadow-lg flex flex-col overflow-hidden relative">
        <div className="flex-1 overflow-y-auto scrollbar-hide p-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
}

export default MainLayout;
