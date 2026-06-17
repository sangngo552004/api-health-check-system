package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.model.Notification;
import java.util.List;

public interface NotificationRepository {
  Notification save(Notification notification);

  List<Notification> findByIncidentId(Long incidentId);
}
