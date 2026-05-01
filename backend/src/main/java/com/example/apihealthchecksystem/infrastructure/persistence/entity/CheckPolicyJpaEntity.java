package com.example.apihealthchecksystem.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "check_policies")
@Getter
@Setter
public class CheckPolicyJpaEntity extends BaseJpaEntity {

  @Column(nullable = false)
  private String name;

  @Column(name = "interval_seconds", nullable = false)
  private Integer intervalSeconds;

  @Column(name = "timeout_millis", nullable = false)
  private Integer timeoutMillis;

  @Column(name = "retry_count", nullable = false)
  private Integer retryCount;

  @Column(name = "failure_threshold", nullable = false)
  private Integer failureThreshold;

  @Column(name = "latency_threshold_millis")
  private Integer latencyThresholdMillis;

  @Column(name = "expected_status_code")
  private Integer expectedStatusCode;

  @Column(name = "expected_response_body", columnDefinition = "TEXT")
  private String expectedResponseBody;

  @Column(name = "response_regex")
  private String responseRegex;

  @Column(name = "created_by")
  private Long createdBy;
}
