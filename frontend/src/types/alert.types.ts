export type AlertRuleType = "STATUS_CHANGE" | "LATENCY_SPIKE" | "ERROR_RATE";
export type AlertOperator = "GREATER_THAN" | "LESS_THAN" | "EQUAL";

export interface AlertRuleDto {
  id: number;
  name: string;
  ruleType: AlertRuleType;
  operator: AlertOperator;
  thresholdValue: number;
  isActive: boolean;
  contactGroupIds: number[];
  overrideDefaultContacts: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface AlertRuleCreateCommand {
  name: string;
  ruleType: AlertRuleType;
  operator: AlertOperator;
  thresholdValue: number;
  isActive: boolean;
  contactGroupIds: number[];
  overrideDefaultContacts: boolean;
}

export interface AlertRuleUpdateCommand extends AlertRuleCreateCommand {
  id: number;
}
