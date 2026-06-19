package com.example.apihealthchecksystem.delivery.rest;

import com.example.apihealthchecksystem.application.dto.response.DashboardActiveIncidentsDto;
import com.example.apihealthchecksystem.application.dto.response.DashboardLatencyChartDto;
import com.example.apihealthchecksystem.application.dto.response.DashboardStatsSummaryDto;
import com.example.apihealthchecksystem.application.dto.response.EndpointLatencyDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDashboardStatsDto;
import com.example.apihealthchecksystem.application.port.in.GetDashboardStatsUseCase;
import com.example.apihealthchecksystem.delivery.rest.common.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final GetDashboardStatsUseCase dashboardUseCase;

  @GetMapping("/summary")
  @PreAuthorize(
      "@workspaceSecurity.isWorkspaceMember(#workspaceId, authentication.principal.id)")
  public ApiResponse<DashboardStatsSummaryDto> getDashboardSummary(
      @RequestHeader("X-Workspace-Id") Long workspaceId) {
    return ApiResponse.success(dashboardUseCase.getDashboardSummary(workspaceId));
  }

  @GetMapping("/active-incidents")
  @PreAuthorize(
      "@workspaceSecurity.isWorkspaceMember(#workspaceId, authentication.principal.id)")
  public ApiResponse<DashboardActiveIncidentsDto> getActiveIncidents(
      @RequestHeader("X-Workspace-Id") Long workspaceId) {
    return ApiResponse.success(dashboardUseCase.getActiveIncidents(workspaceId));
  }

  @GetMapping("/latency-chart")
  @PreAuthorize(
      "@workspaceSecurity.isWorkspaceMember(#workspaceId, authentication.principal.id)")
  public ApiResponse<DashboardLatencyChartDto> getLatencyChart(
      @RequestHeader("X-Workspace-Id") Long workspaceId) {
    return ApiResponse.success(dashboardUseCase.getLatencyChart(workspaceId));
  }

  @GetMapping("/stats")
  @PreAuthorize(
      "@workspaceSecurity.isWorkspaceMember(#workspaceId, authentication.principal.id)")
  public ApiResponse<WorkspaceDashboardStatsDto> getWorkspaceStats(
      @RequestHeader("X-Workspace-Id") Long workspaceId) {
    return ApiResponse.success(dashboardUseCase.getWorkspaceDashboardStats(workspaceId));
  }

  @GetMapping("/endpoints/{endpointId}/latency")
  @PreAuthorize(
      "@workspaceSecurity.isWorkspaceMember(#workspaceId, authentication.principal.id)")
  public ApiResponse<List<EndpointLatencyDto>> getEndpointLatency(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @PathVariable Long endpointId,
      @RequestParam(defaultValue = "10") int limit) {
    return ApiResponse.success(
        dashboardUseCase.getEndpointLatencyHistory(workspaceId, endpointId, limit));
  }
}
