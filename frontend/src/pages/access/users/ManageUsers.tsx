import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

function ManageUsers() {
  const navigate = useNavigate();

  useEffect(() => {
    document.title = "MERP - Manage Users";
  }, []);

  const userActions = [
    {
      label: "Create",
      path: "create",
      icon: "person_add",
      color: "hover:border-primary",
      iconBackground: "bg-primary-light",
      iconColor: "text-primary",
      hoverBackground: "group-hover:bg-primary",
    },
    {
      label: "Delete",
      path: "delete",
      icon: "person_remove",
      color: "hover:border-red-500",
      iconBackground: "bg-red-50",
      iconColor: "text-red-500",
      hoverBackground: "group-hover:bg-red-500",
    },
  ];

  return (
    <div className="max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-extrabold text-gray-800">
            Manage Users
          </h1>

          <p className="text-gray-600 mt-1">
            Choose which action you want to perform
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        {userActions.map((action) => (
          <button
            key={action.path}
            onClick={() => navigate(action.path)}
            className={`flex flex-col items-center justify-center p-8 bg-white rounded-2xl border border-gray-100 shadow-sm hover:shadow-md ${action.color} group transition duration-300 cursor-pointer`}
          >
            <div
              className={`w-16 h-16 ${action.iconBackground} ${action.iconColor} rounded-2xl flex items-center justify-center mb-4 ${action.hoverBackground} group-hover:text-white transition-colors`}
            >
              <span className="material-icons text-3xl">{action.icon}</span>
            </div>

            <span className="text-sm font-bold text-gray-700 group-hover:text-gray-900 text-center">
              {action.label}
            </span>
          </button>
        ))}
      </div>
    </div>
  );
}

export default ManageUsers;
