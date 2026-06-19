import { create } from "zustand";
import { incidentsApi } from "../services/api/incidents.api";
import { getErrorMessage } from "../utils/error";
import { IncidentDto, IncidentStatus } from "../types/incident.types";

interface IncidentState {
  incidents: IncidentDto[];
  selectedIncident: IncidentDto | null;
  loading: boolean;
  detailLoading: boolean;
  error: string | null;
  totalItems: number;
  totalPages: number;
  currentPage: number;
  filters: {
    status: IncidentStatus | "";
    endpointId?: number;
  };
  fetchIncidents: (page?: number, size?: number) => Promise<void>;
  fetchIncidentById: (id: number) => Promise<void>;
  replaceIncident: (incident: IncidentDto) => void;
  setFilters: (filters: {
    status: IncidentStatus | "";
    endpointId?: number;
  }) => void;
  clearSelectedIncident: () => void;
}

export const useIncidentStore = create<IncidentState>((set, get) => ({
  incidents: [],
  selectedIncident: null,
  loading: false,
  detailLoading: false,
  error: null,
  totalItems: 0,
  totalPages: 0,
  currentPage: 0,
  filters: {
    status: "",
  },

  fetchIncidents: async (page = 0, size = 10) => {
    set({ loading: true, error: null });
    try {
      const res = await incidentsApi.getIncidents({
        page,
        size,
        ...get().filters,
      });
      set({
        incidents: res.items,
        totalItems: res.totalItems,
        totalPages: res.totalPages,
        currentPage: page,
        loading: false,
      });
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi tải danh sách incidents"),
        loading: false,
      });
    }
  },

  fetchIncidentById: async (id: number) => {
    set({ detailLoading: true, error: null });
    try {
      const incident = await incidentsApi.getIncidentById(id);
      set({ selectedIncident: incident, detailLoading: false });
    } catch (error) {
      set({
        error: getErrorMessage(error, "Lỗi khi tải chi tiết incident"),
        detailLoading: false,
      });
    }
  },

  replaceIncident: (incident) =>
    set((state) => ({
      selectedIncident:
        state.selectedIncident?.id === incident.id
          ? incident
          : state.selectedIncident,
      incidents: state.incidents.map((item) =>
        item.id === incident.id ? { ...item, ...incident } : item,
      ),
    })),

  setFilters: (filters) => set({ filters, currentPage: 0 }),

  clearSelectedIncident: () => set({ selectedIncident: null }),
}));
