package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.EndpointCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.EndpointUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.EndpointDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.AccessDeniedException;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.mapper.EndpointDtoMapper;
import com.example.apihealthchecksystem.application.port.in.ManageEndpointUseCase;
import com.example.apihealthchecksystem.application.port.out.CheckPolicyRepository;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ManageEndpointService implements ManageEndpointUseCase {

  private final EndpointRepository endpointRepository;
  private final CheckPolicyRepository checkPolicyRepository;
  private final EndpointDtoMapper mapper;

  @Override
  public EndpointDto createEndpoint(
      Long workspaceId, Long currentUserId, EndpointCreateCommand command) {
    CheckPolicy policy = getCheckPolicy(command.policyId());
    validateWorkspaceAccess(policy.getWorkspaceId(), workspaceId);

    MonitoredEndpoint endpoint = mapper.toDomain(command);
    endpoint.initializeForCreation(workspaceId, currentUserId, LocalDateTime.now());

    MonitoredEndpoint savedEndpoint = endpointRepository.save(endpoint);
    return mapper.toDto(savedEndpoint, policy);
  }

  @Override
  public EndpointDto updateEndpoint(Long workspaceId, EndpointUpdateCommand command) {
    MonitoredEndpoint endpoint = getEndpoint(command.id());
    validateWorkspaceAccess(endpoint.getWorkspaceId(), workspaceId);
    CheckPolicy policy = getCheckPolicy(command.policyId());
    validateWorkspaceAccess(policy.getWorkspaceId(), workspaceId);

    endpoint.applyUpdates(
        command.name(),
        command.url(),
        command.method(),
        command.environment(),
        command.checkType(),
        command.policyId(),
        command.alertRuleIds(),
        command.tags(),
        command.headers(),
        command.requestBody(),
        command.isActive(),
        LocalDateTime.now());

    MonitoredEndpoint savedEndpoint = endpointRepository.save(endpoint);
    return mapper.toDto(savedEndpoint, policy);
  }

  @Override
  public EndpointDto getEndpoint(Long workspaceId, Long id) {
    MonitoredEndpoint endpoint = getEndpoint(id);
    validateWorkspaceAccess(endpoint.getWorkspaceId(), workspaceId);
    CheckPolicy policy = checkPolicyRepository.findById(endpoint.getPolicyId()).orElse(null);
    return mapper.toDto(endpoint, policy);
  }

  @Override
  public PagedResponseDto<EndpointDto> getEndpointsByWorkspace(
      Long workspaceId, int page, int size) {
    List<MonitoredEndpoint> endpoints =
        endpointRepository.findByWorkspaceId(workspaceId, page, size);
    long total = endpointRepository.countByWorkspaceId(workspaceId);

    List<EndpointDto> dtos =
        endpoints.stream()
            .map(
                endpoint -> {
                  CheckPolicy policy =
                      checkPolicyRepository.findById(endpoint.getPolicyId()).orElse(null);
                  return mapper.toDto(endpoint, policy);
                })
            .collect(Collectors.toList());

    return PagedResponseDto.of(dtos, page, size, total);
  }

  @Override
  public void deleteEndpoint(Long workspaceId, Long id) {
    MonitoredEndpoint endpoint = getEndpoint(id);
    validateWorkspaceAccess(endpoint.getWorkspaceId(), workspaceId);
    endpointRepository.deleteById(id);
  }

  private MonitoredEndpoint getEndpoint(Long endpointId) {
    return endpointRepository
        .findById(endpointId)
        .orElseThrow(
            () -> new ResourceNotFoundException(AppErrorCode.ENDPOINT_NOT_FOUND, endpointId));
  }

  private CheckPolicy getCheckPolicy(Long policyId) {
    return checkPolicyRepository
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
