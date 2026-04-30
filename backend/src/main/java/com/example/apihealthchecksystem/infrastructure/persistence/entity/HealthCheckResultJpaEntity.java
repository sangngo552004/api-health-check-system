package com.example.apihealthchecksystem.infrastructure.persistence.entity;

import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "health_check_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthCheckResultJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "endpoint_id", nullable = false)
  private MonitoredEndpointJpaEntity endpoint;

  @Column(name = "checked_at", nullable = false)
  private LocalDateTime checkedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private EndpointStatus status;

  @Column(name = "http_status_code")
  private Integer httpStatusCode;

  @Column(name = "response_time_millis")
  private Long responseTimeMillis;

  @Column(name = "error_message", columnDefinition = "TEXT")
  private String errorMessage;

  @Column(nullable = false)
  private Boolean success;
}
