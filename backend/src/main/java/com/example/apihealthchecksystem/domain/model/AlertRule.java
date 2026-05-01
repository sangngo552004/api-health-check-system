package com.example.apihealthchecksystem.domain.model;

import com.example.apihealthchecksystem.domain.valueobject.AlertRuleType;
import com.example.apihealthchecksystem.domain.valueobject.ComparisonOperator;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertRule {
  private Long id;
  private String name;
  private AlertRuleType ruleType;
  private ComparisonOperator operator;
  private Double thresholdValue;
  private Boolean isActive;
  private List<Long> contactGroupIds;
  private Boolean overrideDefaultContacts;
  private Long createdBy;
  private LocalDateTime createdAt;
}
