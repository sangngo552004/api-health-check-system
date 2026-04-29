package com.example.apihealthchecksystem.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckPolicy {
  private Long id;
  private Long endpointId;
  private Integer intervalSeconds;
  private Integer timeoutMillis;
  private Integer retryCount;
  private Integer failureThreshold;
  private Integer latencyThresholdMillis;
}
