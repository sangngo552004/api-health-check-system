package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.CheckPolicyCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.CheckPolicyUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.CheckPolicyDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.AccessDeniedException;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.mapper.CheckPolicyDtoMapper;
import com.example.apihealthchecksystem.application.port.in.ManageCheckPolicyUseCase;
import com.example.apihealthchecksystem.application.port.out.CheckPolicyRepository;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ManageCheckPolicyService implements ManageCheckPolicyUseCase {

  private final CheckPolicyRepository repository;
  private final CheckPolicyDtoMapper mapper;

  @Override
  public CheckPolicyDto createPolicy(Long workspaceId, CheckPolicyCreateCommand command) {
    CheckPolicy policy = mapper.toDomain(command);
    policy.setWorkspaceId(workspaceId);
    return mapper.toDto(repository.save(policy));
  }

  @Override
  public CheckPolicyDto updatePolicy(Long workspaceId, CheckPolicyUpdateCommand command) {
    CheckPolicy existing = getPolicyById(command.id());
    validateWorkspaceAccess(existing.getWorkspaceId(), workspaceId);

    existing.setName(command.name());
    existing.setIntervalSeconds(command.intervalSeconds());
    existing.setTimeoutMillis(command.timeoutMillis());
    existing.setRetryCount(command.retryCount());
    existing.setFailureThreshold(command.failureThreshold());
    existing.setLatencyThresholdMillis(command.latencyThresholdMillis());
    existing.setExpectedStatusCode(command.expectedStatusCode());
    existing.setExpectedResponseBody(command.expectedResponseBody());
    existing.setResponseRegex(command.responseRegex());

    return mapper.toDto(repository.save(existing));
  }

  @Override
  public CheckPolicyDto getPolicy(Long workspaceId, Long id) {
    CheckPolicy policy = getPolicyById(id);
    validateWorkspaceAccess(policy.getWorkspaceId(), workspaceId);

    return mapper.toDto(policy);
  }

  @Override
  public PagedResponseDto<CheckPolicyDto> getPoliciesByWorkspace(
      Long workspaceId, int page, int size) {
    List<CheckPolicy> policies = repository.findByWorkspaceId(workspaceId, page, size);
    long total = repository.countByWorkspaceId(workspaceId);

    List<CheckPolicyDto> dtos = policies.stream().map(mapper::toDto).collect(Collectors.toList());

    return PagedResponseDto.of(dtos, page, size, total);
  }

  @Override
  public void deletePolicy(Long workspaceId, Long id) {
    CheckPolicy policy = getPolicyById(id);
    validateWorkspaceAccess(policy.getWorkspaceId(), workspaceId);
    repository.deleteById(id);
  }

  private CheckPolicy getPolicyById(Long policyId) {
    return repository
        .findById(policyId)
        .orElseThrow(
            () -> new ResourceNotFoundException(AppErrorCode.CHECK_POLICY_NOT_FOUND, policyId));
  }

  private void validateWorkspaceAccess(Long resourceWorkspaceId, Long requestedWorkspaceId) {
    if (!resourceWorkspaceId.equals(requestedWorkspaceId)) {
      throw new AccessDeniedException();
    }
  }
}
