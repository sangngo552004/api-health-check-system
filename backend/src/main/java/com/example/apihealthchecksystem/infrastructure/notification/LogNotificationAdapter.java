package com.example.apihealthchecksystem.infrastructure.notification;

import com.example.apihealthchecksystem.application.port.out.NotificationPort;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogNotificationAdapter implements NotificationPort {

  @Override
  public void sendIncidentAlert(
      Incident incident, MonitoredEndpoint endpoint, Set<String> recipientEmails) {
    log.error("=====================================================");
    log.error("[ALERT] INCIDENT OPENED!");
    log.error("Endpoint: {} ({})", endpoint.getName(), endpoint.getUrl());
    log.error("Incident ID: {}", incident.getId());
    log.error("Reason: {}", incident.getReason());
    log.error("Severity: {}", incident.getSeverity());

    if (recipientEmails == null || recipientEmails.isEmpty()) {
      log.warn(
          "Không có email nào được cấu hình để nhận thông báo cho Workspace {}",
          endpoint.getWorkspaceId());
    } else {
      recipientEmails.forEach(email -> log.info("-> Gửi email tới: {}", email));
    }
    log.error("=====================================================");
  }

  @Override
  public void sendRecoveryAlert(
      Incident incident, MonitoredEndpoint endpoint, Set<String> recipientEmails) {
    log.info("=====================================================");
    log.info("[RECOVERY] INCIDENT RESOLVED!");
    log.info("Endpoint: {} ({})", endpoint.getName(), endpoint.getUrl());
    log.info("Incident ID: {} đã được khắc phục.", incident.getId());

    if (recipientEmails != null && !recipientEmails.isEmpty()) {
      recipientEmails.forEach(email -> log.info("-> Gửi email phục hồi tới: {}", email));
    }
    log.info("=====================================================");
  }
}
