package com.example.apihealthchecksystem.domain.model;

import com.example.apihealthchecksystem.domain.valueobject.CheckStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthCheckResult {
  private Long id;
  private Long endpointId;
  private LocalDateTime checkedAt;
  private CheckStatus status;
  private Integer httpStatusCode;
  private Long responseTimeMillis;
  private String errorMessage;
  private String responsePayload;
  private String nodeId;
  private Boolean success;

  public boolean isUp() {
    return CheckStatus.UP.equals(status);
  }

  public boolean isDown() {
    return CheckStatus.DOWN.equals(status);
  }
}
