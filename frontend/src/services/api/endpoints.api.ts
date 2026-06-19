import { api } from "../api";
import {
  EndpointDto,
  EndpointCreateCommand,
  EndpointUpdateCommand,
} from "../../types/endpoint.types";
import { PagedResponseDto } from "../../types/common.types";

export interface EndpointListParams {
  page?: number;
  size?: number;
  search?: string;
  environment?: string;
  status?: string;
  method?: string;
  checkType?: string;
  isActive?: boolean;
  sortBy?: string;
  sortDir?: "asc" | "desc";
}

export const endpointsApi = {
  getEndpoints: (params?: EndpointListParams) => {
    return api.get<PagedResponseDto<EndpointDto>>("/endpoints", {
      params: params as Record<string, string | number | boolean | undefined>,
    });
  },

  getEndpointById: (id: number) => {
    return api.get<EndpointDto>(`/endpoints/${id}`);
  },

  createEndpoint: (data: EndpointCreateCommand) => {
    return api.post<EndpointDto>("/endpoints", data);
  },

  updateEndpoint: (id: number, data: EndpointUpdateCommand) => {
    return api.put<EndpointDto>(`/endpoints/${id}`, data);
  },

  deleteEndpoint: (id: number) => {
    return api.delete<void>(`/endpoints/${id}`);
  },
};
