package com.example.apihealthchecksystem.infrastructure.notification;

import com.example.apihealthchecksystem.application.port.out.NotificationPort;
import com.example.apihealthchecksystem.application.port.out.NotificationRepository;
import com.example.apihealthchecksystem.domain.model.ContactGroup;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.model.Notification;
import com.example.apihealthchecksystem.domain.valueobject.NotificationChannel;
import com.example.apihealthchecksystem.domain.valueobject.NotificationStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@Slf4j
public class WebhookNotificationAdapter implements NotificationPort {
  private static final DateTimeFormatter MESSAGE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final LogNotificationAdapter logNotificationAdapter;
  private final WebhookDeliveryClient webhookDeliveryClient;
  private final NotificationRepository notificationRepository;
  private final ObjectMapper objectMapper;
  private final String configuredWebhookUrl;

  public WebhookNotificationAdapter(
      LogNotificationAdapter logNotificationAdapter,
      WebhookDeliveryClient webhookDeliveryClient,
      NotificationRepository notificationRepository,
      ObjectMapper objectMapper,
      @Value("${app.notification.webhook-url:}") String configuredWebhookUrl) {
    this.logNotificationAdapter = logNotificationAdapter;
    this.webhookDeliveryClient = webhookDeliveryClient;
    this.notificationRepository = notificationRepository;
    this.objectMapper = objectMapper;
    this.configuredWebhookUrl = configuredWebhookUrl;
  }

  @Override
  public void sendIncidentAlert(
      Incident incident, MonitoredEndpoint endpoint, List<ContactGroup> contactGroups) {
    logNotificationAdapter.sendIncidentAlert(incident, endpoint, contactGroups);
    dispatchWebhook("INCIDENT_OPENED", incident, endpoint, contactGroups);
  }

  @Override
  public void sendRecoveryAlert(
      Incident incident, MonitoredEndpoint endpoint, List<ContactGroup> contactGroups) {
    logNotificationAdapter.sendRecoveryAlert(incident, endpoint, contactGroups);
    dispatchWebhook("INCIDENT_RESOLVED", incident, endpoint, contactGroups);
  }

  private void dispatchWebhook(
      String eventType,
      Incident incident,
      MonitoredEndpoint endpoint,
      List<ContactGroup> contactGroups) {
    persistEmailNotifications(eventType, incident, endpoint, contactGroups);

    Set<String> webhookUrls = collectWebhookUrls(contactGroups);
    if (!configuredWebhookUrl.isBlank()) {
      webhookUrls.add(configuredWebhookUrl.trim());
    }

    if (webhookUrls.isEmpty()) {
      log.info("Bỏ qua webhook cho incident {} vì chưa có URL nào được cấu hình", incident.getId());
      return;
    }

    String payload = toPayload(eventType, incident, endpoint, contactGroups);
    webhookUrls.forEach(
        url -> persistWebhookNotification(url, payload, incident, eventType, endpoint));
  }

  private Set<String> collectWebhookUrls(List<ContactGroup> contactGroups) {
    Set<String> webhookUrls = new LinkedHashSet<>();
    if (contactGroups == null) {
      return webhookUrls;
    }

    contactGroups.stream()
        .filter(group -> Boolean.TRUE.equals(group.getIsActive()))
        .map(ContactGroup::getWebhookUrls)
        .filter(urls -> urls != null && !urls.isEmpty())
        .flatMap(List::stream)
        .map(String::trim)
        .filter(url -> !url.isBlank())
        .forEach(webhookUrls::add);
    return webhookUrls;
  }

  private String toPayload(
      String eventType,
      Incident incident,
      MonitoredEndpoint endpoint,
      List<ContactGroup> contactGroups) {
    NotificationPayload payload =
        new NotificationPayload(
            eventType,
            LocalDateTime.now(),
            endpoint.getWorkspaceId(),
            endpoint.getId(),
            endpoint.getName(),
            endpoint.getUrl(),
            incident.getId(),
            incident.getStatus() != null ? incident.getStatus().name() : null,
            incident.getSeverity() != null ? incident.getSeverity().name() : null,
            incident.getReason(),
            incident.getFailureCount(),
            incident.getStartedAt(),
            incident.getResolvedAt(),
            contactGroups == null ? 0 : contactGroups.size());

    try {
      return objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException ex) {
      throw new IllegalStateException("Không thể serialize notification payload", ex);
    }
  }

  private void persistEmailNotifications(
      String eventType,
      Incident incident,
      MonitoredEndpoint endpoint,
      List<ContactGroup> contactGroups) {
    if (contactGroups == null) {
      return;
    }

    Set<String> emailRecipients = new LinkedHashSet<>();
    contactGroups.stream()
        .filter(group -> Boolean.TRUE.equals(group.getIsActive()))
        .map(ContactGroup::getEmailAddresses)
        .filter(emails -> emails != null && !emails.isEmpty())
        .flatMap(List::stream)
        .map(String::trim)
        .filter(email -> !email.isBlank())
        .forEach(emailRecipients::add);

    String message = buildEmailMessage(eventType, incident, endpoint);
    emailRecipients.forEach(
        email ->
            notificationRepository.save(
                Notification.builder()
                    .incidentId(incident.getId())
                    .channel(NotificationChannel.EMAIL)
                    .recipient(email)
                    .message(message)
                    .status(NotificationStatus.SENT)
                    .sentAt(LocalDateTime.now())
                    .build()));
  }

  private void persistWebhookNotification(
      String webhookUrl,
      String payload,
      Incident incident,
      String eventType,
      MonitoredEndpoint endpoint) {
    Notification notification =
        notificationRepository.save(
            Notification.builder()
                .incidentId(incident.getId())
                .channel(NotificationChannel.WEBHOOK)
                .recipient(webhookUrl)
                .message(payload)
                .status(NotificationStatus.PENDING)
                .build());

    WebhookDeliveryClient.DeliveryResult result = webhookDeliveryClient.post(webhookUrl, payload);
    notification.setStatus(
        result.delivered() ? NotificationStatus.SENT : NotificationStatus.FAILED);
    notification.setSentAt(result.delivered() ? LocalDateTime.now() : null);
    notification.setErrorMessage(result.errorMessage());
    notification.setMessage(payload);

    if (!result.delivered()) {
      log.warn(
          "Khong the gui {} webhook cho incident {} toi {}: {}",
          eventType,
          incident.getId(),
          webhookUrl,
          result.errorMessage());
    }

    notificationRepository.save(notification);
  }

  private String buildEmailMessage(
      String eventType, Incident incident, MonitoredEndpoint endpoint) {
    String startedAt =
        incident.getStartedAt() != null
            ? incident.getStartedAt().format(MESSAGE_TIME_FORMATTER)
            : "N/A";
    String resolvedAt =
        incident.getResolvedAt() != null
            ? incident.getResolvedAt().format(MESSAGE_TIME_FORMATTER)
            : "N/A";

    return String.format(
        """
        [%s] %s
        Endpoint: %s
        URL: %s
        Incident ID: %s
        Reason: %s
        Severity: %s
        Started At: %s
        Resolved At: %s
        """,
        eventType,
        incident.getReason(),
        endpoint.getName(),
        endpoint.getUrl(),
        incident.getId(),
        incident.getReason(),
        incident.getSeverity(),
        startedAt,
        resolvedAt);
  }

  private record NotificationPayload(
      String eventType,
      LocalDateTime sentAt,
      Long workspaceId,
      Long endpointId,
      String endpointName,
      String endpointUrl,
      Long incidentId,
      String incidentStatus,
      String severity,
      String reason,
      Integer failureCount,
      LocalDateTime startedAt,
      LocalDateTime resolvedAt,
      int contactGroupCount) {}
}
