package com.example.apihealthchecksystem.domain.service;

import com.example.apihealthchecksystem.domain.model.AlertRule;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.valueobject.CheckStatus;
import com.example.apihealthchecksystem.domain.valueobject.ComparisonOperator;
import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class IncidentAnalyzer {

  public enum Decision {
    OPEN_INCIDENT,
    CLOSE_INCIDENT,
    NO_ACTION
  }

  public record AnalysisResult(
      Decision decision,
      String reason,
      List<Long> triggeredRuleIds,
      List<Long> triggeringResultIds,
      IncidentSeverity severity) {}

  public AnalysisResult analyze(
      List<HealthCheckResult> recentResults,
      CheckPolicy policy,
      List<AlertRule> alertRules,
      Optional<Incident> openIncident) {

    if (recentResults == null || recentResults.isEmpty()) {
      return new AnalysisResult(
          Decision.NO_ACTION, "Chưa có kết quả kiểm tra.", List.of(), List.of(), null);
    }

    HealthCheckResult latest = recentResults.get(0);
    TriggerEvaluation evaluation = evaluateTriggers(recentResults, policy, alertRules);

    if (openIncident.isPresent()) {
      if (!evaluation.shouldOpenIncident() && latest.getStatus() == CheckStatus.UP) {
        return new AnalysisResult(
            Decision.CLOSE_INCIDENT, "Endpoint đã phục hồi.", List.of(), List.of(), null);
      }
      return new AnalysisResult(
          Decision.NO_ACTION,
          evaluation.reason(),
          evaluation.triggeredRuleIds(),
          evaluation.triggeringResultIds(),
          evaluation.severity());
    }

    if (evaluation.shouldOpenIncident()) {
      return new AnalysisResult(
          Decision.OPEN_INCIDENT,
          evaluation.reason(),
          evaluation.triggeredRuleIds(),
          evaluation.triggeringResultIds(),
          evaluation.severity());
    }

    return new AnalysisResult(
        Decision.NO_ACTION, "Hệ thống bình thường.", List.of(), List.of(), null);
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

  public Incident buildNewIncident(
      Long endpointId,
      Long workspaceId,
      String reason,
      List<HealthCheckResult> recentResults,
      List<Long> triggeringResultIds,
      long consecutiveFailures,
      List<Long> triggeredRuleIds,
      IncidentSeverity severity) {
    return Incident.builder()
        .endpointId(endpointId)
        .workspaceId(workspaceId)
        .startedAt(LocalDateTime.now())
        .status(IncidentStatus.OPEN)
        .reason(reason)
        .failureCount((int) consecutiveFailures)
        .severity(severity)
        .failingResultIds(
            triggeringResultIds == null
                ? List.of()
                : List.copyOf(new LinkedHashSet<>(triggeringResultIds)))
        .triggeredAlertRuleIds(
            triggeredRuleIds == null
                ? List.of()
                : List.copyOf(new LinkedHashSet<>(triggeredRuleIds)))
        .build();
  }

  public Incident buildNewIncident(
      Long endpointId,
      Long workspaceId,
      String reason,
      List<HealthCheckResult> recentResults,
      List<Long> triggeringResultIds,
      List<Long> triggeredRuleIds,
      IncidentSeverity severity) {
    return buildNewIncident(
        endpointId,
        workspaceId,
        reason,
        recentResults,
        triggeringResultIds,
        countConsecutiveFailures(recentResults),
        triggeredRuleIds,
        severity);
  }

  private TriggerEvaluation evaluateTriggers(
      List<HealthCheckResult> recentResults, CheckPolicy policy, List<AlertRule> alertRules) {
    HealthCheckResult latest = recentResults.get(0);
    long consecutiveFailures = countConsecutiveFailures(recentResults);
    List<String> reasons = new ArrayList<>();
    Set<Long> triggeredRuleIds = new LinkedHashSet<>();
    Set<Long> triggeringResultIds = new LinkedHashSet<>();
    IncidentSeverity severity = null;

    for (AlertRule rule : alertRules) {
      if (!isRuleTriggered(rule, latest, policy, consecutiveFailures)) {
        continue;
      }
      if (rule.getId() != null) {
        triggeredRuleIds.add(rule.getId());
      }
      reasons.add(describeRule(rule, policy, latest, consecutiveFailures));
      triggeringResultIds.addAll(
          collectTriggeringResultIds(rule, recentResults, latest, consecutiveFailures));
      severity = maxSeverity(severity, rule.getSeverity());
    }

    if (reasons.isEmpty()) {
      return new TriggerEvaluation(false, List.of(), List.of(), "Hệ thống bình thường.", null);
    }

    return new TriggerEvaluation(
        true,
        List.copyOf(triggeredRuleIds),
        List.copyOf(triggeringResultIds),
        String.join(" | ", reasons),
        severity);
  }

  private List<Long> collectTriggeringResultIds(
      AlertRule rule,
      List<HealthCheckResult> recentResults,
      HealthCheckResult latest,
      long consecutiveFailures) {
    List<Long> resultIds = new ArrayList<>();
    switch (rule.getRuleType()) {
      case CONSECUTIVE_FAILURE -> {
        int threshold = normalizeIntegerThreshold(rule.getThresholdValue());
        int limit = Math.min((int) consecutiveFailures, threshold);
        for (HealthCheckResult result : recentResults) {
          if (result.isUp() || result.getId() == null) {
            break;
          }
          resultIds.add(result.getId());
          if (resultIds.size() >= limit) {
            break;
          }
        }
      }
      case RESPONSE_TIME, HTTP_STATUS_CODE -> {
        if (latest.getId() != null) {
          resultIds.add(latest.getId());
        }
      }
    }
    return resultIds;
  }

  private boolean isRuleTriggered(
      AlertRule rule, HealthCheckResult latest, CheckPolicy policy, long consecutiveFailures) {
    return switch (rule.getRuleType()) {
      case CONSECUTIVE_FAILURE ->
          consecutiveFailures >= normalizeIntegerThreshold(rule.getThresholdValue());
      case RESPONSE_TIME ->
          latest.getResponseTimeMillis() != null
              && compare(
                  latest.getResponseTimeMillis().doubleValue(),
                  normalizeDoubleThreshold(rule.getThresholdValue()),
                  defaultOperator(rule.getOperator(), ComparisonOperator.GT));
      case HTTP_STATUS_CODE ->
          latest.getHttpStatusCode() != null
              && compare(
                  latest.getHttpStatusCode().doubleValue(),
                  normalizeDoubleThreshold(rule.getThresholdValue()),
                  defaultOperator(rule.getOperator(), ComparisonOperator.NE));
    };
  }

  private String describeRule(
      AlertRule rule, CheckPolicy policy, HealthCheckResult latest, long consecutiveFailures) {
    return switch (rule.getRuleType()) {
      case CONSECUTIVE_FAILURE ->
          String.format(
              "Rule '%s' kích hoạt: thất bại liên tiếp %d lần, ngưỡng %.0f.",
              rule.getName(),
              consecutiveFailures,
              normalizeDoubleThreshold(rule.getThresholdValue()));
      case RESPONSE_TIME ->
          String.format(
              "Rule '%s' kích hoạt: response time %dms %s %.0fms.",
              rule.getName(),
              latest.getResponseTimeMillis(),
              defaultOperator(rule.getOperator(), ComparisonOperator.GT).name(),
              normalizeDoubleThreshold(rule.getThresholdValue()));
      case HTTP_STATUS_CODE ->
          String.format(
              "Rule '%s' kích hoạt: status code %d %s %.0f.",
              rule.getName(),
              latest.getHttpStatusCode(),
              defaultOperator(rule.getOperator(), ComparisonOperator.NE).name(),
              normalizeDoubleThreshold(rule.getThresholdValue()));
    };
  }

  private boolean compare(double actual, Double threshold, ComparisonOperator operator) {
    if (threshold == null) {
      return false;
    }
    return switch (operator) {
      case GT -> actual > threshold;
      case GTE -> actual >= threshold;
      case LT -> actual < threshold;
      case LTE -> actual <= threshold;
      case EQ -> Double.compare(actual, threshold) == 0;
      case NE -> Double.compare(actual, threshold) != 0;
    };
  }

  private int normalizeIntegerThreshold(Double value) {
    return Math.max(1, value.intValue());
  }

  private Double normalizeDoubleThreshold(Double value) {
    return value;
  }

  private ComparisonOperator defaultOperator(
      ComparisonOperator operator, ComparisonOperator fallback) {
    return operator != null ? operator : fallback;
  }

  private IncidentSeverity maxSeverity(
      IncidentSeverity current, IncidentSeverity candidate) {
    if (candidate == null) {
      return current;
    }
    if (current == null) {
      return candidate;
    }
    if (candidate.ordinal() < current.ordinal()) {
      return candidate;
    }
    return current;
  }

  private record TriggerEvaluation(
      boolean shouldOpenIncident,
      List<Long> triggeredRuleIds,
      List<Long> triggeringResultIds,
      String reason,
      IncidentSeverity severity) {}
}
