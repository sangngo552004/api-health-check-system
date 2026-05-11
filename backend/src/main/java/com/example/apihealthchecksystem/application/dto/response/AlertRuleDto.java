package com.example.apihealthchecksystem.application.dto.response;

import com.example.apihealthchecksystem.domain.valueobject.AlertRuleType;
import com.example.apihealthchecksystem.domain.valueobject.ComparisonOperator;
import java.util.List;

public record AlertRuleDto(
    Long id,
    String name,
    AlertRuleType ruleType,
    ComparisonOperator operator,
    Double thresholdValue,
    Long workspaceId,
    Boolean isActive,
    List<Long> contactGroupIds,
    Boolean overrideDefaultContacts) {}
