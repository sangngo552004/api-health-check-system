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
import com.example.apihealthchecksystem.application.support.PagingUtils;
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
    applyDefaults(policy);
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
    existing.setDegradedResponseTimeMillis(command.degradedResponseTimeMillis());
    existing.setExpectedStatusCode(command.expectedStatusCode());
    existing.setExpectedResponseBody(command.expectedResponseBody());
    existing.setResponseRegex(command.responseRegex());
    applyDefaults(existing);

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
      Long workspaceId,
      String search,
      Integer expectedStatusCode,
      Boolean hasDegradedResponseTimeThreshold,
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
            expectedStatusCode,
            hasDegradedResponseTimeThreshold,
            safePage,
            safeSize,
            sortBy,
            sortDir);

    List<CheckPolicyDto> dtos =
        result.items().stream().map(mapper::toDto).collect(Collectors.toList());

    return PagedResponseDto.of(dtos, safePage, safeSize, result.totalItems());
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

  private void applyDefaults(CheckPolicy policy) {
    if (policy.getIntervalSeconds() == null) {
      policy.setIntervalSeconds(CheckPolicy.DEFAULT_INTERVAL_SECONDS);
    }
    if (policy.getTimeoutMillis() == null) {
      policy.setTimeoutMillis(CheckPolicy.DEFAULT_TIMEOUT_MILLIS);
    }
    if (policy.getRetryCount() == null) {
      policy.setRetryCount(CheckPolicy.DEFAULT_RETRY_COUNT);
    }
    if (policy.getExpectedStatusCode() == null) {
      policy.setExpectedStatusCode(CheckPolicy.DEFAULT_EXPECTED_STATUS_CODE);
    }
  }
}
