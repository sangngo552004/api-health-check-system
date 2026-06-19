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
  latencySeries: DashboardLatencySeriesDto[];
}

export interface DashboardStatsSummaryDto {
  workspaceId: number;
  totalEndpoints: number;
  upEndpoints: number;
  downEndpoints: number;
  degradedEndpoints: number;
}

export interface DashboardActiveIncidentsDto {
  workspaceId: number;
  openIncidentsCount: number;
  incidents: IncidentSummaryDto[];
}

export interface DashboardLatencySeriesDto {
  endpointId: number;
  endpointName: string;
  points: EndpointLatencyDto[];
}

export interface DashboardLatencyChartDto {
  workspaceId: number;
  series: DashboardLatencySeriesDto[];
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
