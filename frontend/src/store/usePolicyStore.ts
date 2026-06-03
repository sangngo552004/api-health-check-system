import { create } from "zustand";
import { policiesApi } from "../services/api/policies.api";
import {
  CheckPolicyDto,
  CheckPolicyCreateCommand,
  CheckPolicyUpdateCommand,
} from "../types/policy.types";
import { getErrorMessage } from "../utils/error";

interface PolicyState {
  policies: CheckPolicyDto[];
  loading: boolean;
  error: string | null;
  totalElements: number;
  totalPages: number;
  currentPage: number;

  fetchPolicies: (page?: number, size?: number) => Promise<void>;
  createPolicy: (data: CheckPolicyCreateCommand) => Promise<void>;
  updatePolicy: (id: number, data: CheckPolicyUpdateCommand) => Promise<void>;
  deletePolicy: (id: number) => Promise<void>;
}

export const usePolicyStore = create<PolicyState>((set, get) => ({
  policies: [],
  loading: false,
  error: null,
  totalElements: 0,
  totalPages: 0,
  currentPage: 0,

  fetchPolicies: async (page = 0, size = 10) => {
    set({ loading: true, error: null });
    try {
      const res = await policiesApi.getPolicies(page, size);
      set({
        policies: res.content,
        totalElements: res.totalElements,
        totalPages: res.totalPages,
        currentPage: page,
        loading: false,
      });
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi tải danh sách Policies"),
        loading: false,
      });
    }
  },

  createPolicy: async (data: CheckPolicyCreateCommand) => {
    set({ loading: true, error: null });
    try {
      await policiesApi.createPolicy(data);
      await get().fetchPolicies(get().currentPage);
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi tạo Policy"),
        loading: false,
      });
      throw error;
    }
  },

  updatePolicy: async (id: number, data: CheckPolicyUpdateCommand) => {
    set({ loading: true, error: null });
    try {
      await policiesApi.updatePolicy(id, data);
      await get().fetchPolicies(get().currentPage);
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi cập nhật Policy"),
        loading: false,
      });
      throw error;
    }
  },

  deletePolicy: async (id: number) => {
    set({ loading: true, error: null });
    try {
      await policiesApi.deletePolicy(id);
      await get().fetchPolicies(get().currentPage);
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi xóa Policy"),
        loading: false,
      });
      throw error;
    }
  },
}));
