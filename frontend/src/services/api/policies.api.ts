import { api } from "../api";
import {
  CheckPolicyDto,
  CheckPolicyCreateCommand,
  CheckPolicyUpdateCommand,
} from "../../types/policy.types";
import { PagedResponseDto } from "../../types/common.types";

export interface CheckPolicyListParams {
  page?: number;
  size?: number;
  search?: string;
  expectedStatusCode?: number;
  hasDegradedResponseTimeThreshold?: boolean;
  sortBy?: string;
  sortDir?: "asc" | "desc";
}

export const policiesApi = {
  getPolicies: (params?: CheckPolicyListParams) => {
    return api.get<PagedResponseDto<CheckPolicyDto>>("/check-policies", {
      params: params as Record<string, string | number | boolean | undefined>,
    });
  },

  getPolicyById: (id: number) => {
    return api.get<CheckPolicyDto>(`/check-policies/${id}`);
  },

  createPolicy: (data: CheckPolicyCreateCommand) => {
    return api.post<CheckPolicyDto>("/check-policies", data);
  },

  updatePolicy: (id: number, data: CheckPolicyUpdateCommand) => {
    return api.put<CheckPolicyDto>(`/check-policies/${id}`, data);
  },

  deletePolicy: (id: number) => {
    return api.delete<void>(`/check-policies/${id}`);
  },
};
