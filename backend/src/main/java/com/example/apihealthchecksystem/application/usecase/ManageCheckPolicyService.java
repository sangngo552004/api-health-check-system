package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.CheckPolicyCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.CheckPolicyUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.CheckPolicyDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.AccessDeniedException;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.mapper.CheckPolicyDtoMapper;
import com.example.apihealthchecksystem.application.port.in.ManageCheckPolicyUseCase;
import com.example.apihealthchecksystem.application.port.out.CheckPolicyRepository;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManageCheckPolicyService implements ManageCheckPolicyUseCase {

  private final CheckPolicyRepository repository;
  private final CheckPolicyDtoMapper mapper;

  @Override
  @Transactional
  public CheckPolicyDto createPolicy(Long workspaceId, CheckPolicyCreateCommand command) {
    CheckPolicy policy = mapper.toDomain(command);
    policy.setWorkspaceId(workspaceId);
    return mapper.toDto(repository.save(policy));
  }

  @Override
  @Transactional
  public CheckPolicyDto updatePolicy(Long workspaceId, CheckPolicyUpdateCommand command) {
    CheckPolicy existing =
        repository
            .findById(command.id())
            .orElseThrow(() -> new ResourceNotFoundException("CheckPolicy", command.id()));

    if (!existing.getWorkspaceId().equals(workspaceId)) {
      throw new AccessDeniedException("CheckPolicy không thuộc về Workspace này.");
    }

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
    CheckPolicy policy =
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("CheckPolicy", id));

    if (!policy.getWorkspaceId().equals(workspaceId)) {
      throw new AccessDeniedException("CheckPolicy không thuộc về Workspace này.");
    }

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
  @Transactional
  public void deletePolicy(Long workspaceId, Long id) {
    CheckPolicy policy =
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("CheckPolicy", id));

    if (!policy.getWorkspaceId().equals(workspaceId)) {
      throw new AccessDeniedException("CheckPolicy không thuộc về Workspace này.");
    }
    repository.deleteById(id);
  }
}
