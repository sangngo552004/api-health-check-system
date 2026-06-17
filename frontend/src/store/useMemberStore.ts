import { create } from "zustand";
import { workspacesApi } from "../services/api/workspaces.api";
import { WorkspaceMemberDto } from "../types/workspace.types";
import { getErrorMessage } from "../utils/error";

interface MemberState {
  members: WorkspaceMemberDto[];
  loading: boolean;
  error: string | null;

  fetchMembers: (workspaceId: number) => Promise<void>;
  addMember: (workspaceId: number, userId: number) => Promise<void>;
  removeMember: (workspaceId: number, userId: number) => Promise<void>;
}

export const useMemberStore = create<MemberState>((set, get) => ({
  members: [],
  loading: false,
  error: null,

  fetchMembers: async (workspaceId: number) => {
    set({ loading: true, error: null });
    try {
      const res = await workspacesApi.getMembers(workspaceId);
      set({ members: res, loading: false });
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi tải danh sách thành viên"),
        loading: false,
      });
    }
  },

  addMember: async (workspaceId: number, userId: number) => {
    set({ loading: true, error: null });
    try {
      await workspacesApi.addMember(workspaceId, userId);
      await get().fetchMembers(workspaceId);
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi thêm thành viên"),
        loading: false,
      });
      throw error;
    }
  },

  removeMember: async (workspaceId: number, userId: number) => {
    set({ loading: true, error: null });
    try {
      await workspacesApi.removeMember(workspaceId, userId);
      await get().fetchMembers(workspaceId);
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi xóa thành viên"),
        loading: false,
      });
      throw error;
    }
  },
}));
