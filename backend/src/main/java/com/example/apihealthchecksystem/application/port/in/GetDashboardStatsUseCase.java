package com.example.apihealthchecksystem.application.port.in;

import com.example.apihealthchecksystem.application.dto.response.EndpointLatencyDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDashboardStatsDto;
import java.util.List;

public interface GetDashboardStatsUseCase {
  WorkspaceDashboardStatsDto getWorkspaceDashboardStats(Long workspaceId);

  List<EndpointLatencyDto> getEndpointLatencyHistory(Long workspaceId, Long endpointId, int limit);
}
