package com.example.apihealthchecksystem.domain.event;

import java.time.LocalDateTime;

public record IncidentResolvedEvent(Long incidentId, Long endpointId, LocalDateTime resolvedAt) {

  public static IncidentResolvedEvent of(Long incidentId, Long endpointId) {
    return new IncidentResolvedEvent(incidentId, endpointId, LocalDateTime.now());
  }
}
