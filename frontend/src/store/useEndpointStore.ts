import { create } from "zustand";
import { endpointsApi } from "../services/api/endpoints.api";
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
  totalElements: number;
  totalPages: number;
  currentPage: number;

  fetchEndpoints: (page?: number, size?: number) => Promise<void>;
  createEndpoint: (data: EndpointCreateCommand) => Promise<void>;
  updateEndpoint: (id: number, data: EndpointUpdateCommand) => Promise<void>;
  deleteEndpoint: (id: number) => Promise<void>;
}

export const useEndpointStore = create<EndpointState>((set, get) => ({
  endpoints: [],
  loading: false,
  error: null,
  totalElements: 0,
  totalPages: 0,
  currentPage: 0,

  fetchEndpoints: async (page = 0, size = 10) => {
    set({ loading: true, error: null });
    try {
      const res = await endpointsApi.getEndpoints(page, size);
      set({
        endpoints: res.content,
        totalElements: res.totalElements,
        totalPages: res.totalPages,
        currentPage: page,
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
      // Reload current page
      await get().fetchEndpoints(get().currentPage);
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
      await get().fetchEndpoints(get().currentPage);
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
      await get().fetchEndpoints(get().currentPage);
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi xóa Endpoint"),
        loading: false,
      });
      throw error;
    }
  },
}));
