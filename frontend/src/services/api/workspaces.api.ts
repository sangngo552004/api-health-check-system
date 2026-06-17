import { api } from "../api";
import {
  AdminUserCreateCommand,
  AdminUserDto,
  AdminUserUpdateCommand,
  AdminWorkspaceCreateCommand,
  AdminWorkspaceUpdateCommand,
  WorkspaceCreateCommand,
  WorkspaceDto,
  WorkspaceMemberDto,
  WorkspaceUpdateCommand,
} from "../../types/workspace.types";
import { PagedResponseDto } from "../../types/common.types";

export const workspacesApi = {
  getMyWorkspaces: () => {
    return api.get<WorkspaceDto[]>("/workspaces/my");
  },
  getWorkspaceById: (id: number) => {
    return api.get<WorkspaceDto>(`/workspaces/${id}`);
  },
  getAllWorkspacesForAdmin: (params?: {
    page?: number;
    size?: number;
    search?: string;
    isActive?: boolean;
    ownerId?: number;
    sortBy?: string;
    sortDir?: "asc" | "desc";
  }) => {
    return api.get<PagedResponseDto<WorkspaceDto>>("/admin/workspaces", {
      params,
    });
  },
  getAdminUsers: (params?: {
    page?: number;
    size?: number;
    search?: string;
    role?: "SUPER_ADMIN" | "USER";
    isActive?: boolean;
    sortBy?: string;
    sortDir?: "asc" | "desc";
  }) => {
    return api.get<PagedResponseDto<AdminUserDto>>("/admin/users", { params });
  },
  getAdminUserById: (userId: number) => {
    return api.get<AdminUserDto>(`/admin/users/${userId}`);
  },
  createAdminUser: (command: AdminUserCreateCommand) => {
    return api.post<AdminUserDto, AdminUserCreateCommand>(
      "/admin/users",
      command,
    );
  },
  updateAdminUser: (userId: number, command: AdminUserUpdateCommand) => {
    return api.put<AdminUserDto, AdminUserUpdateCommand>(
      `/admin/users/${userId}`,
      command,
    );
  },
  deleteAdminUser: (userId: number) => {
    return api.delete<void>(`/admin/users/${userId}`);
  },
  getAdminWorkspaceById: (workspaceId: number) => {
    return api.get<WorkspaceDto>(`/admin/workspaces/${workspaceId}`);
  },
  createAdminWorkspace: (command: AdminWorkspaceCreateCommand) => {
    return api.post<WorkspaceDto, AdminWorkspaceCreateCommand>(
      "/admin/workspaces",
      command,
    );
  },
  updateAdminWorkspace: (
    workspaceId: number,
    command: AdminWorkspaceUpdateCommand,
  ) => {
    return api.put<WorkspaceDto, AdminWorkspaceUpdateCommand>(
      `/admin/workspaces/${workspaceId}`,
      command,
    );
  },
  deleteAdminWorkspace: (workspaceId: number) => {
    return api.delete<void>(`/admin/workspaces/${workspaceId}`);
  },
  createWorkspace: (command: WorkspaceCreateCommand) => {
    return api.post<WorkspaceDto, WorkspaceCreateCommand>(
      "/workspaces",
      command,
    );
  },
  updateWorkspace: (workspaceId: number, command: WorkspaceUpdateCommand) => {
    return api.put<WorkspaceDto, WorkspaceUpdateCommand>(
      `/workspaces/${workspaceId}`,
      command,
    );
  },
  deleteWorkspace: (workspaceId: number) => {
    return api.delete<void>(`/workspaces/${workspaceId}`);
  },
  getMembers: (workspaceId: number) => {
    return api.get<WorkspaceMemberDto[]>(`/workspaces/${workspaceId}/members`);
  },
  addMember: (workspaceId: number, userId: number) => {
    // Controller đang dùng RequestParam thay vì Body
    return api.post<void>(`/workspaces/${workspaceId}/members`, null, {
      params: { userId },
    });
  },
  removeMember: (workspaceId: number, userId: number) => {
    return api.delete<void>(`/workspaces/${workspaceId}/members/${userId}`);
  },
};
