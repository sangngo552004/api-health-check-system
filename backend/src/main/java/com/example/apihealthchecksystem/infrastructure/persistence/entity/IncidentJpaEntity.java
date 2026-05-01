package com.example.apihealthchecksystem.infrastructure.persistence.entity;

import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "incidents")
@Getter
@Setter
public class IncidentJpaEntity extends BaseJpaEntity {

  @Column(name = "endpoint_id", nullable = false)
  private Long endpointId;

  @Column(name = "started_at", nullable = false)
  private LocalDateTime startedAt;

  @Column(name = "resolved_at")
  private LocalDateTime resolvedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private IncidentStatus status;

  @Column(columnDefinition = "TEXT")
  private String reason;

  @Column(name = "failure_count")
  private Integer failureCount;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private IncidentSeverity severity;

  @Column(name = "root_cause", columnDefinition = "TEXT")
  private String rootCause;

  @ElementCollection
  @CollectionTable(
      name = "incident_failing_results",
      joinColumns = @JoinColumn(name = "incident_id"))
  @Column(name = "result_id")
  private List<Long> failingResultIds;
}
