package com.example.apihealthchecksystem.domain.service;

import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IncidentAnalyzer {

  public enum Decision {
    OPEN_INCIDENT,
    CLOSE_INCIDENT,
    NO_ACTION
  }

  public record AnalysisResult(Decision decision, String reason) {}

  public AnalysisResult analyze(
      List<HealthCheckResult> recentResults, CheckPolicy policy, Optional<Incident> openIncident) {

    if (recentResults == null || recentResults.isEmpty()) {
      return new AnalysisResult(Decision.NO_ACTION, "Chưa có kết quả kiểm tra.");
    }

    HealthCheckResult latest = recentResults.get(0);
    int failureThreshold = policy.getFailureThreshold() != null ? policy.getFailureThreshold() : 3;

    if (openIncident.isPresent() && latest.isUp()) {
      return new AnalysisResult(Decision.CLOSE_INCIDENT, "Endpoint đã phục hồi.");
    }

    if (openIncident.isEmpty()) {
      long consecutiveFailures = countConsecutiveFailures(recentResults);
      if (consecutiveFailures >= failureThreshold) {
        String reason =
            String.format(
                "Endpoint thất bại liên tiếp %d lần (ngưỡng: %d).",
                consecutiveFailures, failureThreshold);
        return new AnalysisResult(Decision.OPEN_INCIDENT, reason);
      }
    }

    return new AnalysisResult(Decision.NO_ACTION, "Hệ thống bình thường.");
  }

  private long countConsecutiveFailures(List<HealthCheckResult> results) {
    long count = 0;
    for (HealthCheckResult result : results) {
      if (!result.isUp()) {
        count++;
      } else {
        break;
      }
    }
    return count;
  }

  public IncidentSeverity determineSeverity(long consecutiveFailures) {
    if (consecutiveFailures >= 10) {
      return IncidentSeverity.CRITICAL;
    } else if (consecutiveFailures >= 5) {
      return IncidentSeverity.WARNING;
    }
    return IncidentSeverity.INFO;
  }

  public Incident buildNewIncident(
      Long endpointId,
      Long workspaceId,
      String reason,
      List<HealthCheckResult> failingResults,
      long consecutiveFailures) {

    List<Long> failingIds = new ArrayList<>();
    for (HealthCheckResult r : failingResults) {
      if (r.getId() != null) {
        failingIds.add(r.getId());
      }
    }

    return Incident.builder()
        .endpointId(endpointId)
        .startedAt(LocalDateTime.now())
        .status(IncidentStatus.OPEN)
        .reason(reason)
        .failureCount((int) consecutiveFailures)
        .severity(determineSeverity(consecutiveFailures))
        .failingResultIds(failingIds)
        .build();
  }
}
