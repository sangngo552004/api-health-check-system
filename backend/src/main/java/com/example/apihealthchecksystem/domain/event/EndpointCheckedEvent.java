package com.example.apihealthchecksystem.domain.event;

import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import java.time.LocalDateTime;

public record EndpointCheckedEvent(
    Long endpointId, HealthCheckResult result, LocalDateTime occurredAt) {

  public static EndpointCheckedEvent of(Long endpointId, HealthCheckResult result) {
    return new EndpointCheckedEvent(endpointId, result, LocalDateTime.now());
  }
}
