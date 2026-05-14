package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.model.ContactGroup;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import java.util.List;

/**
 * Port out — Giao diện gửi thông báo cảnh báo. Có thể được implement bởi LogNotificationAdapter,
 * EmailAdapter, WebhookAdapter, ...
 */
public interface NotificationPort {

  /**
   * Gửi cảnh báo khi Incident được mở.
   *
   * @param incident thông tin sự cố
   * @param endpoint endpoint gặp sự cố
   * @param contactGroups danh sách nhóm nhận thông báo của workspace đó
   */
  void sendIncidentAlert(
      Incident incident, MonitoredEndpoint endpoint, List<ContactGroup> contactGroups);

  /**
   * Gửi thông báo phục hồi khi Incident được đóng.
   *
   * @param incident thông tin sự cố đã giải quyết
   * @param endpoint endpoint đã phục hồi
   * @param contactGroups danh sách nhóm nhận thông báo
   */
  void sendRecoveryAlert(
      Incident incident, MonitoredEndpoint endpoint, List<ContactGroup> contactGroups);
}
