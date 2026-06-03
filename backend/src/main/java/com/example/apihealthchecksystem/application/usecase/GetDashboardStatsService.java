package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.response.EndpointLatencyDto;
import com.example.apihealthchecksystem.application.dto.response.IncidentSummaryDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDashboardStatsDto;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.port.in.GetDashboardStatsUseCase;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.application.port.out.HealthCheckResultRepository;
import com.example.apihealthchecksystem.application.port.out.IncidentRepository;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetDashboardStatsService implements GetDashboardStatsUseCase {

  private final WorkspaceRepository workspaceRepository;
  private final EndpointRepository endpointRepository;
  private final IncidentRepository incidentRepository;
  private final HealthCheckResultRepository resultRepository;

  @Override
  public WorkspaceDashboardStatsDto getWorkspaceDashboardStats(Long workspaceId) {
    workspaceRepository
        .findById(workspaceId)
        .orElseThrow(
            () -> new ResourceNotFoundException(AppErrorCode.WORKSPACE_NOT_FOUND, workspaceId));

    List<MonitoredEndpoint> endpoints = endpointRepository.findByWorkspaceId(workspaceId);

    long totalEndpoints = endpoints.size();
    long upEndpoints =
        endpoints.stream().filter(e -> EndpointStatus.UP.equals(e.getStatus())).count();
    long downEndpoints =
        endpoints.stream().filter(e -> EndpointStatus.DOWN.equals(e.getStatus())).count();
    long degradedEndpoints =
        endpoints.stream().filter(e -> EndpointStatus.DEGRADED.equals(e.getStatus())).count();

    List<Incident> openIncidents = incidentRepository.findOpenIncidentsByWorkspaceId(workspaceId);
    long openIncidentsCount = openIncidents.size();

    // Map endpoint IDs to names for dashboard representation
    Map<Long, String> endpointNameMap =
        endpoints.stream()
            .collect(
                Collectors.toMap(
                    MonitoredEndpoint::getId, MonitoredEndpoint::getName, (e1, e2) -> e1));

    List<IncidentSummaryDto> activeIncidents =
        openIncidents.stream()
            .map(
                incident ->
                    new IncidentSummaryDto(
                        incident.getId(),
                        incident.getEndpointId(),
                        endpointNameMap.getOrDefault(incident.getEndpointId(), "Unknown Endpoint"),
                        incident.getStartedAt(),
                        incident.getReason(),
                        incident.getSeverity() != null ? incident.getSeverity().name() : "HIGH"))
            .collect(Collectors.toList());

    return new WorkspaceDashboardStatsDto(
        workspaceId,
        totalEndpoints,
        upEndpoints,
        downEndpoints,
        degradedEndpoints,
        openIncidentsCount,
        activeIncidents);
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
}
