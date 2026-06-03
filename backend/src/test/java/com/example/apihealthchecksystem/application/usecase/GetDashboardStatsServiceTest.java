package com.example.apihealthchecksystem.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.apihealthchecksystem.application.dto.response.EndpointLatencyDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDashboardStatsDto;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.application.port.out.HealthCheckResultRepository;
import com.example.apihealthchecksystem.application.port.out.IncidentRepository;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.model.Workspace;
import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetDashboardStatsServiceTest {

  @Mock private WorkspaceRepository workspaceRepository;
  @Mock private EndpointRepository endpointRepository;
  @Mock private IncidentRepository incidentRepository;
  @Mock private HealthCheckResultRepository resultRepository;

  @InjectMocks private GetDashboardStatsService service;

  @Test
  void getWorkspaceDashboardStats_shouldReturnStats_whenWorkspaceExists() {
    Long workspaceId = 1L;
    Workspace workspace = Workspace.builder().id(workspaceId).name("Test Team").build();

    MonitoredEndpoint ep1 =
        MonitoredEndpoint.builder().id(10L).name("EP1").status(EndpointStatus.UP).build();
    MonitoredEndpoint ep2 =
        MonitoredEndpoint.builder().id(20L).name("EP2").status(EndpointStatus.DOWN).build();
    MonitoredEndpoint ep3 =
        MonitoredEndpoint.builder().id(30L).name("EP3").status(EndpointStatus.DEGRADED).build();

    Incident incident =
        Incident.builder()
            .id(100L)
            .endpointId(20L)
            .startedAt(LocalDateTime.now())
            .reason("Conn timeout")
            .build();

    when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));
    when(endpointRepository.findByWorkspaceId(workspaceId)).thenReturn(List.of(ep1, ep2, ep3));
    when(incidentRepository.findOpenIncidentsByWorkspaceId(workspaceId))
        .thenReturn(List.of(incident));

    WorkspaceDashboardStatsDto stats = service.getWorkspaceDashboardStats(workspaceId);

    assertNotNull(stats);
    assertEquals(workspaceId, stats.workspaceId());
    assertEquals(3, stats.totalEndpoints());
    assertEquals(1, stats.upEndpoints());
    assertEquals(1, stats.downEndpoints());
    assertEquals(1, stats.degradedEndpoints());
    assertEquals(1, stats.openIncidentsCount());
    assertEquals(1, stats.activeIncidents().size());
    assertEquals("EP2", stats.activeIncidents().get(0).endpointName());
  }

  @Test
  void getWorkspaceDashboardStats_shouldThrow_whenWorkspaceNotFound() {
    Long workspaceId = 1L;
    when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> service.getWorkspaceDashboardStats(workspaceId));
  }

  @Test
  void getEndpointLatencyHistory_shouldReturnHistory() {
    Long workspaceId = 1L;
    Long endpointId = 10L;
    Workspace workspace = Workspace.builder().id(workspaceId).build();
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder().id(endpointId).workspaceId(workspaceId).build();

    HealthCheckResult res =
        HealthCheckResult.builder()
            .checkedAt(LocalDateTime.now())
            .responseTimeMillis(250L)
            .success(true)
            .build();

    when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));
    when(endpointRepository.findById(endpointId)).thenReturn(Optional.of(endpoint));
    when(resultRepository.findTop10ByEndpointIdOrderByCheckedAtDesc(endpointId))
        .thenReturn(List.of(res));

    List<EndpointLatencyDto> history =
        service.getEndpointLatencyHistory(workspaceId, endpointId, 10);

    assertNotNull(history);
    assertEquals(1, history.size());
    assertEquals(250L, history.get(0).responseTimeMillis());
    assertTrue(history.get(0).success());
  }

  @Test
  void getEndpointLatencyHistory_shouldThrow_whenEndpointNotBelongsToWorkspace() {
    Long workspaceId = 1L;
    Long endpointId = 10L;
    Workspace workspace = Workspace.builder().id(workspaceId).build();
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder().id(endpointId).workspaceId(99L).build(); // Different workspace

    when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));
    when(endpointRepository.findById(endpointId)).thenReturn(Optional.of(endpoint));

    assertThrows(
        ResourceNotFoundException.class,
        () -> service.getEndpointLatencyHistory(workspaceId, endpointId, 10));
  }
}
