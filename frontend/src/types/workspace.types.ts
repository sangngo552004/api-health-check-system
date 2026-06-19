import type { AppRole } from "../context/auth-context";

export interface WorkspaceDto {
  id: number;
  name: string;
  description?: string;
  slug: string;
  ownerId: number;
  isActive: boolean;
  createdAt: string;
}

export interface Workspace {
  id: number;
  name: string;
  description?: string;
  slug: string;
  ownerId: number;
  isActive: boolean;
}

export interface WorkspaceMemberDto {
  userId: number;
  username: string;
  email: string;
  joinedAt: string;
}

export interface WorkspaceCreateCommand {
  name: string;
  description?: string;
  slug: string;
}

export interface WorkspaceUpdateCommand {
  id: number;
  name: string;
  description?: string;
  isActive?: boolean;
}

export interface AdminUserDto {
  id: number;
  username: string;
  email?: string;
  phoneNumber?: string;
  role: AppRole;
  isActive?: boolean;
}

export interface AdminUserCreateCommand {
  username: string;
  email?: string;
  phoneNumber?: string;
  password: string;
  role: AppRole;
  isActive?: boolean;
  requiresPasswordChange?: boolean;
}

export interface AdminUserUpdateCommand {
  username: string;
  email?: string;
  phoneNumber?: string;
  password?: string;
  role: AppRole;
  isActive?: boolean;
  requiresPasswordChange?: boolean;
}

export interface AdminWorkspaceCreateCommand {
  name: string;
  description?: string;
  slug: string;
  ownerId?: number;
  isActive?: boolean;
}

export interface AdminWorkspaceUpdateCommand {
  name: string;
  description?: string;
  slug: string;
  ownerId?: number;
  isActive?: boolean;
}
