import { api } from "../api";
import {
  AlertRuleDto,
  AlertRuleCreateCommand,
  AlertRuleUpdateCommand,
} from "../../types/alert.types";
import { PagedResponseDto } from "../../types/common.types";

export const alertsApi = {
  getAlertRules: (page = 0, size = 10) => {
    return api.get<PagedResponseDto<AlertRuleDto>>("/alert-rules", {
      params: { page, size },
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
