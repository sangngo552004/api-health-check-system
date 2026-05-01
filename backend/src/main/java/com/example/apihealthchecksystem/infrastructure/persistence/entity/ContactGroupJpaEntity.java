package com.example.apihealthchecksystem.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contact_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactGroupJpaEntity extends BaseJpaEntity {

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @ElementCollection
  @CollectionTable(
      name = "contact_group_users",
      joinColumns = @JoinColumn(name = "contact_group_id"))
  @Column(name = "user_id")
  private List<Long> userIds;

  @ElementCollection
  @CollectionTable(
      name = "contact_group_emails",
      joinColumns = @JoinColumn(name = "contact_group_id"))
  @Column(name = "email_address")
  private List<String> emailAddresses;

  @ElementCollection
  @CollectionTable(
      name = "contact_group_webhooks",
      joinColumns = @JoinColumn(name = "contact_group_id"))
  @Column(name = "webhook_url")
  private List<String> webhookUrls;

  @Column(name = "is_active")
  private Boolean isActive;

  @Column(name = "created_by")
  private Long createdBy;
}
