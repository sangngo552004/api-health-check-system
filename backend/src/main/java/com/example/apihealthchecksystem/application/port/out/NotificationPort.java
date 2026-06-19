package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import java.util.Set;

/**
 * Port out — Giao diện gửi thông báo cảnh báo qua email và lưu lịch sử thông báo.
 */
public interface NotificationPort {

  /**
   * Gửi cảnh báo khi Incident được mở.
   *
   * @param incident thông tin sự cố
   * @param endpoint endpoint gặp sự cố
   * @param recipientEmails danh sách email nhận thông báo
   */
  void sendIncidentAlert(Incident incident, MonitoredEndpoint endpoint, Set<String> recipientEmails);

  /**
   * Gửi thông báo phục hồi khi Incident được đóng.
   *
   * @param incident thông tin sự cố đã giải quyết
   * @param endpoint endpoint đã phục hồi
   * @param recipientEmails danh sách email nhận thông báo
   */
  void sendRecoveryAlert(Incident incident, MonitoredEndpoint endpoint, Set<String> recipientEmails);
}
