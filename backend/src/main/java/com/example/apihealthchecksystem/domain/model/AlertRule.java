package com.example.apihealthchecksystem.domain.model;

import com.example.apihealthchecksystem.domain.valueobject.AlertRuleType;
import com.example.apihealthchecksystem.domain.valueobject.ComparisonOperator;
import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRule {
  private Long id;
  private String name;
  private AlertRuleType ruleType;
  private ComparisonOperator operator;
  private Double thresholdValue;
  private IncidentSeverity severity;
  private Boolean isActive;
  private List<Long> contactGroupIds;
  private Long createdBy;
  private Long workspaceId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
