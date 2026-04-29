package com.example.apihealthchecksystem.domain.model;

import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthCheckResult {
  private Long id;
  private Long endpointId;
  private LocalDateTime checkedAt;
  private EndpointStatus status;
  private Integer httpStatusCode;
  private Long responseTimeMillis;
  private String errorMessage;
  private Boolean success;
}
