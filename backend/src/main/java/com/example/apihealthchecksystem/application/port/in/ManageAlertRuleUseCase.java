package com.example.apihealthchecksystem.application.port.in;

import com.example.apihealthchecksystem.application.dto.request.AlertRuleCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AlertRuleUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.AlertRuleDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;

public interface ManageAlertRuleUseCase {
  AlertRuleDto createAlertRule(Long workspaceId, AlertRuleCreateCommand command);

  AlertRuleDto updateAlertRule(Long workspaceId, AlertRuleUpdateCommand command);

  AlertRuleDto getAlertRule(Long workspaceId, Long id);

  PagedResponseDto<AlertRuleDto> getAlertRulesByWorkspace(Long workspaceId, int page, int size);

  void deleteAlertRule(Long workspaceId, Long id);
}
