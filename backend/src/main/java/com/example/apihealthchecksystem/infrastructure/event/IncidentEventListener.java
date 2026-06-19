package com.example.apihealthchecksystem.infrastructure.event;

import com.example.apihealthchecksystem.application.port.out.NotificationPort;
import com.example.apihealthchecksystem.domain.event.IncidentOpenedEvent;
import com.example.apihealthchecksystem.domain.event.IncidentResolvedEvent;
import com.example.apihealthchecksystem.infrastructure.notification.IncidentNotificationContextResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncidentEventListener {

  private final IncidentNotificationContextResolver notificationContextResolver;
  private final NotificationPort notificationPort;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleIncidentOpened(IncidentOpenedEvent event) {
    log.info(
        "Nhận sự kiện IncidentOpenedEvent cho incident {} trên thread {}",
        event.incidentId(),
        Thread.currentThread().getName());

    var resolvedNotification =
        notificationContextResolver.resolve(event.incidentId(), event.endpointId());
    if (resolvedNotification.isEmpty()) {
      log.warn(
          "Không thể gửi alert cho incident {} vì incident={}, endpoint={}",
          event.incidentId(),
          "MISSING",
          "MISSING_OR_NOT_ACCESSIBLE");
      return;
    }

    var notification = resolvedNotification.get();
    log.info(
        "Chuẩn bị gửi alert cho incident {} endpoint {} với {} recipient(s): {}",
        notification.incident().getId(),
        notification.endpoint().getId(),
        notification.recipientEmails().size(),
        notification.recipientEmails());
    notificationPort.sendIncidentAlert(
        notification.incident(), notification.endpoint(), notification.recipientEmails());
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleIncidentResolved(IncidentResolvedEvent event) {
    log.info(
        "Nhận sự kiện IncidentResolvedEvent cho incident {} trên thread {}",
        event.incidentId(),
        Thread.currentThread().getName());

    var resolvedNotification =
        notificationContextResolver.resolve(event.incidentId(), event.endpointId());
    if (resolvedNotification.isEmpty()) {
      log.warn(
          "Không thể gửi recovery alert cho incident {} vì incident={}, endpoint={}",
          event.incidentId(),
          "MISSING",
          "MISSING_OR_NOT_ACCESSIBLE");
      return;
    }

    var notification = resolvedNotification.get();
    log.info(
        "Chuẩn bị gửi recovery alert cho incident {} endpoint {} với {} recipient(s): {}",
        notification.incident().getId(),
        notification.endpoint().getId(),
        notification.recipientEmails().size(),
        notification.recipientEmails());
    notificationPort.sendRecoveryAlert(
        notification.incident(), notification.endpoint(), notification.recipientEmails());
  }
}
