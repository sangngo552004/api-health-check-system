package com.example.apihealthchecksystem.domain.model;

import com.example.apihealthchecksystem.domain.valueobject.CheckStatus;
import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonitoredEndpoint {
  private Long id;
  private String name;
  private String url;
  private HttpMethod method;
  private String environment;
  private CheckType checkType;
  private Boolean isActive;
  private EndpointStatus status;
  private List<String> tags;
  private Long policyId;
  private Long workspaceId;
  private List<Long> alertRuleIds;
  private List<Long> contactGroupIds;
  private Map<String, String> headers;
  private String requestBody;
  private Long createdBy;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime lastCheckedAt;

  public void initializeForCreation(Long workspaceId, Long createdBy, LocalDateTime now) {
    this.workspaceId = workspaceId;
    this.createdBy = createdBy;
    this.status = EndpointStatus.UP;
    this.createdAt = now;
    this.updatedAt = now;
  }

  public void applyUpdates(
      String name,
      String url,
      HttpMethod method,
      String environment,
      CheckType checkType,
      Long policyId,
      List<Long> alertRuleIds,
      List<String> tags,
      Map<String, String> headers,
      String requestBody,
      Boolean isActive,
      LocalDateTime updatedAt) {
    this.name = name;
    this.url = url;
    this.method = method;
    this.environment = environment;
    this.checkType = checkType;
    this.policyId = policyId;
    this.alertRuleIds = alertRuleIds;
    this.tags = tags;
    this.headers = headers;
    this.requestBody = requestBody;

    if (isActive != null) {
      this.isActive = isActive;
    }

    this.updatedAt = updatedAt;
  }

  public void markChecked(HealthCheckResult result) {
    this.status = mapStatus(result.getStatus());
    this.lastCheckedAt = result.getCheckedAt();
  }

  private EndpointStatus mapStatus(CheckStatus checkStatus) {
    if (checkStatus == CheckStatus.DOWN) {
      return EndpointStatus.DOWN;
    }
    if (checkStatus == CheckStatus.DEGRADED) {
      return EndpointStatus.DEGRADED;
    }
    return EndpointStatus.UP;
  }
}
