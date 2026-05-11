package com.example.apihealthchecksystem.application.dto.request;

import com.example.apihealthchecksystem.domain.valueobject.AlertRuleType;
import com.example.apihealthchecksystem.domain.valueobject.ComparisonOperator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AlertRuleUpdateCommand(
    @NotNull Long id,
    @NotBlank String name,
    @NotNull AlertRuleType ruleType,
    @NotNull ComparisonOperator operator,
    @NotNull Double thresholdValue,
    Boolean isActive,
    List<Long> contactGroupIds,
    Boolean overrideDefaultContacts) {}
