export type HttpMethod = "GET" | "POST" | "PUT" | "DELETE" | "PATCH" | "HEAD";
export type Environment = "PRODUCTION" | "STAGING" | "DEVELOPMENT";
export type CheckType = "HTTP" | "TCP";
export type EndpointStatus = "UP" | "DOWN" | "DEGRADED" | "UNKNOWN";

export interface EndpointDto {
  id: number;
  name: string;
  url: string;
  method: HttpMethod;
  environment: Environment;
  checkType: CheckType;
  isActive: boolean;
  status: EndpointStatus;
  policyId?: number;
  alertRuleIds: number[];
  tags: string[];
  headers: Record<string, string>;
  requestBody?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface EndpointCreateCommand {
  name: string;
  url: string;
  method: HttpMethod;
  environment: Environment;
  checkType: CheckType;
  isActive: boolean;
  policyId?: number;
  alertRuleIds: number[];
  tags: string[];
  headers: Record<string, string>;
  requestBody?: string;
}

export interface EndpointUpdateCommand extends EndpointCreateCommand {
  id: number;
}
