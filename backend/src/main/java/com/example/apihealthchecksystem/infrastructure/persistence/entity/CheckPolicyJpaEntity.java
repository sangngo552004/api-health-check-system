package com.example.apihealthchecksystem.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "check_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckPolicyJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "endpoint_id", nullable = false)
  private MonitoredEndpointJpaEntity endpoint;

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
}
