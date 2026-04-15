import type { Task } from "./Task";

export type Profile = {
  id: number;
  name: string;
  tasks: Task[];
};
