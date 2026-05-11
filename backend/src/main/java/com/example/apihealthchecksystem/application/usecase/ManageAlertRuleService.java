package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.AlertRuleCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AlertRuleUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.AlertRuleDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.AccessDeniedException;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.mapper.AlertRuleDtoMapper;
import com.example.apihealthchecksystem.application.port.in.ManageAlertRuleUseCase;
import com.example.apihealthchecksystem.application.port.out.AlertRuleRepository;
import com.example.apihealthchecksystem.domain.model.AlertRule;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManageAlertRuleService implements ManageAlertRuleUseCase {

  private final AlertRuleRepository repository;
  private final AlertRuleDtoMapper mapper;

  @Override
  @Transactional
  public AlertRuleDto createAlertRule(Long workspaceId, AlertRuleCreateCommand command) {
    AlertRule rule = mapper.toDomain(command);
    rule.setWorkspaceId(workspaceId);
    return mapper.toDto(repository.save(rule));
  }

  @Override
  @Transactional
  public AlertRuleDto updateAlertRule(Long workspaceId, AlertRuleUpdateCommand command) {
    AlertRule existing =
        repository
            .findById(command.id())
            .orElseThrow(() -> new ResourceNotFoundException("AlertRule", command.id()));

    if (!existing.getWorkspaceId().equals(workspaceId)) {
      throw new AccessDeniedException("AlertRule không thuộc về Workspace này.");
    }

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
    AlertRule rule =
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AlertRule", id));

    if (!rule.getWorkspaceId().equals(workspaceId)) {
      throw new AccessDeniedException("AlertRule không thuộc về Workspace này.");
    }

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
  @Transactional
  public void deleteAlertRule(Long workspaceId, Long id) {
    AlertRule rule =
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AlertRule", id));

    if (!rule.getWorkspaceId().equals(workspaceId)) {
      throw new AccessDeniedException("AlertRule không thuộc về Workspace này.");
    }
    repository.deleteById(id);
  }
}
