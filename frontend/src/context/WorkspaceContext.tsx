import React, { useCallback, useEffect, useState } from "react";
import { api } from "../services/api";
import { Workspace } from "../types/workspace.types";
import { useAuth } from "./useAuth";
import { getErrorMessage } from "../utils/error";
import { WorkspaceContext } from "./workspace-context";

export const WorkspaceProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const { user } = useAuth();
  const [workspaces, setWorkspaces] = useState<Workspace[]>([]);
  const [activeWorkspace, setActiveWorkspace] = useState<Workspace | null>(
    null,
  );
  const [loadingWorkspaces, setLoadingWorkspaces] = useState(false);

  const fetchWorkspaces = useCallback(async (): Promise<Workspace[]> => {
    if (!user || user.role !== "USER") {
      setWorkspaces([]);
      setActiveWorkspace(null);
      localStorage.removeItem("workspace_id");
      return [];
    }

    setLoadingWorkspaces(true);
    try {
      const data = await api.get<Workspace[]>("/workspaces/my");
      setWorkspaces(data || []);

      // Determine active workspace
      if (data && data.length > 0) {
        const savedId = localStorage.getItem("workspace_id");
        const matched = data.find((ws) => String(ws.id) === savedId);
        if (matched) {
          setActiveWorkspace(matched);
        } else {
          setActiveWorkspace(data[0]);
          localStorage.setItem("workspace_id", String(data[0].id));
        }
      } else {
        setActiveWorkspace(null);
        localStorage.removeItem("workspace_id");
      }
      return data || [];
    } catch (error) {
      console.error(
        "Failed to load workspaces:",
        getErrorMessage(error, "Unknown workspace error"),
      );
      return [];
    } finally {
      setLoadingWorkspaces(false);
    }
  }, [user]);

  useEffect(() => {
    if (user?.role === "USER") {
      void fetchWorkspaces();
    } else {
      setWorkspaces([]);
      setActiveWorkspace(null);
      localStorage.removeItem("workspace_id");
    }
  }, [fetchWorkspaces, user]);

  const selectWorkspace = (id: number) => {
    const matched = workspaces.find((ws) => ws.id === id);
    if (matched) {
      setActiveWorkspace(matched);
      localStorage.setItem("workspace_id", String(matched.id));
      // Trigger a window event to let other services know workspace has changed
      window.dispatchEvent(new Event("workspace-changed"));
    }
  };

  return (
    <WorkspaceContext.Provider
      value={{
        workspaces,
        activeWorkspace,
        loadingWorkspaces,
        selectWorkspace,
        fetchWorkspaces,
      }}
    >
      {children}
    </WorkspaceContext.Provider>
  );
};
