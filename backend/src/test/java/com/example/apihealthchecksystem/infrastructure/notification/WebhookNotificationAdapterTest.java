package com.example.apihealthchecksystem.infrastructure.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.apihealthchecksystem.application.port.out.NotificationRepository;
import com.example.apihealthchecksystem.domain.model.ContactGroup;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.model.Notification;
import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class WebhookNotificationAdapterTest {
  @Test
  void sendIncidentAlert_shouldSendWebhookPayloadToConfiguredUrls() {
    RecordingWebhookDeliveryClient deliveryClient = new RecordingWebhookDeliveryClient();
    WebhookNotificationAdapter adapter =
        new WebhookNotificationAdapter(
            new LogNotificationAdapter(),
            deliveryClient,
            new InMemoryNotificationRepository(),
            new ObjectMapper().findAndRegisterModules(),
            "https://global.example/webhook");

    Incident incident =
        Incident.builder()
            .id(100L)
            .workspaceId(1L)
            .endpointId(10L)
            .status(IncidentStatus.OPEN)
            .severity(IncidentSeverity.CRITICAL)
            .reason("Connection timeout")
            .failureCount(3)
            .startedAt(LocalDateTime.of(2026, 6, 11, 12, 0))
            .build();
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder()
            .id(10L)
            .workspaceId(1L)
            .name("Payment API")
            .url("https://api.example.com/health")
            .build();
    ContactGroup group =
        ContactGroup.builder()
            .id(1L)
            .name("On-call")
            .isActive(true)
            .webhookUrls(List.of("https://team.example/webhook"))
            .build();

    adapter.sendIncidentAlert(incident, endpoint, List.of(group));

    assertEquals(2, deliveryClient.requests.size());
    assertTrue(
        deliveryClient.requests.get(0).payload.contains("\"eventType\":\"INCIDENT_OPENED\""));
    assertTrue(deliveryClient.requests.get(0).payload.contains("\"incidentId\":100"));
    assertTrue(
        deliveryClient.requests.stream()
            .anyMatch(request -> request.url.equals("https://team.example/webhook")));
    assertTrue(
        deliveryClient.requests.stream()
            .anyMatch(request -> request.url.equals("https://global.example/webhook")));
  }

  private static final class RecordingWebhookDeliveryClient implements WebhookDeliveryClient {
    private final List<Request> requests = new ArrayList<>();

    @Override
    public DeliveryResult post(String webhookUrl, String payload) {
      requests.add(new Request(webhookUrl, payload));
      return DeliveryResult.success();
    }
  }

  private static final class InMemoryNotificationRepository implements NotificationRepository {
    @Override
    public Notification save(Notification notification) {
      return notification;
    }

    @Override
    public List<Notification> findByIncidentId(Long incidentId) {
      return List.of();
    }
  }

  private record Request(String url, String payload) {}
}
