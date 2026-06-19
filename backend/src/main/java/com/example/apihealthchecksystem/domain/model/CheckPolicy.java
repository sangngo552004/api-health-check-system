package com.example.apihealthchecksystem.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckPolicy {
  public static final int DEFAULT_INTERVAL_SECONDS = 60;
  public static final int DEFAULT_TIMEOUT_MILLIS = 5000;
  public static final int DEFAULT_RETRY_COUNT = 0;
  public static final int DEFAULT_EXPECTED_STATUS_CODE = 200;

  private Long id;
  private String name;
  private Integer intervalSeconds;
  private Integer timeoutMillis;
  private Integer retryCount;
  private Integer degradedResponseTimeMillis;
  private Integer expectedStatusCode;
  private String expectedResponseBody;
  private String responseRegex;
  private Long createdBy;
  private Long workspaceId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public int effectiveIntervalSeconds() {
    return intervalSeconds != null ? intervalSeconds : DEFAULT_INTERVAL_SECONDS;
  }

  public int effectiveTimeoutMillis() {
    return timeoutMillis != null ? timeoutMillis : DEFAULT_TIMEOUT_MILLIS;
  }

  public int effectiveRetryCount() {
    return retryCount != null ? retryCount : DEFAULT_RETRY_COUNT;
  }

  public int effectiveExpectedStatusCode() {
    return expectedStatusCode != null ? expectedStatusCode : DEFAULT_EXPECTED_STATUS_CODE;
  }

  public boolean hasExpectedResponseBody() {
    return expectedResponseBody != null && !expectedResponseBody.isBlank();
  }

  public boolean hasResponseRegex() {
    return responseRegex != null && !responseRegex.isBlank();
  }

  public boolean hasDegradedResponseTimeThreshold() {
    return degradedResponseTimeMillis != null;
  }
}
