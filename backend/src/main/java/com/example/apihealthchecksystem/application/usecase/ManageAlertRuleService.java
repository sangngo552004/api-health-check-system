package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.AlertRuleCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AlertRuleUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.AlertRuleDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.AccessDeniedException;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.exception.ValidationException;
import com.example.apihealthchecksystem.application.mapper.AlertRuleDtoMapper;
import com.example.apihealthchecksystem.application.port.in.ManageAlertRuleUseCase;
import com.example.apihealthchecksystem.application.port.out.AlertRuleRepository;
import com.example.apihealthchecksystem.application.port.out.ContactGroupRepository;
import com.example.apihealthchecksystem.application.support.PagingUtils;
import com.example.apihealthchecksystem.domain.model.AlertRule;
import com.example.apihealthchecksystem.domain.model.ContactGroup;
import com.example.apihealthchecksystem.domain.valueobject.AlertRuleType;
import com.example.apihealthchecksystem.domain.valueobject.ComparisonOperator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ManageAlertRuleService implements ManageAlertRuleUseCase {

  private final AlertRuleRepository repository;
  private final ContactGroupRepository contactGroupRepository;
  private final AlertRuleDtoMapper mapper;

  @Override
  public AlertRuleDto createAlertRule(Long workspaceId, AlertRuleCreateCommand command) {
    validateContactGroups(command.contactGroupIds(), workspaceId);
    AlertRule rule = mapper.toDomain(command);
    validateRuleConfiguration(rule);
    rule.setWorkspaceId(workspaceId);
    return mapper.toDto(repository.save(rule));
  }

  @Override
  public AlertRuleDto updateAlertRule(Long workspaceId, AlertRuleUpdateCommand command) {
    AlertRule existing = getAlertRuleById(command.id());
    validateWorkspaceAccess(existing.getWorkspaceId(), workspaceId);
    validateContactGroups(command.contactGroupIds(), workspaceId);

    existing.setName(command.name());
    existing.setRuleType(command.ruleType());
    existing.setOperator(command.operator());
    existing.setThresholdValue(command.thresholdValue());
    existing.setSeverity(command.severity());
    existing.setContactGroupIds(command.contactGroupIds());
    if (command.isActive() != null) {
      existing.setIsActive(command.isActive());
    }
    validateRuleConfiguration(existing);

    return mapper.toDto(repository.save(existing));
  }

  @Override
  public AlertRuleDto getAlertRule(Long workspaceId, Long id) {
    AlertRule rule = getAlertRuleById(id);
    validateWorkspaceAccess(rule.getWorkspaceId(), workspaceId);

    return mapper.toDto(rule);
  }

  @Override
  public PagedResponseDto<AlertRuleDto> getAlertRulesByWorkspace(
      Long workspaceId,
      String search,
      String ruleType,
      String operator,
      Boolean isActive,
      int page,
      int size,
      String sortBy,
      String sortDir) {
    int safePage = PagingUtils.normalizePage(page);
    int safeSize = PagingUtils.normalizeSize(size);
    var result =
        repository.searchByWorkspace(
            workspaceId,
            search,
            parseAlertRuleType(ruleType),
            parseComparisonOperator(operator),
            isActive,
            safePage,
            safeSize,
            sortBy,
            sortDir);

    List<AlertRuleDto> dtos =
        result.items().stream().map(mapper::toDto).collect(Collectors.toList());

    return PagedResponseDto.of(dtos, safePage, safeSize, result.totalItems());
  }

  @Override
  public void deleteAlertRule(Long workspaceId, Long id) {
    AlertRule rule = getAlertRuleById(id);
    validateWorkspaceAccess(rule.getWorkspaceId(), workspaceId);
    repository.deleteById(id);
  }

  private AlertRule getAlertRuleById(Long alertRuleId) {
    return repository
        .findById(alertRuleId)
        .orElseThrow(
            () -> new ResourceNotFoundException(AppErrorCode.ALERT_RULE_NOT_FOUND, alertRuleId));
  }

  private void validateWorkspaceAccess(Long resourceWorkspaceId, Long requestedWorkspaceId) {
    if (!resourceWorkspaceId.equals(requestedWorkspaceId)) {
      throw new AccessDeniedException();
    }
  }

  private void validateContactGroups(List<Long> contactGroupIds, Long workspaceId) {
    if (contactGroupIds == null || contactGroupIds.isEmpty()) {
      return;
    }

    List<ContactGroup> contactGroups = contactGroupRepository.findAllByIds(contactGroupIds);
    if (contactGroups.size() != contactGroupIds.size()) {
      throw new ResourceNotFoundException(AppErrorCode.CONTACT_GROUP_NOT_FOUND_IN_WORKSPACE, workspaceId);
    }

    boolean invalidWorkspace =
        contactGroups.stream().anyMatch(group -> !workspaceId.equals(group.getWorkspaceId()));
    if (invalidWorkspace) {
      throw new ValidationException(AppErrorCode.CONTACT_GROUP_WORKSPACE_MISMATCH);
    }
  }

  private AlertRuleType parseAlertRuleType(String ruleType) {
    if (ruleType == null || ruleType.isBlank()) {
      return null;
    }
    try {
      return AlertRuleType.valueOf(ruleType.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new ValidationException(AppErrorCode.INVALID_ALERT_RULE_TYPE);
    }
  }

  private ComparisonOperator parseComparisonOperator(String operator) {
    if (operator == null || operator.isBlank()) {
      return null;
    }
    try {
      return ComparisonOperator.valueOf(operator.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new ValidationException(AppErrorCode.INVALID_COMPARISON_OPERATOR);
    }
  }

  private void validateRuleConfiguration(AlertRule rule) {
    if (rule.getThresholdValue() == null) {
      throw new ValidationException(AppErrorCode.INVALID_ALERT_RULE_TYPE);
    }

    if (AlertRuleType.CONSECUTIVE_FAILURE.equals(rule.getRuleType())) {
      if (rule.getThresholdValue() < 1) {
        throw new ValidationException(AppErrorCode.INVALID_ALERT_RULE_TYPE);
      }
      rule.setOperator(null);
      return;
    }

    if (rule.getOperator() == null) {
      throw new ValidationException(AppErrorCode.INVALID_COMPARISON_OPERATOR);
    }
  }
}
