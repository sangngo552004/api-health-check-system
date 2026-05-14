package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.AlertRuleCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AlertRuleUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.AlertRuleDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.AccessDeniedException;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.mapper.AlertRuleDtoMapper;
import com.example.apihealthchecksystem.application.port.in.ManageAlertRuleUseCase;
import com.example.apihealthchecksystem.application.port.out.AlertRuleRepository;
import com.example.apihealthchecksystem.domain.model.AlertRule;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ManageAlertRuleService implements ManageAlertRuleUseCase {

  private final AlertRuleRepository repository;
  private final AlertRuleDtoMapper mapper;

  @Override
  public AlertRuleDto createAlertRule(Long workspaceId, AlertRuleCreateCommand command) {
    AlertRule rule = mapper.toDomain(command);
    rule.setWorkspaceId(workspaceId);
    return mapper.toDto(repository.save(rule));
  }

  @Override
  public AlertRuleDto updateAlertRule(Long workspaceId, AlertRuleUpdateCommand command) {
    AlertRule existing = getAlertRuleById(command.id());
    validateWorkspaceAccess(existing.getWorkspaceId(), workspaceId);

    existing.setName(command.name());
    existing.setRuleType(command.ruleType());
    existing.setOperator(command.operator());
    existing.setThresholdValue(command.thresholdValue());
    existing.setContactGroupIds(command.contactGroupIds());
    existing.setOverrideDefaultContacts(command.overrideDefaultContacts());
    if (command.isActive() != null) {
      existing.setIsActive(command.isActive());
    }

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
      Long workspaceId, int page, int size) {
    List<AlertRule> rules = repository.findByWorkspaceId(workspaceId, page, size);
    long total = repository.countByWorkspaceId(workspaceId);

    List<AlertRuleDto> dtos = rules.stream().map(mapper::toDto).collect(Collectors.toList());

    return PagedResponseDto.of(dtos, page, size, total);
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
}
