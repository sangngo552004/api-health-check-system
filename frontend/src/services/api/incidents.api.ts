import { api } from "../api";
import { PagedResponseDto } from "../../types/common.types";
import {
  IncidentDto,
  IncidentHealthCheckResultDto,
  IncidentStatus,
} from "../../types/incident.types";

interface IncidentFilterParams {
  page?: number;
  size?: number;
  status?: IncidentStatus | "";
  endpointId?: number;
}

export const incidentsApi = {
  getIncidents: ({
    page = 0,
    size = 10,
    status,
    endpointId,
  }: IncidentFilterParams) => {
    const params: Record<string, string | number> = { page, size };
    if (status) {
      params.status = status;
    }
    if (endpointId !== undefined) {
      params.endpointId = endpointId;
    }

    return api.get<PagedResponseDto<IncidentDto>>("/incidents", { params });
  },

  getIncidentById: (id: number) => {
    return api.get<IncidentDto>(`/incidents/${id}`);
  },

  getIncidentResults: (id: number) => {
    return api.get<IncidentHealthCheckResultDto[]>(`/incidents/${id}/results`);
  },

  updateRootCause: (id: number, rootCause: string | null) => {
    return api.patch<IncidentDto, { rootCause: string | null }>(
      `/incidents/${id}/root-cause`,
      { rootCause },
    );
  },
};
