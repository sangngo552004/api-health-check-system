export type AlertRuleType =
  | "CONSECUTIVE_FAILURE"
  | "RESPONSE_TIME"
  | "HTTP_STATUS_CODE";

export type AlertOperator = "GT" | "GTE" | "LT" | "LTE" | "EQ" | "NE";
export type AlertSeverity = "INFO" | "WARNING" | "CRITICAL";

export interface AlertRuleDto {
  id: number;
  name: string;
  ruleType: AlertRuleType;
  operator: AlertOperator | null;
  thresholdValue: number;
  severity: AlertSeverity;
  workspaceId: number;
  isActive: boolean;
  contactGroupIds: number[];
}

export interface AlertRuleCreateCommand {
  name: string;
  ruleType: AlertRuleType;
  operator?: AlertOperator | null;
  thresholdValue: number;
  severity: AlertSeverity;
  contactGroupIds: number[];
}

export interface AlertRuleUpdateCommand extends AlertRuleCreateCommand {
  id: number;
  isActive?: boolean;
}
