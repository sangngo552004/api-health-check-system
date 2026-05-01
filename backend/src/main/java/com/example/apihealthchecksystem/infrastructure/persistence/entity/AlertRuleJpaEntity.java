package com.example.apihealthchecksystem.infrastructure.persistence.entity;

import com.example.apihealthchecksystem.domain.valueobject.AlertRuleType;
import com.example.apihealthchecksystem.domain.valueobject.ComparisonOperator;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alert_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertRuleJpaEntity extends BaseJpaEntity {

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "rule_type", nullable = false, length = 50)
  private AlertRuleType ruleType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ComparisonOperator operator;

  @Column(name = "threshold_value")
  private Double thresholdValue;

  @Column(name = "is_active")
  private Boolean isActive;

  @ElementCollection
  @CollectionTable(
      name = "alert_rule_contact_groups",
      joinColumns = @JoinColumn(name = "alert_rule_id"))
  @Column(name = "contact_group_id")
  private List<Long> contactGroupIds;

  @Column(name = "override_default_contacts")
  private Boolean overrideDefaultContacts;

  @Column(name = "created_by")
  private Long createdBy;
}
