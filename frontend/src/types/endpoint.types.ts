export type HttpMethod = "GET" | "POST" | "PUT" | "DELETE" | "PATCH" | "HEAD";
export type Environment = "PRODUCTION" | "STAGING" | "DEVELOPMENT";
export type CheckType = "HTTP" | "TCP";
export type EndpointStatus = "UP" | "DOWN" | "DEGRADED" | "UNKNOWN";

export interface EndpointDto {
  id: number;
  name: string;
  url: string;
  method: HttpMethod;
  environment: Environment | string;
  checkType: CheckType;
  workspaceId: number;
  policyId: number;
  expectedStatusCode?: number;
  isActive: boolean;
  status: EndpointStatus;
  lastCheckedAt?: string;
  alertRuleIds: number[];
  tags: string[];
  headers: Record<string, string>;
  requestBody?: string;
  intervalSeconds?: number;
  timeoutMillis?: number;
  retryCount?: number;
  degradedResponseTimeMillis?: number;
  nextRunAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface EndpointCreateCommand {
  name: string;
  url: string;
  method: HttpMethod;
  environment?: Environment | string;
  checkType: CheckType;
  policyId: number;
  alertRuleIds: number[];
  tags: string[];
  headers: Record<string, string>;
  requestBody?: string;
}

export interface EndpointUpdateCommand extends EndpointCreateCommand {
  id: number;
  isActive?: boolean;
}
