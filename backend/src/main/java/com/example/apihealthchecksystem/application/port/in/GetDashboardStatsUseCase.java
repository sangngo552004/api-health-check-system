package com.example.apihealthchecksystem.application.port.in;

import com.example.apihealthchecksystem.application.dto.response.DashboardActiveIncidentsDto;
import com.example.apihealthchecksystem.application.dto.response.DashboardLatencyChartDto;
import com.example.apihealthchecksystem.application.dto.response.DashboardStatsSummaryDto;
import com.example.apihealthchecksystem.application.dto.response.EndpointLatencyDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDashboardStatsDto;
import java.util.List;

public interface GetDashboardStatsUseCase {
  DashboardStatsSummaryDto getDashboardSummary(Long workspaceId);

  DashboardActiveIncidentsDto getActiveIncidents(Long workspaceId);

  DashboardLatencyChartDto getLatencyChart(Long workspaceId);

  WorkspaceDashboardStatsDto getWorkspaceDashboardStats(Long workspaceId);

  List<EndpointLatencyDto> getEndpointLatencyHistory(Long workspaceId, Long endpointId, int limit);
}
