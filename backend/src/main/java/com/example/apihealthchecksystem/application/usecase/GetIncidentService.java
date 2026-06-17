package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.response.IncidentDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.AppException;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.port.in.GetIncidentUseCase;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.application.port.out.IncidentRepository;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetIncidentService implements GetIncidentUseCase {
  private final WorkspaceRepository workspaceRepository;
  private final IncidentRepository incidentRepository;
  private final EndpointRepository endpointRepository;

  public GetIncidentService(
      WorkspaceRepository workspaceRepository,
      IncidentRepository incidentRepository,
      EndpointRepository endpointRepository) {
    this.workspaceRepository = workspaceRepository;
    this.incidentRepository = incidentRepository;
    this.endpointRepository = endpointRepository;
  }

  @Override
  public PagedResponseDto<IncidentDto> getIncidents(
      Long workspaceId, String status, Long endpointId, int page, int size) {
    ensureWorkspaceExists(workspaceId);
    ensureEndpointInWorkspaceIfProvided(workspaceId, endpointId);
    IncidentStatus statusFilter = parseStatus(status);

    Map<Long, MonitoredEndpoint> endpointsById =
        endpointRepository.findByWorkspaceId(workspaceId).stream()
            .collect(Collectors.toMap(MonitoredEndpoint::getId, Function.identity()));

    List<Incident> filteredIncidents =
        incidentRepository.findByWorkspaceId(workspaceId).stream()
            .filter(incident -> statusFilter == null || statusFilter == incident.getStatus())
            .filter(
                incident ->
                    endpointId == null || Objects.equals(endpointId, incident.getEndpointId()))
            .sorted(
                Comparator.comparing(
                        Incident::getStartedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(
                        Incident::getId, Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();

    int safePage = Math.max(page, 0);
    int safeSize = Math.max(size, 1);
    int fromIndex = Math.min(safePage * safeSize, filteredIncidents.size());
    int toIndex = Math.min(fromIndex + safeSize, filteredIncidents.size());

    List<IncidentDto> items =
        filteredIncidents.subList(fromIndex, toIndex).stream()
            .map(incident -> toDto(incident, endpointsById.get(incident.getEndpointId())))
            .toList();

    return PagedResponseDto.of(items, safePage, safeSize, filteredIncidents.size());
  }

  @Override
  public IncidentDto getIncidentById(Long workspaceId, Long incidentId) {
    ensureWorkspaceExists(workspaceId);

    Incident incident =
        incidentRepository
            .findById(incidentId)
            .filter(item -> Objects.equals(workspaceId, item.getWorkspaceId()))
            .orElseThrow(() -> new ResourceNotFoundException("incident", incidentId));

    MonitoredEndpoint endpoint =
        endpointRepository
            .findById(incident.getEndpointId())
            .filter(item -> Objects.equals(workspaceId, item.getWorkspaceId()))
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        AppErrorCode.ENDPOINT_NOT_FOUND, incident.getEndpointId()));

    return toDto(incident, endpoint);
  }

  private void ensureWorkspaceExists(Long workspaceId) {
    workspaceRepository
        .findById(workspaceId)
        .orElseThrow(
            () -> new ResourceNotFoundException(AppErrorCode.WORKSPACE_NOT_FOUND, workspaceId));
  }

  private void ensureEndpointInWorkspaceIfProvided(Long workspaceId, Long endpointId) {
    if (endpointId == null) {
      return;
    }

    endpointRepository
        .findById(endpointId)
        .filter(endpoint -> Objects.equals(workspaceId, endpoint.getWorkspaceId()))
        .orElseThrow(
            () -> new ResourceNotFoundException(AppErrorCode.ENDPOINT_NOT_FOUND, endpointId));
  }

  private IncidentStatus parseStatus(String status) {
    if (status == null || status.isBlank()) {
      return null;
    }

    try {
      return IncidentStatus.valueOf(status.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new AppException(AppErrorCode.VALIDATION_ERROR, "Trạng thái incident không hợp lệ");
    }
  }

  private IncidentDto toDto(Incident incident, MonitoredEndpoint endpoint) {
    return new IncidentDto(
        incident.getId(),
        incident.getEndpointId(),
        endpoint != null ? endpoint.getName() : "Unknown endpoint",
        incident.getWorkspaceId(),
        incident.getStartedAt(),
        incident.getResolvedAt(),
        incident.getStatus() != null ? incident.getStatus().name() : null,
        incident.getReason(),
        incident.getFailureCount(),
        incident.getSeverity() != null ? incident.getSeverity().name() : null,
        incident.getRootCause(),
        incident.getFailingResultIds());
  }
}
