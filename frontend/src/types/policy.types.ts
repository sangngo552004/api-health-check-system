export interface CheckPolicyDto {
  id: number;
  name: string;
  intervalSeconds: number;
  timeoutMillis: number;
  retryCount: number;
  degradedResponseTimeMillis?: number;
  expectedStatusCode?: number;
  expectedResponseBody?: string;
  responseRegex?: string;
}

export interface CheckPolicyCreateCommand {
  name: string;
  intervalSeconds?: number;
  timeoutMillis?: number;
  retryCount?: number;
  degradedResponseTimeMillis?: number;
  expectedStatusCode?: number;
  expectedResponseBody?: string;
  responseRegex?: string;
}

export interface CheckPolicyUpdateCommand extends CheckPolicyCreateCommand {
  id: number;
}
