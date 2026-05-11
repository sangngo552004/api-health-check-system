package com.example.apihealthchecksystem.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "workspace_members")
@Getter
@Setter
public class WorkspaceMemberJpaEntity {

  @EmbeddedId private WorkspaceMemberId id;

  @Column(nullable = false)
  private String role; // ADMIN, MEMBER

  @CreatedDate
  @Column(name = "joined_at", nullable = false, updatable = false)
  private LocalDateTime joinedAt = LocalDateTime.now();
}
