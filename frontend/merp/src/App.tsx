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

import ManageUsers from "./pages/users/ManageUsers";
import CreateUsers from "./pages/users/CreateUsers";
import DeleteUsers from "./pages/users/DeleteUsers";

import ManageProfiles from "./pages/profiles/ManageProfiles";

import ManageAccounts from "./pages/accounts/ManageAccounts";
import ShowAccounts from "./pages/accounts/ShowAccounts";
import CreateAccounts from "./pages/accounts/CreateAccounts";
import EditAccounts from "./pages/accounts/EditAccounts";
import DeleteAccounts from "./pages/accounts/DeleteAccounts";

import ManageJournalEntries from "./pages/journal-entries/ManageJournalEntries";

import GeneralJournal from "./pages/reports/GeneralJournal";
import GeneralLedger from "./pages/reports/GeneralLedger";

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
          </Route>
        </Route>

        <Route path="/404" element={<NotFound />} />
        <Route path="*" element={<Navigate to="/404" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
