package com.example.apihealthchecksystem.domain.event;

import java.time.LocalDateTime;

public record IncidentOpenedEvent(
    Long incidentId, Long endpointId, String reason, LocalDateTime occurredAt) {

  public static IncidentOpenedEvent of(Long incidentId, Long endpointId, String reason) {
    return new IncidentOpenedEvent(incidentId, endpointId, reason, LocalDateTime.now());
  }
}
