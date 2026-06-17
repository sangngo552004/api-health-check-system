package com.example.apihealthchecksystem.infrastructure.persistence.entity;

import com.example.apihealthchecksystem.domain.valueobject.NotificationChannel;
import com.example.apihealthchecksystem.domain.valueobject.NotificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class NotificationJpaEntity extends BaseJpaEntity {

  @Column(name = "incident_id")
  private Long incidentId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private NotificationChannel channel;

  @Column(nullable = false, length = 512)
  private String recipient;

  @Column(columnDefinition = "TEXT")
  private String message;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private NotificationStatus status;

  @Column(name = "sent_at")
  private LocalDateTime sentAt;

  @Column(name = "error_message", columnDefinition = "TEXT")
  private String errorMessage;
}
