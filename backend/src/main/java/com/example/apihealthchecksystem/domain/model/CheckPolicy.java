package com.example.apihealthchecksystem.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckPolicy {
  private Long id;
  private String name;
  private Integer intervalSeconds;
  private Integer timeoutMillis;
  private Integer retryCount;
  private Integer failureThreshold;
  private Integer latencyThresholdMillis;
  private Integer expectedStatusCode;
  private String expectedResponseBody;
  private String responseRegex;
  private Long createdBy;
  private Long workspaceId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public int effectiveFailureThreshold() {
    return failureThreshold != null ? failureThreshold : 3;
  }

  public int effectiveTimeoutMillis() {
    return timeoutMillis != null ? timeoutMillis : 5000;
  }

  public int effectiveExpectedStatusCode() {
    return expectedStatusCode != null ? expectedStatusCode : 200;
  }

  public boolean hasExpectedResponseBody() {
    return expectedResponseBody != null && !expectedResponseBody.isBlank();
  }

  public boolean hasResponseRegex() {
    return responseRegex != null && !responseRegex.isBlank();
  }

  public boolean hasLatencyThreshold() {
    return latencyThresholdMillis != null;
  }
}
