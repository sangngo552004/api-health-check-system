import { api } from "../api";
import {
  EndpointDto,
  EndpointCreateCommand,
  EndpointUpdateCommand,
} from "../../types/endpoint.types";
import { PagedResponseDto } from "../../types/common.types";

export const endpointsApi = {
  getEndpoints: (page = 0, size = 10) => {
    return api.get<PagedResponseDto<EndpointDto>>("/endpoints", {
      params: { page, size },
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
