package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.response.IncidentDto;
import com.example.apihealthchecksystem.application.dto.response.IncidentHealthCheckResultDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.exception.ValidationException;
import com.example.apihealthchecksystem.application.port.in.GetIncidentUseCase;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.application.port.out.HealthCheckResultRepository;
import com.example.apihealthchecksystem.application.port.out.IncidentRepository;
import com.example.apihealthchecksystem.application.port.out.IncidentListView;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.application.support.PagingUtils;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
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
  private final HealthCheckResultRepository healthCheckResultRepository;

  public GetIncidentService(
      WorkspaceRepository workspaceRepository,
      IncidentRepository incidentRepository,
      EndpointRepository endpointRepository,
      HealthCheckResultRepository healthCheckResultRepository) {
    this.workspaceRepository = workspaceRepository;
    this.incidentRepository = incidentRepository;
    this.endpointRepository = endpointRepository;
    this.healthCheckResultRepository = healthCheckResultRepository;
  }

  @Override
  public PagedResponseDto<IncidentDto> getIncidents(
      Long workspaceId,
      String search,
      String status,
      String severity,
      Long endpointId,
      int page,
      int size,
      String sortBy,
      String sortDir) {
    ensureWorkspaceExists(workspaceId);
    ensureEndpointInWorkspaceIfProvided(workspaceId, endpointId);
    IncidentStatus statusFilter = parseStatus(status);
    IncidentSeverity severityFilter = parseSeverity(severity);
    String normalizedSearch = normalizeSearch(search);

    List<IncidentListView> filteredIncidents =
        incidentRepository.findListByWorkspace(
            workspaceId, statusFilter, severityFilter, endpointId, normalizedSearch).stream()
            .sorted(buildComparator(sortBy, sortDir))
            .toList();

    int safePage = PagingUtils.normalizePage(page);
    int safeSize = PagingUtils.normalizeSize(size);
    int fromIndex = Math.min(safePage * safeSize, filteredIncidents.size());
    int toIndex = Math.min(fromIndex + safeSize, filteredIncidents.size());

    List<IncidentDto> items =
        filteredIncidents.subList(fromIndex, toIndex).stream()
            .map(this::toDto)
            .toList();

    return PagedResponseDto.of(items, safePage, safeSize, filteredIncidents.size());
  }

  @Override
  public IncidentDto getIncidentById(Long workspaceId, Long incidentId) {
    ensureWorkspaceExists(workspaceId);

    Incident incident = getIncidentInWorkspace(workspaceId, incidentId);

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

  @Override
  public List<IncidentHealthCheckResultDto> getIncidentResults(Long workspaceId, Long incidentId) {
    ensureWorkspaceExists(workspaceId);

    Incident incident = getIncidentInWorkspace(workspaceId, incidentId);
    List<Long> resultIds = incident.getFailingResultIds();
    if (resultIds == null || resultIds.isEmpty()) {
      return List.of();
    }

    Map<Long, HealthCheckResult> resultsById =
        healthCheckResultRepository.findAllByIds(resultIds).stream()
            .filter(result -> Objects.equals(workspaceId, result.getWorkspaceId()))
            .collect(Collectors.toMap(HealthCheckResult::getId, Function.identity()));

    return resultIds.stream()
        .map(resultsById::get)
        .filter(Objects::nonNull)
        .map(this::toHealthCheckResultDto)
        .toList();
  }

  private Incident getIncidentInWorkspace(Long workspaceId, Long incidentId) {
    return incidentRepository
        .findById(incidentId)
        .filter(item -> Objects.equals(workspaceId, item.getWorkspaceId()))
        .orElseThrow(
            () -> new ResourceNotFoundException(AppErrorCode.INCIDENT_NOT_FOUND, incidentId));
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
      throw new ValidationException(AppErrorCode.INVALID_INCIDENT_STATUS);
    }
  }

  private IncidentSeverity parseSeverity(String severity) {
    if (severity == null || severity.isBlank()) {
      return null;
    }
    try {
      return IncidentSeverity.valueOf(severity.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new ValidationException(AppErrorCode.INVALID_INCIDENT_SEVERITY);
    }
  }

  private String normalizeSearch(String search) {
    if (search == null || search.isBlank()) {
      return null;
    }
    return search.trim().toLowerCase();
  }

  private Comparator<IncidentListView> buildComparator(String sortBy, String sortDir) {
    Comparator<IncidentListView> comparator =
        switch (sortBy == null ? "" : sortBy) {
          case "resolvedAt" ->
              Comparator.comparing(
                  IncidentListView::resolvedAt, Comparator.nullsLast(Comparator.naturalOrder()));
          case "status" ->
              Comparator.comparing(
                  incident -> incident.status() != null ? incident.status().name() : null,
                  Comparator.nullsLast(String::compareToIgnoreCase));
          case "severity" ->
              Comparator.comparing(
                  incident -> incident.severity() != null ? incident.severity().name() : null,
                  Comparator.nullsLast(String::compareToIgnoreCase));
          case "failureCount" ->
              Comparator.comparing(
                  IncidentListView::failureCount,
                  Comparator.nullsLast(Comparator.naturalOrder()));
          case "endpointName" ->
              Comparator.comparing(
                  IncidentListView::endpointName,
                  Comparator.nullsLast(String::compareToIgnoreCase));
          default ->
              Comparator.comparing(
                  IncidentListView::startedAt, Comparator.nullsLast(Comparator.naturalOrder()));
        };

    if (!"asc".equalsIgnoreCase(sortDir)) {
      comparator = comparator.reversed();
    }
    return comparator.thenComparing(
        IncidentListView::id, Comparator.nullsLast(Comparator.reverseOrder()));
  }

  private IncidentDto toDto(IncidentListView incident) {
    return new IncidentDto(
        incident.id(),
        incident.endpointId(),
        incident.endpointName() != null ? incident.endpointName() : "Unknown endpoint",
        incident.workspaceId(),
        incident.startedAt(),
        incident.resolvedAt(),
        incident.status() != null ? incident.status().name() : null,
        incident.reason(),
        incident.failureCount(),
        incident.severity() != null ? incident.severity().name() : null,
        incident.rootCause(),
        null);
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

  private IncidentHealthCheckResultDto toHealthCheckResultDto(HealthCheckResult result) {
    return new IncidentHealthCheckResultDto(
        result.getId(),
        result.getCheckedAt(),
        result.getStatus() != null ? result.getStatus().name() : null,
        result.getHttpStatusCode(),
        result.getResponseTimeMillis(),
        result.getErrorMessage(),
        result.getResponsePayload(),
        result.getSuccess(),
        result.getNodeId());
  }
}
