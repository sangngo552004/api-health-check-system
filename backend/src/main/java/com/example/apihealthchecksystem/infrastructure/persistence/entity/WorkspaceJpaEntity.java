package com.example.apihealthchecksystem.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workspaces")
@Getter
@Setter
public class WorkspaceJpaEntity extends BaseJpaEntity {

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false, unique = true)
  private String slug;

  @Column(name = "owner_id", nullable = false)
  private Long ownerId;

  @Column(name = "is_active")
  private Boolean isActive = true;
}
