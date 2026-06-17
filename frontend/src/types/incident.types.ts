export type IncidentStatus = "OPEN" | "RESOLVED" | "CLOSED";
export type IncidentSeverity = "CRITICAL" | "WARNING" | "INFO";

export interface IncidentDto {
  id: number;
  endpointId: number;
  endpointName: string;
  workspaceId: number;
  startedAt: string;
  resolvedAt: string | null;
  status: IncidentStatus;
  reason: string | null;
  failureCount: number | null;
  severity: IncidentSeverity | null;
  rootCause: string | null;
  failingResultIds: number[] | null;
}
