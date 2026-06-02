import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";

import { TASKS } from "./constants/tasks";

import MainLayout from "./components/MainLayout";
import ProtectedRoute from "./components/ProtectedRoute";
import PublicRoute from "./components/PublicRoute";

import NotFound from "./pages/NotFound";

import Login from "./pages/Login";
import MainMenu from "./pages/MainMenu";

// Access Module
import ManageUsers from "./pages/access/users/ManageUsers";
import CreateUsers from "./pages/access/users/CreateUsers";
import DeleteUsers from "./pages/access/users/DeleteUsers";

import ManageProfiles from "./pages/access/profiles/ManageProfiles";

// Accounting Module
import ManageAccounts from "./pages/accounting/accounts/ManageAccounts";
import ShowAccounts from "./pages/accounting/accounts/ShowAccounts";
import CreateAccounts from "./pages/accounting/accounts/CreateAccounts";
import EditAccounts from "./pages/accounting/accounts/EditAccounts";
import DeleteAccounts from "./pages/accounting/accounts/DeleteAccounts";

import ManageJournalEntries from "./pages/accounting/journal-entries/ManageJournalEntries";

import GeneralJournal from "./pages/accounting/reports/GeneralJournal";
import GeneralLedger from "./pages/accounting/reports/GeneralLedger";

// Sales Module
import SalesMainMenu from "./pages/sales/SalesMainMenu";

function App() {
  return (
    <Router>
      <Routes>
        {/* Public Routes */}
        <Route element={<PublicRoute />}>
          <Route path="/" element={<Login />} />
        </Route>

        {/* Private Routes */}
        <Route element={<ProtectedRoute />}>
          <Route element={<MainLayout />}>
            {/* Main Menu */}
            <Route path="/main-menu" element={<MainMenu />} />

            {/* --- ACCESS AND ACCOUNTING MODULE ROUTES --- */}

            {/* Users */}
            <Route
              element={<ProtectedRoute requiredTask={TASKS.MANAGE_USERS} />}
            >
              <Route path="/manage-users">
                <Route index element={<ManageUsers />} />
                <Route path="create" element={<CreateUsers />} />
                <Route path="delete" element={<DeleteUsers />} />
              </Route>
            </Route>

            {/* Profiles */}
            <Route
              element={<ProtectedRoute requiredTask={TASKS.MANAGE_PROFILES} />}
            >
              <Route path="/manage-profiles" element={<ManageProfiles />} />
            </Route>

            {/* Accounts */}
            <Route
              element={<ProtectedRoute requiredTask={TASKS.MANAGE_ACCOUNTS} />}
            >
              <Route path="/manage-accounts">
                <Route index element={<ManageAccounts />} />
                <Route path="show" element={<ShowAccounts />} />
                <Route path="create" element={<CreateAccounts />} />
                <Route path="edit" element={<EditAccounts />} />
                <Route path="delete" element={<DeleteAccounts />} />
              </Route>
            </Route>

            {/* Journal Entries */}
            <Route
              element={
                <ProtectedRoute requiredTask={TASKS.MANAGE_JOURNAL_ENTRIES} />
              }
            >
              <Route
                path="/manage-journal-entries"
                element={<ManageJournalEntries />}
              />
            </Route>

            {/* General Journal and General Ledger */}
            <Route
              element={<ProtectedRoute requiredTask={TASKS.VIEW_REPORTS} />}
            >
              <Route path="/reports">
                <Route path="general-journal" element={<GeneralJournal />} />
                <Route path="general-ledger" element={<GeneralLedger />} />
              </Route>
            </Route>

            {/* --- SALES MODULE ROUTES --- */}

            {/* Sales Menu */}
            <Route path="/sales-menu" element={<SalesMainMenu />} />
          </Route>
        </Route>

        <Route path="/404" element={<NotFound />} />
        <Route path="*" element={<Navigate to="/404" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
