import { createContext } from "react";
import type { Workspace } from "../types/workspace.types";

export interface WorkspaceContextType {
  workspaces: Workspace[];
  activeWorkspace: Workspace | null;
  loadingWorkspaces: boolean;
  selectWorkspace: (id: number) => void;
  fetchWorkspaces: () => Promise<Workspace[]>;
}

export const WorkspaceContext = createContext<WorkspaceContextType | undefined>(
  undefined,
);
