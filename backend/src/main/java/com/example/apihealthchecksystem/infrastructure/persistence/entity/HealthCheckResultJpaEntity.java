package com.example.apihealthchecksystem.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "health_check_results")
@Getter
@Setter
public class HealthCheckResultJpaEntity extends BaseJpaEntity {

  @Column(name = "endpoint_id", nullable = false)
  private Long endpointId;

  @Column(name = "checked_at", nullable = false)
  private LocalDateTime checkedAt;

  @Column(nullable = false, length = 50)
  private String status;

  @Column(name = "http_status_code")
  private Integer httpStatusCode;

  @Column(name = "response_time_millis")
  private Long responseTimeMillis;

  @Column(name = "error_message", columnDefinition = "TEXT")
  private String errorMessage;

  @Column(name = "response_payload", columnDefinition = "TEXT")
  private String responsePayload;

  @Column(name = "node_id")
  private String nodeId;

  @Column(nullable = false)
  private Boolean success;
}
