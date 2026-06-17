package com.example.apihealthchecksystem.infrastructure.notification;

import com.example.apihealthchecksystem.domain.model.ContactGroup;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogNotificationAdapter {

  public void sendIncidentAlert(
      Incident incident, MonitoredEndpoint endpoint, List<ContactGroup> contactGroups) {
    log.error("=====================================================");
    log.error("[ALERT] INCIDENT OPENED!");
    log.error("Endpoint: {} ({})", endpoint.getName(), endpoint.getUrl());
    log.error("Incident ID: {}", incident.getId());
    log.error("Reason: {}", incident.getReason());
    log.error("Severity: {}", incident.getSeverity());

    if (contactGroups == null || contactGroups.isEmpty()) {
      log.warn(
          "Không có ContactGroup nào được cấu hình để nhận thông báo cho Workspace {}",
          endpoint.getWorkspaceId());
    } else {
      contactGroups.forEach(
          group -> {
            log.info("-> Đang gửi cảnh báo tới ContactGroup: {}", group.getName());
            if (group.getEmailAddresses() != null) {
              group
                  .getEmailAddresses()
                  .forEach(email -> log.info("    -> Gửi email tới: {}", email));
            }
            if (group.getWebhookUrls() != null) {
              group
                  .getWebhookUrls()
                  .forEach(webhook -> log.info("    -> Gắn webhook tới: {}", webhook));
            }
          });
    }
    log.error("=====================================================");
  }

  public void sendRecoveryAlert(
      Incident incident, MonitoredEndpoint endpoint, List<ContactGroup> contactGroups) {
    log.info("=====================================================");
    log.info("[RECOVERY] INCIDENT RESOLVED!");
    log.info("Endpoint: {} ({})", endpoint.getName(), endpoint.getUrl());
    log.info("Incident ID: {} đã được khắc phục.", incident.getId());

    if (contactGroups != null && !contactGroups.isEmpty()) {
      contactGroups.forEach(
          group ->
              log.info("-> Đang gửi thông báo phục hồi tới ContactGroup: {}", group.getName()));
    }
    log.info("=====================================================");
  }
}
