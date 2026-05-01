package com.example.apihealthchecksystem.domain.event;

import static org.junit.jupiter.api.Assertions.*;

import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.valueobject.CheckStatus;
import org.junit.jupiter.api.Test;

class DomainEventTest {

  @Test
  void testEndpointCheckedEvent_of() {
    HealthCheckResult result = HealthCheckResult.builder().status(CheckStatus.UP).build();
    EndpointCheckedEvent event = EndpointCheckedEvent.of(1L, result);

    assertNotNull(event);
    assertEquals(1L, event.endpointId());
    assertEquals(result, event.result());
    assertNotNull(event.occurredAt());
  }

  @Test
  void testIncidentOpenedEvent_of() {
    IncidentOpenedEvent event = IncidentOpenedEvent.of(100L, 1L, "Too many failures");

    assertNotNull(event);
    assertEquals(100L, event.incidentId());
    assertEquals(1L, event.endpointId());
    assertEquals("Too many failures", event.reason());
    assertNotNull(event.occurredAt());
  }

  @Test
  void testIncidentResolvedEvent_of() {
    IncidentResolvedEvent event = IncidentResolvedEvent.of(100L, 1L);

    assertNotNull(event);
    assertEquals(100L, event.incidentId());
    assertEquals(1L, event.endpointId());
    assertNotNull(event.resolvedAt());
  }
}
