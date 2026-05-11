package com.example.apihealthchecksystem.application.port.in;

import com.example.apihealthchecksystem.application.dto.request.CheckPolicyCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.CheckPolicyUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.CheckPolicyDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;

public interface ManageCheckPolicyUseCase {
  CheckPolicyDto createPolicy(Long workspaceId, CheckPolicyCreateCommand command);

  CheckPolicyDto updatePolicy(Long workspaceId, CheckPolicyUpdateCommand command);

  CheckPolicyDto getPolicy(Long workspaceId, Long id);

  PagedResponseDto<CheckPolicyDto> getPoliciesByWorkspace(Long workspaceId, int page, int size);

  void deletePolicy(Long workspaceId, Long id);
}
