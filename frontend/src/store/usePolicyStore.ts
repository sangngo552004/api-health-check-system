import { create } from "zustand";
import {
  CheckPolicyListParams,
  policiesApi,
} from "../services/api/policies.api";
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
  totalItems: number;
  totalPages: number;
  currentPage: number;
  lastQuery: CheckPolicyListParams;

  fetchPolicies: (query?: CheckPolicyListParams) => Promise<void>;
  createPolicy: (data: CheckPolicyCreateCommand) => Promise<void>;
  updatePolicy: (id: number, data: CheckPolicyUpdateCommand) => Promise<void>;
  deletePolicy: (id: number) => Promise<void>;
}

export const usePolicyStore = create<PolicyState>((set, get) => ({
  policies: [],
  loading: false,
  error: null,
  totalItems: 0,
  totalPages: 0,
  currentPage: 0,
  lastQuery: { page: 0, size: 10, sortBy: "createdAt", sortDir: "desc" },

  fetchPolicies: async (query = {}) => {
    set({ loading: true, error: null });
    try {
      const previous = get().lastQuery;
      const nextQuery = {
        ...previous,
        ...query,
        page: query.page ?? previous.page ?? 0,
        size: query.size ?? previous.size ?? 10,
      };
      const res = await policiesApi.getPolicies(nextQuery);
      set({
        policies: res.items,
        totalItems: res.totalItems,
        totalPages: res.totalPages,
        currentPage: nextQuery.page ?? 0,
        lastQuery: nextQuery,
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
      await get().fetchPolicies();
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
      await get().fetchPolicies();
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
      await get().fetchPolicies();
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi xóa Policy"),
        loading: false,
      });
      throw error;
    }
  },
}));
