import { NavLink, Outlet, type NavLinkProps } from "react-router-dom";
import { TASKS } from "../constants/tasks";
import { usePermissions } from "../utils/methods";

interface MenuItem {
  to: string;
  icon: string;
  label: string;
}

const MenuItem = ({ to, icon, label }: MenuItem) => {
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

  const handleLogout = () => {
    localStorage.clear();
    window.location.href = "/";
  };

  return (
    <div className="h-screen bg-gray-100 flex p-4 gap-6 font-['Poppins']">
      <aside className="w-64 flex flex-col h-full">
        <div className="flex items-center gap-3 px-4 py-6">
          <div className="bg-primary text-white p-2 rounded-xl flex items-center justify-center shadow-md">
            <span className="material-icons text-3xl">account_balance</span>
          </div>

          <h2 className="text-xl font-extrabold text-gray-800 tracking-tight">
            Accounting System
          </h2>
        </div>

        <div className="flex-1 bg-white rounded-2xl shadow-lg py-4 flex flex-col overflow-y-auto">
          <MenuItem to="/main-menu" icon="home" label="Main Menu" />

          {hasPermission(TASKS.MANAGE_USERS) && (
            <MenuItem to="/manage-users" icon="person_outline" label="Users" />
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

          <div className="mt-auto pt-4 flex flex-col">
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
        <div className="flex-1 overflow-y-auto p-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
}

export default MainLayout;
