package com.example.apihealthchecksystem.domain.model;

import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Incident {
  private Long id;
  private Long endpointId;
  private LocalDateTime startedAt;
  private LocalDateTime resolvedAt;
  private IncidentStatus status;
  private String reason;
}
