package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.EndpointCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.EndpointUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.EndpointDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.AccessDeniedException;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.exception.ValidationException;
import com.example.apihealthchecksystem.application.mapper.EndpointDtoMapper;
import com.example.apihealthchecksystem.application.port.in.ManageEndpointUseCase;
import com.example.apihealthchecksystem.application.port.out.AlertRuleRepository;
import com.example.apihealthchecksystem.application.port.out.CheckPolicyRepository;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.application.support.PagingUtils;
import com.example.apihealthchecksystem.domain.model.AlertRule;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ManageEndpointService implements ManageEndpointUseCase {

  private final EndpointRepository endpointRepository;
  private final CheckPolicyRepository checkPolicyRepository;
  private final AlertRuleRepository alertRuleRepository;
  private final EndpointDtoMapper mapper;

  @Override
  public EndpointDto createEndpoint(
      Long workspaceId, Long currentUserId, EndpointCreateCommand command) {
    CheckPolicy policy = getCheckPolicy(command.policyId());
    validateWorkspaceAccess(policy.getWorkspaceId(), workspaceId);
    validateAlertRules(command.alertRuleIds(), workspaceId);

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
    validateAlertRules(command.alertRuleIds(), workspaceId);

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
      Long workspaceId,
      String search,
      String environment,
      String status,
      String method,
      String checkType,
      Boolean isActive,
      int page,
      int size,
      String sortBy,
      String sortDir) {
    int safePage = PagingUtils.normalizePage(page);
    int safeSize = PagingUtils.normalizeSize(size);
    var result =
        endpointRepository.searchByWorkspace(
            workspaceId,
            search,
            environment,
            parseEndpointStatus(status),
            parseHttpMethod(method),
            parseCheckType(checkType),
            isActive,
            safePage,
            safeSize,
            sortBy,
            sortDir);
    Map<Long, CheckPolicy> policiesById =
        checkPolicyRepository.findAllByIds(
                result.items().stream()
                    .map(MonitoredEndpoint::getPolicyId)
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .toList())
            .stream()
            .collect(Collectors.toMap(CheckPolicy::getId, Function.identity()));

    List<EndpointDto> dtos =
        result.items().stream()
            .map(endpoint -> mapper.toDto(endpoint, policiesById.get(endpoint.getPolicyId())))
            .collect(Collectors.toList());

    return PagedResponseDto.of(dtos, safePage, safeSize, result.totalItems());
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

  private void validateAlertRules(List<Long> alertRuleIds, Long workspaceId) {
    if (alertRuleIds == null || alertRuleIds.isEmpty()) {
      throw new ValidationException(
          AppErrorCode.VALIDATION_ERROR, "Endpoint phai co it nhat mot alert rule");
    }

    List<AlertRule> alertRules = alertRuleRepository.findAllByIds(alertRuleIds);
    if (alertRules.size() != alertRuleIds.size()) {
      throw new ResourceNotFoundException(AppErrorCode.ALERT_RULE_NOT_FOUND_IN_WORKSPACE, workspaceId);
    }

    boolean invalidWorkspace =
        alertRules.stream().anyMatch(rule -> !workspaceId.equals(rule.getWorkspaceId()));
    if (invalidWorkspace) {
      throw new ValidationException(AppErrorCode.ALERT_RULE_WORKSPACE_MISMATCH);
    }
  }

  private EndpointStatus parseEndpointStatus(String status) {
    if (status == null || status.isBlank()) {
      return null;
    }
    try {
      return EndpointStatus.valueOf(status.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new ValidationException(AppErrorCode.INVALID_ENDPOINT_STATUS);
    }
  }

  private HttpMethod parseHttpMethod(String method) {
    if (method == null || method.isBlank()) {
      return null;
    }
    try {
      return HttpMethod.valueOf(method.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new ValidationException(AppErrorCode.INVALID_HTTP_METHOD);
    }
  }

  private CheckType parseCheckType(String checkType) {
    if (checkType == null || checkType.isBlank()) {
      return null;
    }
    try {
      return CheckType.valueOf(checkType.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new ValidationException(AppErrorCode.INVALID_CHECK_TYPE);
    }
  }
}
