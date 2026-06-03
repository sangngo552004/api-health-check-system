import { api } from "../api";
import {
  CheckPolicyDto,
  CheckPolicyCreateCommand,
  CheckPolicyUpdateCommand,
} from "../../types/policy.types";
import { PagedResponseDto } from "../../types/common.types";

export const policiesApi = {
  getPolicies: (page = 0, size = 10) => {
    return api.get<PagedResponseDto<CheckPolicyDto>>("/check-policies", {
      params: { page, size },
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
