import { create } from "zustand";
import {
  AlertRuleListParams,
  alertsApi,
} from "../services/api/alerts.api";
import {
  AlertRuleDto,
  AlertRuleCreateCommand,
  AlertRuleUpdateCommand,
} from "../types/alert.types";
import { getErrorMessage } from "../utils/error";

interface AlertState {
  alertRules: AlertRuleDto[];
  loading: boolean;
  error: string | null;
  totalItems: number;
  totalPages: number;
  currentPage: number;
  lastQuery: AlertRuleListParams;

  fetchAlertRules: (query?: AlertRuleListParams) => Promise<void>;
  createAlertRule: (data: AlertRuleCreateCommand) => Promise<void>;
  updateAlertRule: (id: number, data: AlertRuleUpdateCommand) => Promise<void>;
  deleteAlertRule: (id: number) => Promise<void>;
}

export const useAlertStore = create<AlertState>((set, get) => ({
  alertRules: [],
  loading: false,
  error: null,
  totalItems: 0,
  totalPages: 0,
  currentPage: 0,
  lastQuery: { page: 0, size: 10, sortBy: "createdAt", sortDir: "desc" },

  fetchAlertRules: async (query = {}) => {
    set({ loading: true, error: null });
    try {
      const previous = get().lastQuery;
      const nextQuery = {
        ...previous,
        ...query,
        page: query.page ?? previous.page ?? 0,
        size: query.size ?? previous.size ?? 10,
      };
      const res = await alertsApi.getAlertRules(nextQuery);
      set({
        alertRules: res.items,
        totalItems: res.totalItems,
        totalPages: res.totalPages,
        currentPage: nextQuery.page ?? 0,
        lastQuery: nextQuery,
        loading: false,
      });
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi tải danh sách Alert Rules"),
        loading: false,
      });
    }
  },

  createAlertRule: async (data: AlertRuleCreateCommand) => {
    set({ loading: true, error: null });
    try {
      await alertsApi.createAlertRule(data);
      await get().fetchAlertRules();
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi tạo Alert Rule"),
        loading: false,
      });
      throw error;
    }
  },

  updateAlertRule: async (id: number, data: AlertRuleUpdateCommand) => {
    set({ loading: true, error: null });
    try {
      await alertsApi.updateAlertRule(id, data);
      await get().fetchAlertRules();
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi cập nhật Alert Rule"),
        loading: false,
      });
      throw error;
    }
  },

  deleteAlertRule: async (id: number) => {
    set({ loading: true, error: null });
    try {
      await alertsApi.deleteAlertRule(id);
      await get().fetchAlertRules();
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi xóa Alert Rule"),
        loading: false,
      });
      throw error;
    }
  },
}));
