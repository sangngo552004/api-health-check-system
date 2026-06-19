package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.IncidentRootCauseUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.IncidentDto;
import com.example.apihealthchecksystem.application.exception.AccessDeniedException;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.port.in.ManageIncidentUseCase;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.application.port.out.IncidentRepository;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import java.util.Objects;

public class ManageIncidentService implements ManageIncidentUseCase {
  private final IncidentRepository incidentRepository;
  private final EndpointRepository endpointRepository;

  public ManageIncidentService(
      IncidentRepository incidentRepository, EndpointRepository endpointRepository) {
    this.incidentRepository = incidentRepository;
    this.endpointRepository = endpointRepository;
  }

  @Override
  public IncidentDto updateRootCause(
      Long workspaceId, Long incidentId, IncidentRootCauseUpdateCommand command) {
    Incident incident =
        incidentRepository
            .findById(incidentId)
            .orElseThrow(
                () -> new ResourceNotFoundException(AppErrorCode.INCIDENT_NOT_FOUND, incidentId));
    validateWorkspaceAccess(incident.getWorkspaceId(), workspaceId);

    incident.setRootCause(normalizeRootCause(command.rootCause()));
    Incident savedIncident = incidentRepository.save(incident);

    MonitoredEndpoint endpoint =
        endpointRepository
            .findById(savedIncident.getEndpointId())
            .filter(item -> Objects.equals(workspaceId, item.getWorkspaceId()))
            .orElse(null);

    return new IncidentDto(
        savedIncident.getId(),
        savedIncident.getEndpointId(),
        endpoint != null ? endpoint.getName() : "Unknown endpoint",
        savedIncident.getWorkspaceId(),
        savedIncident.getStartedAt(),
        savedIncident.getResolvedAt(),
        savedIncident.getStatus() != null ? savedIncident.getStatus().name() : null,
        savedIncident.getReason(),
        savedIncident.getFailureCount(),
        savedIncident.getSeverity() != null ? savedIncident.getSeverity().name() : null,
        savedIncident.getRootCause(),
        savedIncident.getFailingResultIds());
  }

  private void validateWorkspaceAccess(Long resourceWorkspaceId, Long requestedWorkspaceId) {
    if (!Objects.equals(resourceWorkspaceId, requestedWorkspaceId)) {
      throw new AccessDeniedException();
    }
  }

  private String normalizeRootCause(String rootCause) {
    if (rootCause == null) {
      return null;
    }

    String normalized = rootCause.trim();
    return normalized.isEmpty() ? null : normalized;
  }
}
