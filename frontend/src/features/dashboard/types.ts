import type { EndpointStatus } from "../../types/endpoint.types";

export interface IncidentSummaryDto {
  id: number;
  endpointId: number;
  endpointName: string;
  startedAt: string;
  reason: string;
  severity: string;
}

export interface WorkspaceDashboardStatsDto {
  workspaceId: number;
  totalEndpoints: number;
  upEndpoints: number;
  downEndpoints: number;
  degradedEndpoints: number;
  openIncidentsCount: number;
  activeIncidents: IncidentSummaryDto[];
}

export interface DashboardEndpointSummary {
  id: number;
  name: string;
  url: string;
  status: EndpointStatus;
}

export interface EndpointLatencyDto {
  checkedAt: string;
  responseTimeMillis: number;
  success: boolean;
}

export interface LatencyChartLine {
  key: string;
  color: string;
}

export interface LatencyChartPoint {
  time: string;
  [endpointName: string]: number | string | undefined;
}
