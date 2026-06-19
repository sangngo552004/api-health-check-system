package com.example.apihealthchecksystem.infrastructure.notification;

import com.example.apihealthchecksystem.application.port.out.NotificationPort;
import com.example.apihealthchecksystem.application.port.out.NotificationRepository;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.model.Notification;
import com.example.apihealthchecksystem.domain.valueobject.NotificationChannel;
import com.example.apihealthchecksystem.domain.valueobject.NotificationStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Primary
@ConditionalOnProperty(prefix = "app.notification.mail", name = "enabled", havingValue = "true")
@Slf4j
public class EmailNotificationAdapter implements NotificationPort {
  private static final DateTimeFormatter MESSAGE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final JavaMailSender mailSender;
  private final NotificationRepository notificationRepository;
  private final String fromAddress;

  public EmailNotificationAdapter(
      JavaMailSender mailSender,
      NotificationRepository notificationRepository,
      @Value("${app.notification.mail.from:no-reply@localhost}") String fromAddress) {
    this.mailSender = mailSender;
    this.notificationRepository = notificationRepository;
    this.fromAddress = fromAddress;
  }

  @Override
  public void sendIncidentAlert(
      Incident incident, MonitoredEndpoint endpoint, Set<String> recipientEmails) {
    log.info(
        "EmailNotificationAdapter đang xử lý sendIncidentAlert cho incident {} với fromAddress={}",
        incident.getId(),
        fromAddress);
    logBaseNotification("INCIDENT OPENED", incident, endpoint, recipientEmails);
    sendEmailNotifications("INCIDENT_OPENED", incident, endpoint, recipientEmails);
  }

  @Override
  public void sendRecoveryAlert(
      Incident incident, MonitoredEndpoint endpoint, Set<String> recipientEmails) {
    log.info(
        "EmailNotificationAdapter đang xử lý sendRecoveryAlert cho incident {} với fromAddress={}",
        incident.getId(),
        fromAddress);
    logBaseNotification("INCIDENT RESOLVED", incident, endpoint, recipientEmails);
    sendEmailNotifications("INCIDENT_RESOLVED", incident, endpoint, recipientEmails);
  }

  private void logBaseNotification(
      String eventLabel,
      Incident incident,
      MonitoredEndpoint endpoint,
      Set<String> recipientEmails) {
    log.info("=====================================================");
    log.info("[{}] Endpoint: {} ({})", eventLabel, endpoint.getName(), endpoint.getUrl());
    log.info("Incident ID: {}", incident.getId());
    log.info("Reason: {}", incident.getReason());
    log.info("Severity: {}", incident.getSeverity());
    if (recipientEmails == null || recipientEmails.isEmpty()) {
      log.warn("Không có email nào được cấu hình để nhận thông báo cho incident {}", incident.getId());
    } else {
      recipientEmails.forEach(email -> log.info("-> Gửi email tới: {}", email));
    }
    log.info("=====================================================");
  }

  private void sendEmailNotifications(
      String eventType,
      Incident incident,
      MonitoredEndpoint endpoint,
      Set<String> recipientEmails) {
    if (recipientEmails == null || recipientEmails.isEmpty()) {
      log.info("Bỏ qua email cho incident {} vì chưa có recipient nào", incident.getId());
      return;
    }

    String message = buildEmailMessage(eventType, incident, endpoint);
    String subject = buildSubject(eventType, endpoint);
    for (String email : new LinkedHashSet<>(recipientEmails)) {
      log.info(
          "Tạo notification record PENDING cho incident {} tới {} với eventType={}",
          incident.getId(),
          email,
          eventType);
      Notification notification =
          notificationRepository.save(
              Notification.builder()
                  .incidentId(incident.getId())
                  .channel(NotificationChannel.EMAIL)
                  .recipient(email)
                  .message(message)
                  .status(NotificationStatus.PENDING)
                  .build());

      try {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromAddress);
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        log.info(
            "Đang gửi email incident {} tới {} với subject='{}' từ '{}'",
            incident.getId(),
            email,
            subject,
            fromAddress);
        mailSender.send(mailMessage);

        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        notification.setErrorMessage(null);
        log.info(
            "Gửi email thành công cho incident {} tới {} lúc {}",
            incident.getId(),
            email,
            notification.getSentAt());
      } catch (Exception ex) {
        log.warn("Không thể gửi email notification tới {} cho incident {}", email, incident.getId(), ex);
        notification.setStatus(NotificationStatus.FAILED);
        notification.setSentAt(null);
        notification.setErrorMessage(ex.getMessage());
      }

      notificationRepository.save(notification);
      log.info(
          "Đã lưu notification cho incident {} tới {} với status={} error={}",
          incident.getId(),
          email,
          notification.getStatus(),
          notification.getErrorMessage());
    }
  }

  private String buildSubject(String eventType, MonitoredEndpoint endpoint) {
    return String.format("[%s] %s", eventType, endpoint.getName());
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
}
