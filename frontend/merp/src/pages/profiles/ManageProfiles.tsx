import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import type { Profile } from "../../types/Profile";
import type { Task } from "../../types/Task";
import api from "../../utils/api";
import { getCurrentProfile, extractFirstError } from "../../utils/methods";

function ManageProfiles() {
  const navigate = useNavigate();
  const [profiles, setProfiles] = useState<Profile[]>([]);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [result, setResult] = useState("");
  const [isSuccess, setIsSuccess] = useState<boolean | null>(null);

  useEffect(() => {
    document.title = "MERP - Manage Profiles";

    const fetchProfilesAndTasks = async () => {
      try {
        const [responseProfiles, responseTasks] = await Promise.all([
          api.get("/profiles"),
          api.get("/tasks"),
        ]);

        const fetchedProfiles = responseProfiles.data;
        const fetchedTasks = responseTasks.data;

        const normalizedProfiles = (
          Array.isArray(fetchedProfiles) ? fetchedProfiles : []
        ).map((profile) => ({
          ...profile,
          tasks: Array.isArray(profile.tasks) ? profile.tasks : [],
        }));

        setProfiles(normalizedProfiles);
        setTasks(Array.isArray(fetchedTasks) ? fetchedTasks : []);
      } catch (error: unknown) {
        setResult(extractFirstError(error));
        setIsSuccess(false);
      }
    };

    fetchProfilesAndTasks();
  }, []);

  const formatTaskName = (name: string) => {
    return name
      .toLowerCase()
      .split("_")
      .map((word) => word[0].toUpperCase() + word.slice(1))
      .join(" ");
  };

  const handleCheckboxChange = (
    profileId: number,
    taskId: number,
    isChecked: boolean,
  ) => {
    setProfiles((updatedProfiles) =>
      updatedProfiles.map((profile) => {
        if (profile.id === profileId) {
          const currentTasks = profile.tasks;
          let updatedTasks;

          if (isChecked) {
            const taskObject = tasks.find((task) => task.id === taskId);
            if (
              taskObject &&
              !currentTasks.some((task) => task.id === taskId)
            ) {
              updatedTasks = [...currentTasks, taskObject];
            } else {
              updatedTasks = currentTasks;
            }
          } else {
            updatedTasks = currentTasks.filter(
              (currentTask) => currentTask.id !== taskId,
            );
          }

          return { ...profile, tasks: updatedTasks };
        }

        return profile;
      }),
    );
  };

  const assignTasks = async (profileId: number, selectedTaskIds: number[]) => {
    setResult("");
    setIsSuccess(null);

    try {
      await api.put(`/profiles/${profileId}/tasks`, selectedTaskIds);

      setResult("Tasks assigned successfully");
      setIsSuccess(true);

      const myProfileName = getCurrentProfile();
      const editedProfile = profiles.find(
        (profile) => profile.id === profileId,
      );

      if (editedProfile && editedProfile.name === myProfileName) {
        setResult("Your permissions have changed");
        setTimeout(() => {
          localStorage.clear();
          navigate("/");
        }, 2000);
      }
    } catch (error: unknown) {
      setResult(extractFirstError(error));
      setIsSuccess(false);
    }
  };

  return (
    <div className="max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-extrabold text-gray-800">
            Manage Profiles
          </h1>

          <p className="text-gray-600 mt-1">
            Assign system permissions to roles
          </p>
        </div>
      </div>

      {result && (
        <div
          className={`p-4 mb-6 rounded-xl font-medium text-center ${isSuccess ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"}`}
        >
          {result}
        </div>
      )}

      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="bg-gray-50 text-gray-800 border-b border-gray-100 text-sm uppercase tracking-wider">
              <th className="p-6 font-semibold w-24">ID</th>
              <th className="p-6 font-semibold w-1/4">Profile Name</th>
              <th className="p-6 font-semibold text-center">Assigned Tasks</th>
            </tr>
          </thead>

          <tbody className="divide-y divide-gray-100">
            {profiles.map((profile) => {
              const currentTasks = profile.tasks;
              const currentTaskIds = new Set(
                currentTasks.map((task) => task.id),
              );

              return (
                <tr
                  key={profile.id}
                  className="hover:bg-gray-50 transition-colors align-top"
                >
                  <td className="p-6 text-gray-600 font-medium pt-8">
                    #{profile.id}
                  </td>
                  <td className="p-6 text-gray-800 font-medium pt-8">
                    {profile.name}
                  </td>
                  <td className="p-6">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mb-6 mt-2">
                      {tasks.map((task) => {
                        const isChecked = currentTaskIds.has(task.id);

                        return (
                          <label
                            key={task.id}
                            className="flex items-center gap-3 text-sm text-gray-700 cursor-pointer hover:bg-gray-100 p-2 rounded-lg transition-colors group"
                          >
                            <input
                              type="checkbox"
                              checked={isChecked}
                              onChange={(event) =>
                                handleCheckboxChange(
                                  profile.id,
                                  task.id,
                                  event.target.checked,
                                )
                              }
                              className="w-4 h-4 accent-primary border-gray-300 rounded cursor-pointer"
                            />

                            <span className="font-medium group-hover:text-gray-900">
                              {formatTaskName(task.name)}
                            </span>
                          </label>
                        );
                      })}
                    </div>

                    <div className="flex justify-end border-t border-gray-100 pt-3">
                      <button
                        onClick={() =>
                          assignTasks(
                            profile.id,
                            currentTasks.map((task) => task.id),
                          )
                        }
                        className="bg-primary hover:bg-primary-dark text-white px-6 py-2.5 rounded-xl shadow-sm font-bold transition-all flex items-center gap-2 cursor-pointer text-sm"
                      >
                        <span className="material-icons text-sm">save</span>{" "}
                        Save
                      </button>
                    </div>
                  </td>
                </tr>
              );
            })}

            {profiles.length === 0 && (
              <tr>
                <td colSpan={3} className="p-8 text-center text-gray-600">
                  There are no registered profiles
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default ManageProfiles;
