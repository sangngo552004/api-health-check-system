export interface WorkspaceDto {
  id: number;
  name: string;
  description: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Workspace {
  id: number;
  name: string;
  description?: string;
  slug: string;
  ownerId: number;
  isActive: boolean;
}

export type WorkspaceRole = "ADMIN" | "MEMBER";

export interface WorkspaceMemberDto {
  userId: number;
  username: string;
  email: string;
  role: WorkspaceRole;
  joinedAt: string;
}

export interface WorkspaceCreateCommand {
  name: string;
  description?: string;
  isActive: boolean;
}

export interface WorkspaceUpdateCommand extends WorkspaceCreateCommand {
  id: number;
}
