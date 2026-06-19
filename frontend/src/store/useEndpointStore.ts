import { create } from "zustand";
import {
  EndpointListParams,
  endpointsApi,
} from "../services/api/endpoints.api";
import {
  EndpointDto,
  EndpointCreateCommand,
  EndpointUpdateCommand,
} from "../types/endpoint.types";
import { getErrorMessage } from "../utils/error";

interface EndpointState {
  endpoints: EndpointDto[];
  loading: boolean;
  error: string | null;
  totalItems: number;
  totalPages: number;
  currentPage: number;
  lastQuery: EndpointListParams;

  fetchEndpoints: (query?: EndpointListParams) => Promise<void>;
  createEndpoint: (data: EndpointCreateCommand) => Promise<void>;
  updateEndpoint: (id: number, data: EndpointUpdateCommand) => Promise<void>;
  deleteEndpoint: (id: number) => Promise<void>;
}

export const useEndpointStore = create<EndpointState>((set, get) => ({
  endpoints: [],
  loading: false,
  error: null,
  totalItems: 0,
  totalPages: 0,
  currentPage: 0,
  lastQuery: { page: 0, size: 10, sortBy: "createdAt", sortDir: "desc" },

  fetchEndpoints: async (query = {}) => {
    set({ loading: true, error: null });
    try {
      const previous = get().lastQuery;
      const nextQuery = {
        ...previous,
        ...query,
        page: query.page ?? previous.page ?? 0,
        size: query.size ?? previous.size ?? 10,
      };
      const res = await endpointsApi.getEndpoints(nextQuery);
      set({
        endpoints: res.items,
        totalItems: res.totalItems,
        totalPages: res.totalPages,
        currentPage: nextQuery.page ?? 0,
        lastQuery: nextQuery,
        loading: false,
      });
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi tải danh sách Endpoints"),
        loading: false,
      });
    }
  },

  createEndpoint: async (data: EndpointCreateCommand) => {
    set({ loading: true, error: null });
    try {
      await endpointsApi.createEndpoint(data);
      await get().fetchEndpoints();
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi tạo Endpoint"),
        loading: false,
      });
      throw error;
    }
  },

  updateEndpoint: async (id: number, data: EndpointUpdateCommand) => {
    set({ loading: true, error: null });
    try {
      await endpointsApi.updateEndpoint(id, data);
      await get().fetchEndpoints();
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi cập nhật Endpoint"),
        loading: false,
      });
      throw error;
    }
  },

  deleteEndpoint: async (id: number) => {
    set({ loading: true, error: null });
    try {
      await endpointsApi.deleteEndpoint(id);
      await get().fetchEndpoints();
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi xóa Endpoint"),
        loading: false,
      });
      throw error;
    }
  },
}));
