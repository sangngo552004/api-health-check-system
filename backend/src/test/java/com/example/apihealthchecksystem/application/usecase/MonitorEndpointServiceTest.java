package com.example.apihealthchecksystem.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.apihealthchecksystem.application.port.out.CheckPolicyRepository;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.application.port.out.HealthCheckExecutor;
import com.example.apihealthchecksystem.application.port.out.HealthCheckResultRepository;
import com.example.apihealthchecksystem.application.port.out.IncidentRepository;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.CheckStatus;
import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class MonitorEndpointServiceTest {

  @Mock private EndpointRepository endpointRepository;
  @Mock private CheckPolicyRepository checkPolicyRepository;
  @Mock private HealthCheckExecutor executor;
  @Mock private HealthCheckResultRepository resultRepository;
  @Mock private IncidentRepository incidentRepository;
  @Mock private ApplicationEventPublisher eventPublisher;

  private MonitorEndpointService monitorEndpointService;

  @BeforeEach
  void setUp() {
    monitorEndpointService =
        new MonitorEndpointService(
            endpointRepository,
            checkPolicyRepository,
            List.of(executor),
            resultRepository,
            incidentRepository,
            eventPublisher);
  }

  @Test
  void runHealthCheckForEndpoint_shouldSkipInactiveEndpoint() {
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder().id(1L).isActive(false).checkType(CheckType.HTTP).build();
    when(endpointRepository.findById(1L)).thenReturn(Optional.of(endpoint));

    monitorEndpointService.runHealthCheckForEndpoint(1L);

    verify(checkPolicyRepository, never()).findById(any());
    verify(resultRepository, never()).save(any());
  }

  @Test
  void runHealthCheckForEndpoint_shouldSkipWhenPolicyIsMissing() {
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder()
            .id(2L)
            .workspaceId(20L)
            .isActive(true)
            .policyId(200L)
            .checkType(CheckType.HTTP)
            .build();
    when(endpointRepository.findById(2L)).thenReturn(Optional.of(endpoint));
    when(checkPolicyRepository.findById(200L)).thenReturn(Optional.empty());

    monitorEndpointService.runHealthCheckForEndpoint(2L);

    verify(executor, never()).execute(any(), any());
    verify(resultRepository, never()).save(any());
  }

  @Test
  void runHealthCheckForEndpoint_shouldSaveResultAndOpenIncident() {
    LocalDateTime now = LocalDateTime.now();
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder()
            .id(3L)
            .workspaceId(30L)
            .isActive(true)
            .policyId(300L)
            .checkType(CheckType.HTTP)
            .status(EndpointStatus.UP)
            .build();
    CheckPolicy policy = CheckPolicy.builder().id(300L).failureThreshold(3).build();
    HealthCheckResult executed =
        HealthCheckResult.builder()
            .endpointId(3L)
            .checkedAt(now)
            .status(CheckStatus.DOWN)
            .responseTimeMillis(120L)
            .build();
    HealthCheckResult saved =
        HealthCheckResult.builder()
            .id(301L)
            .endpointId(3L)
            .workspaceId(30L)
            .checkedAt(now)
            .status(CheckStatus.DOWN)
            .responseTimeMillis(120L)
            .build();

    when(endpointRepository.findById(3L)).thenReturn(Optional.of(endpoint));
    when(checkPolicyRepository.findById(300L)).thenReturn(Optional.of(policy));
    when(executor.supports(CheckType.HTTP)).thenReturn(true);
    when(executor.execute(endpoint, policy)).thenReturn(executed);
    when(resultRepository.save(executed)).thenReturn(saved);
    when(resultRepository.findTop10ByEndpointIdOrderByCheckedAtDesc(3L))
        .thenReturn(List.of(saved, failureResult(302L), failureResult(303L)));
    when(incidentRepository.findOpenIncidentByEndpointId(3L)).thenReturn(Optional.empty());
    when(incidentRepository.save(any(Incident.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    monitorEndpointService.runHealthCheckForEndpoint(3L);

    verify(resultRepository).save(executed);
    verify(endpointRepository)
        .save(
            argThat(
                monitoredEndpoint ->
                    monitoredEndpoint.getStatus() == EndpointStatus.DOWN
                        && now.equals(monitoredEndpoint.getLastCheckedAt())));
    verify(eventPublisher, org.mockito.Mockito.times(2)).publishEvent(any(Object.class));
    verify(incidentRepository)
        .save(
            argThat(
                incident ->
                    incident.getEndpointId().equals(3L)
                        && incident.getWorkspaceId().equals(30L)
                        && incident.getFailureCount() == 3));
  }

  @Test
  void runHealthCheckForEndpoint_shouldResolveOpenIncident_whenEndpointRecovers() {
    LocalDateTime now = LocalDateTime.now();
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder()
            .id(4L)
            .workspaceId(40L)
            .isActive(true)
            .policyId(400L)
            .checkType(CheckType.HTTP)
            .build();
    CheckPolicy policy = CheckPolicy.builder().id(400L).failureThreshold(3).build();
    HealthCheckResult executed =
        HealthCheckResult.builder()
            .endpointId(4L)
            .checkedAt(now)
            .status(CheckStatus.UP)
            .responseTimeMillis(90L)
            .build();
    HealthCheckResult saved =
        HealthCheckResult.builder()
            .id(401L)
            .endpointId(4L)
            .workspaceId(40L)
            .checkedAt(now)
            .status(CheckStatus.UP)
            .responseTimeMillis(90L)
            .build();
    Incident openIncident =
        Incident.builder()
            .id(999L)
            .endpointId(4L)
            .workspaceId(40L)
            .status(IncidentStatus.OPEN)
            .reason("Old issue")
            .build();

    when(endpointRepository.findById(4L)).thenReturn(Optional.of(endpoint));
    when(checkPolicyRepository.findById(400L)).thenReturn(Optional.of(policy));
    when(executor.supports(CheckType.HTTP)).thenReturn(true);
    when(executor.execute(endpoint, policy)).thenReturn(executed);
    when(resultRepository.save(executed)).thenReturn(saved);
    when(resultRepository.findTop10ByEndpointIdOrderByCheckedAtDesc(4L)).thenReturn(List.of(saved));
    when(incidentRepository.findOpenIncidentByEndpointId(4L)).thenReturn(Optional.of(openIncident));
    when(incidentRepository.save(any(Incident.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    monitorEndpointService.runHealthCheckForEndpoint(4L);

    verify(incidentRepository)
        .save(
            argThat(
                incident -> {
                  assertNotNull(incident.getResolvedAt());
                  assertEquals(IncidentStatus.RESOLVED, incident.getStatus());
                  return true;
                }));
  }

  private static HealthCheckResult failureResult(Long id) {
    return HealthCheckResult.builder().id(id).status(CheckStatus.DOWN).build();
  }
}
