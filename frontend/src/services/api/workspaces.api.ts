import { api } from "../api";
import {
  WorkspaceDto,
  WorkspaceMemberDto,
  WorkspaceRole,
} from "../../types/workspace.types";

export const workspacesApi = {
  getMyWorkspaces: () => {
    return api.get<WorkspaceDto[]>("/workspaces/my");
  },
  getWorkspaceById: (id: number) => {
    return api.get<WorkspaceDto>(`/workspaces/${id}`);
  },
  getMembers: (workspaceId: number) => {
    return api.get<WorkspaceMemberDto[]>(`/workspaces/${workspaceId}/members`);
  },
  addMember: (workspaceId: number, userId: number, role: WorkspaceRole) => {
    // Controller đang dùng RequestParam thay vì Body
    return api.post<void>(`/workspaces/${workspaceId}/members`, null, {
      params: { userId, role },
    });
  },
  removeMember: (workspaceId: number, userId: number) => {
    return api.delete<void>(`/workspaces/${workspaceId}/members/${userId}`);
  },
};
