import { api } from "../api";
import {
  AlertRuleDto,
  AlertRuleCreateCommand,
  AlertRuleUpdateCommand,
} from "../../types/alert.types";
import { PagedResponseDto } from "../../types/common.types";

export interface AlertRuleListParams {
  page?: number;
  size?: number;
  search?: string;
  ruleType?: string;
  operator?: string;
  isActive?: boolean;
  sortBy?: string;
  sortDir?: "asc" | "desc";
}

export const alertsApi = {
  getAlertRules: (params?: AlertRuleListParams) => {
    return api.get<PagedResponseDto<AlertRuleDto>>("/alert-rules", {
      params: params as Record<string, string | number | boolean | undefined>,
    });
  },

  getAlertRuleById: (id: number) => {
    return api.get<AlertRuleDto>(`/alert-rules/${id}`);
  },

  createAlertRule: (data: AlertRuleCreateCommand) => {
    return api.post<AlertRuleDto>("/alert-rules", data);
  },

  updateAlertRule: (id: number, data: AlertRuleUpdateCommand) => {
    return api.put<AlertRuleDto>(`/alert-rules/${id}`, data);
  },

  deleteAlertRule: (id: number) => {
    return api.delete<void>(`/alert-rules/${id}`);
  },
};
