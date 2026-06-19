package com.example.apihealthchecksystem.application.dto.response;

import com.example.apihealthchecksystem.domain.valueobject.AlertRuleType;
import com.example.apihealthchecksystem.domain.valueobject.ComparisonOperator;
import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
import java.util.List;

public record AlertRuleDto(
    Long id,
    String name,
    AlertRuleType ruleType,
    ComparisonOperator operator,
    Double thresholdValue,
    IncidentSeverity severity,
    Long workspaceId,
    Boolean isActive,
    List<Long> contactGroupIds) {}
