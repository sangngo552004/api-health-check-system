package com.example.apihealthchecksystem.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.apihealthchecksystem.application.dto.response.IncidentDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.AppException;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.application.port.out.IncidentRepository;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.model.Workspace;
import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetIncidentServiceTest {
  @Mock private WorkspaceRepository workspaceRepository;
  @Mock private IncidentRepository incidentRepository;
  @Mock private EndpointRepository endpointRepository;

  @InjectMocks private GetIncidentService service;

  @Test
  void getIncidents_shouldFilterByStatusAndEndpoint() {
    Long workspaceId = 1L;
    Long endpointId = 11L;
    Workspace workspace = Workspace.builder().id(workspaceId).name("Demo").build();
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder()
            .id(endpointId)
            .workspaceId(workspaceId)
            .name("Payment API")
            .build();
    Incident openIncident =
        Incident.builder()
            .id(100L)
            .endpointId(endpointId)
            .workspaceId(workspaceId)
            .startedAt(LocalDateTime.of(2026, 6, 11, 12, 0))
            .status(IncidentStatus.OPEN)
            .reason("HTTP 500")
            .failureCount(3)
            .severity(IncidentSeverity.CRITICAL)
            .build();
    Incident resolvedIncident =
        Incident.builder()
            .id(101L)
            .endpointId(endpointId)
            .workspaceId(workspaceId)
            .startedAt(LocalDateTime.of(2026, 6, 10, 12, 0))
            .status(IncidentStatus.RESOLVED)
            .reason("Recovered")
            .build();

    when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));
    when(endpointRepository.findById(endpointId)).thenReturn(Optional.of(endpoint));
    when(endpointRepository.findByWorkspaceId(workspaceId)).thenReturn(List.of(endpoint));
    when(incidentRepository.findByWorkspaceId(workspaceId))
        .thenReturn(List.of(resolvedIncident, openIncident));

    PagedResponseDto<IncidentDto> result =
        service.getIncidents(workspaceId, "OPEN", endpointId, 0, 10);

    assertEquals(1, result.items().size());
    assertEquals(1, result.totalItems());
    assertEquals("Payment API", result.items().get(0).endpointName());
    assertEquals("OPEN", result.items().get(0).status());
  }

  @Test
  void getIncidentById_shouldReturnIncidentDetail() {
    Long workspaceId = 1L;
    Long endpointId = 11L;
    Long incidentId = 100L;
    Workspace workspace = Workspace.builder().id(workspaceId).build();
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder()
            .id(endpointId)
            .workspaceId(workspaceId)
            .name("Auth API")
            .build();
    Incident incident =
        Incident.builder()
            .id(incidentId)
            .endpointId(endpointId)
            .workspaceId(workspaceId)
            .startedAt(LocalDateTime.of(2026, 6, 11, 12, 0))
            .status(IncidentStatus.OPEN)
            .reason("Timeout")
            .build();

    when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));
    when(incidentRepository.findById(incidentId)).thenReturn(Optional.of(incident));
    when(endpointRepository.findById(endpointId)).thenReturn(Optional.of(endpoint));

    IncidentDto result = service.getIncidentById(workspaceId, incidentId);

    assertEquals(incidentId, result.id());
    assertEquals("Auth API", result.endpointName());
  }

  @Test
  void getIncidentById_shouldThrowWhenIncidentNotInWorkspace() {
    Long workspaceId = 1L;
    Long incidentId = 100L;
    Workspace workspace = Workspace.builder().id(workspaceId).build();
    Incident incident = Incident.builder().id(incidentId).workspaceId(99L).build();

    when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));
    when(incidentRepository.findById(incidentId)).thenReturn(Optional.of(incident));

    assertThrows(
        ResourceNotFoundException.class, () -> service.getIncidentById(workspaceId, incidentId));
  }

  @Test
  void getIncidents_shouldThrowWhenStatusInvalid() {
    Long workspaceId = 1L;
    Workspace workspace = Workspace.builder().id(workspaceId).build();
    when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));

    assertThrows(
        AppException.class, () -> service.getIncidents(workspaceId, "BROKEN", null, 0, 10));
  }
}
