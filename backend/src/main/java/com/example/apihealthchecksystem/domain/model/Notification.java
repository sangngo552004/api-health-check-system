package com.example.apihealthchecksystem.domain.model;

import com.example.apihealthchecksystem.domain.valueobject.NotificationChannel;
import com.example.apihealthchecksystem.domain.valueobject.NotificationStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Notification {
  private Long id;
  private Long incidentId;
  private NotificationChannel channel;
  private String recipient;
  private String message;
  private LocalDateTime sentAt;
  private NotificationStatus status;
}
