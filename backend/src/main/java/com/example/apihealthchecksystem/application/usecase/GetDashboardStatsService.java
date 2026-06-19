package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.response.DashboardActiveIncidentsDto;
import com.example.apihealthchecksystem.application.dto.response.DashboardLatencyChartDto;
import com.example.apihealthchecksystem.application.dto.response.DashboardLatencySeriesDto;
import com.example.apihealthchecksystem.application.dto.response.DashboardStatsSummaryDto;
import com.example.apihealthchecksystem.application.dto.response.EndpointLatencyDto;
import com.example.apihealthchecksystem.application.dto.response.IncidentSummaryDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDashboardStatsDto;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.port.in.GetDashboardStatsUseCase;
import com.example.apihealthchecksystem.application.port.out.DashboardEndpointView;
import com.example.apihealthchecksystem.application.port.out.DashboardIncidentView;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.application.port.out.HealthCheckResultRepository;
import com.example.apihealthchecksystem.application.port.out.IncidentRepository;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GetDashboardStatsService implements GetDashboardStatsUseCase {
  private static final int DASHBOARD_CHART_ENDPOINT_LIMIT = 3;
  private static final int DASHBOARD_CHART_POINT_LIMIT = 20;

  private final WorkspaceRepository workspaceRepository;
  private final EndpointRepository endpointRepository;
  private final IncidentRepository incidentRepository;
  private final HealthCheckResultRepository resultRepository;

  @Override
  public DashboardStatsSummaryDto getDashboardSummary(Long workspaceId) {
    long totalStartNanos = System.nanoTime();
    List<DashboardEndpointView> endpoints = loadDashboardEndpoints(workspaceId, "summary");

    long aggregationStart = System.nanoTime();
    long totalEndpoints = endpoints.size();
    long upEndpoints =
        endpoints.stream()
            .filter(e -> EndpointStatus.UP.equals(parseEndpointStatus(e.status())))
            .count();
    long downEndpoints =
        endpoints.stream()
            .filter(e -> EndpointStatus.DOWN.equals(parseEndpointStatus(e.status())))
            .count();
    long degradedEndpoints =
        endpoints.stream()
            .filter(e -> EndpointStatus.DEGRADED.equals(parseEndpointStatus(e.status())))
            .count();
    log.info(
        "Dashboard summary aggregation for workspaceId={} took {} ms",
        workspaceId,
        elapsedMillis(aggregationStart));

    DashboardStatsSummaryDto dto =
        new DashboardStatsSummaryDto(
            workspaceId, totalEndpoints, upEndpoints, downEndpoints, degradedEndpoints);
    log.info(
        "Dashboard summary total for workspaceId={} completed in {} ms",
        workspaceId,
        elapsedMillis(totalStartNanos));
    return dto;
  }

  @Override
  public DashboardActiveIncidentsDto getActiveIncidents(Long workspaceId) {
    long totalStartNanos = System.nanoTime();
    verifyWorkspaceExists(workspaceId, "active-incidents");

    long incidentsStart = System.nanoTime();
    List<DashboardIncidentView> openIncidents =
        incidentRepository.findDashboardOpenIncidentsByWorkspaceId(workspaceId);
    long openIncidentsCount = openIncidents.size();
    log.info(
        "Dashboard active incidents lookup for workspaceId={} returned {} incidents in {} ms",
        workspaceId,
        openIncidentsCount,
        elapsedMillis(incidentsStart));

    long incidentMappingStart = System.nanoTime();
    List<IncidentSummaryDto> activeIncidents =
        openIncidents.stream()
            .map(
                incident ->
                    new IncidentSummaryDto(
                        incident.id(),
                        incident.endpointId(),
                        incident.endpointName(),
                        incident.startedAt(),
                        incident.reason(),
                        incident.severity() != null ? incident.severity().name() : "HIGH"))
            .collect(Collectors.toList());
    log.info(
        "Dashboard active incidents mapping for workspaceId={} took {} ms",
        workspaceId,
        elapsedMillis(incidentMappingStart));

    DashboardActiveIncidentsDto dto =
        new DashboardActiveIncidentsDto(workspaceId, openIncidentsCount, activeIncidents);
    log.info(
        "Dashboard active incidents total for workspaceId={} completed in {} ms",
        workspaceId,
        elapsedMillis(totalStartNanos));
    return dto;
  }

  @Override
  public DashboardLatencyChartDto getLatencyChart(Long workspaceId) {
    long totalStartNanos = System.nanoTime();
    List<DashboardEndpointView> endpoints = loadDashboardEndpoints(workspaceId, "latency-chart");

    long latencySeriesStart = System.nanoTime();
    List<DashboardLatencySeriesDto> latencySeries = buildLatencySeries(endpoints);
    log.info(
        "Dashboard latency chart build for workspaceId={} produced {} series in {} ms",
        workspaceId,
        latencySeries.size(),
        elapsedMillis(latencySeriesStart));

    DashboardLatencyChartDto dto = new DashboardLatencyChartDto(workspaceId, latencySeries);
    log.info(
        "Dashboard latency chart total for workspaceId={} completed in {} ms",
        workspaceId,
        elapsedMillis(totalStartNanos));
    return dto;
  }

  @Override
  public WorkspaceDashboardStatsDto getWorkspaceDashboardStats(Long workspaceId) {
    DashboardStatsSummaryDto summary = getDashboardSummary(workspaceId);
    DashboardActiveIncidentsDto activeIncidents = getActiveIncidents(workspaceId);
    DashboardLatencyChartDto latencyChart = getLatencyChart(workspaceId);
    return new WorkspaceDashboardStatsDto(
        workspaceId,
        summary.totalEndpoints(),
        summary.upEndpoints(),
        summary.downEndpoints(),
        summary.degradedEndpoints(),
        activeIncidents.openIncidentsCount(),
        activeIncidents.incidents(),
        latencyChart.series());
  }

  @Override
  public List<EndpointLatencyDto> getEndpointLatencyHistory(
      Long workspaceId, Long endpointId, int limit) {
    workspaceRepository
        .findById(workspaceId)
        .orElseThrow(
            () -> new ResourceNotFoundException(AppErrorCode.WORKSPACE_NOT_FOUND, workspaceId));

    MonitoredEndpoint endpoint =
        endpointRepository
            .findById(endpointId)
            .orElseThrow(
                () -> new ResourceNotFoundException(AppErrorCode.ENDPOINT_NOT_FOUND, endpointId));

    if (!workspaceId.equals(endpoint.getWorkspaceId())) {
      throw new ResourceNotFoundException(AppErrorCode.ENDPOINT_NOT_FOUND, endpointId);
    }

    List<HealthCheckResult> results =
        resultRepository.findTop10ByEndpointIdOrderByCheckedAtDesc(endpointId);

    return results.stream()
        .limit(limit)
        .map(
            result ->
                new EndpointLatencyDto(
                    result.getCheckedAt(),
                    result.getResponseTimeMillis(),
                    Boolean.TRUE.equals(result.getSuccess())))
        .collect(Collectors.toList());
  }

  private List<DashboardLatencySeriesDto> buildLatencySeries(
      List<DashboardEndpointView> endpoints) {
    List<DashboardEndpointView> plotEndpoints =
        endpoints.stream().limit(DASHBOARD_CHART_ENDPOINT_LIMIT).toList();
    if (plotEndpoints.isEmpty()) {
      return List.of();
    }

    List<Long> endpointIds = plotEndpoints.stream().map(DashboardEndpointView::id).toList();
    Map<Long, List<HealthCheckResult>> resultsByEndpoint =
        resultRepository.findByEndpointIdsOrderByCheckedAtDesc(endpointIds).stream()
            .collect(Collectors.groupingBy(HealthCheckResult::getEndpointId));

    return plotEndpoints.stream()
        .map(
            endpoint -> {
              List<EndpointLatencyDto> points =
                  resultsByEndpoint
                      .getOrDefault(endpoint.id(), List.of())
                      .stream()
                      .limit(DASHBOARD_CHART_POINT_LIMIT)
                      .map(this::toEndpointLatencyDto)
                      .toList();
              return new DashboardLatencySeriesDto(endpoint.id(), endpoint.name(), points);
            })
        .toList();
  }

  private EndpointLatencyDto toEndpointLatencyDto(HealthCheckResult result) {
    return new EndpointLatencyDto(
        result.getCheckedAt(),
        result.getResponseTimeMillis(),
        Boolean.TRUE.equals(result.getSuccess()));
  }

  private long elapsedMillis(long startNanos) {
    return (System.nanoTime() - startNanos) / 1_000_000;
  }

  private void verifyWorkspaceExists(Long workspaceId, String operation) {
    long workspaceLookupStart = System.nanoTime();
    workspaceRepository
        .findById(workspaceId)
        .orElseThrow(
            () -> new ResourceNotFoundException(AppErrorCode.WORKSPACE_NOT_FOUND, workspaceId));
    log.info(
        "Dashboard {} workspace lookup for workspaceId={} took {} ms",
        operation,
        workspaceId,
        elapsedMillis(workspaceLookupStart));
  }

  private List<DashboardEndpointView> loadDashboardEndpoints(Long workspaceId, String operation) {
    verifyWorkspaceExists(workspaceId, operation);
    long endpointsStart = System.nanoTime();
    List<DashboardEndpointView> endpoints =
        endpointRepository.findDashboardByWorkspaceId(workspaceId);
    log.info(
        "Dashboard {} endpoint lookup for workspaceId={} returned {} endpoints in {} ms",
        operation,
        workspaceId,
        endpoints.size(),
        elapsedMillis(endpointsStart));
    return endpoints;
  }

  private EndpointStatus parseEndpointStatus(String status) {
    if (status == null) {
      return EndpointStatus.UP;
    }
    try {
      return EndpointStatus.valueOf(status);
    } catch (IllegalArgumentException ex) {
      return EndpointStatus.UP;
    }
  }
}
