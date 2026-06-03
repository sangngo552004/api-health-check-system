package com.example.apihealthchecksystem.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.valueobject.CheckStatus;
import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class IncidentAnalyzerTest {

  private final IncidentAnalyzer analyzer = new IncidentAnalyzer();

  @Test
  void analyze_shouldReturnNoAction_whenNoResults() {
    IncidentAnalyzer.AnalysisResult result =
        analyzer.analyze(
            List.of(), CheckPolicy.builder().failureThreshold(3).build(), Optional.empty());

    assertEquals(IncidentAnalyzer.Decision.NO_ACTION, result.decision());
    assertEquals("Chưa có kết quả kiểm tra.", result.reason());
  }

  @Test
  void analyze_shouldOpenIncident_whenFailuresReachThreshold() {
    CheckPolicy policy = CheckPolicy.builder().failureThreshold(3).build();

    IncidentAnalyzer.AnalysisResult result =
        analyzer.analyze(
            List.of(failureResult(1L), failureResult(2L), failureResult(3L), successResult(4L)),
            policy,
            Optional.empty());

    assertEquals(IncidentAnalyzer.Decision.OPEN_INCIDENT, result.decision());
    assertEquals("Endpoint thất bại liên tiếp 3 lần (ngưỡng: 3).", result.reason());
  }

  @Test
  void analyze_shouldCloseIncident_whenOpenIncidentAndLatestCheckIsUp() {
    Incident openIncident = Incident.builder().status(IncidentStatus.OPEN).build();

    IncidentAnalyzer.AnalysisResult result =
        analyzer.analyze(
            List.of(successResult(10L)),
            CheckPolicy.builder().failureThreshold(3).build(),
            Optional.of(openIncident));

    assertEquals(IncidentAnalyzer.Decision.CLOSE_INCIDENT, result.decision());
    assertEquals("Endpoint đã phục hồi.", result.reason());
  }

  @Test
  void buildNewIncident_shouldPopulateSeverityAndFailingIds() {
    Incident incident =
        analyzer.buildNewIncident(
            9L,
            100L,
            "Endpoint thất bại liên tục",
            List.of(
                failureResult(11L), failureResult(12L), failureResult(null), successResult(13L)),
            6);

    assertEquals(9L, incident.getEndpointId());
    assertEquals(IncidentStatus.OPEN, incident.getStatus());
    assertEquals("Endpoint thất bại liên tục", incident.getReason());
    assertEquals(6, incident.getFailureCount());
    assertEquals(IncidentSeverity.WARNING, incident.getSeverity());
    assertIterableEquals(List.of(11L, 12L, 13L), incident.getFailingResultIds());
  }

  @Test
  void determineSeverity_shouldReturnCriticalForTenOrMoreFailures() {
    assertEquals(IncidentSeverity.CRITICAL, analyzer.determineSeverity(10));
    assertEquals(IncidentSeverity.INFO, analyzer.determineSeverity(2));
  }

  private static HealthCheckResult failureResult(Long id) {
    return HealthCheckResult.builder().id(id).status(CheckStatus.DOWN).build();
  }

  private static HealthCheckResult successResult(Long id) {
    return HealthCheckResult.builder().id(id).status(CheckStatus.UP).build();
  }
}
