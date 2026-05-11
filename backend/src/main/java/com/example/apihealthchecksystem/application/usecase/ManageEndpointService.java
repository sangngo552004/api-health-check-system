package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.EndpointCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.EndpointUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.EndpointDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.AccessDeniedException;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.mapper.EndpointDtoMapper;
import com.example.apihealthchecksystem.application.port.in.ManageEndpointUseCase;
import com.example.apihealthchecksystem.application.port.out.CheckPolicyRepository;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManageEndpointService implements ManageEndpointUseCase {

  private final EndpointRepository endpointRepository;
  private final CheckPolicyRepository checkPolicyRepository;
  private final EndpointDtoMapper mapper;

  @Override
  @Transactional
  public EndpointDto createEndpoint(Long workspaceId, EndpointCreateCommand command) {
    MonitoredEndpoint endpoint = mapper.toDomain(command);
    endpoint.setWorkspaceId(workspaceId);
    endpoint.setStatus(EndpointStatus.UP);
    endpoint.setCreatedAt(LocalDateTime.now());
    endpoint.setUpdatedAt(LocalDateTime.now());

    // Giả định userId được lấy từ Security Context (hiện tại fix cứng 1L để demo)
    endpoint.setCreatedBy(1L);

    MonitoredEndpoint savedEndpoint = endpointRepository.save(endpoint);
    CheckPolicy policy =
        checkPolicyRepository
            .findById(savedEndpoint.getPolicyId())
            .orElseThrow(
                () -> new ResourceNotFoundException("CheckPolicy", savedEndpoint.getPolicyId()));

    if (!policy.getWorkspaceId().equals(workspaceId)) {
      throw new AccessDeniedException("CheckPolicy không thuộc về Workspace này.");
    }

    return mapper.toDto(savedEndpoint, policy);
  }

  @Override
  @Transactional
  public EndpointDto updateEndpoint(Long workspaceId, EndpointUpdateCommand command) {
    MonitoredEndpoint endpoint =
        endpointRepository
            .findById(command.id())
            .orElseThrow(() -> new ResourceNotFoundException("MonitoredEndpoint", command.id()));

    if (!endpoint.getWorkspaceId().equals(workspaceId)) {
      throw new AccessDeniedException("Endpoint không thuộc về Workspace này.");
    }

    endpoint.setName(command.name());
    endpoint.setUrl(command.url());
    endpoint.setMethod(command.method());
    endpoint.setEnvironment(command.environment());
    endpoint.setCheckType(command.checkType());
    endpoint.setPolicyId(command.policyId());
    endpoint.setAlertRuleIds(command.alertRuleIds());
    endpoint.setTags(command.tags());
    endpoint.setHeaders(command.headers());
    endpoint.setRequestBody(command.requestBody());

    if (command.isActive() != null) {
      endpoint.setIsActive(command.isActive());
    }
    endpoint.setUpdatedAt(LocalDateTime.now());

    MonitoredEndpoint savedEndpoint = endpointRepository.save(endpoint);
    CheckPolicy policy = checkPolicyRepository.findById(savedEndpoint.getPolicyId()).orElse(null);
    if (policy != null && !policy.getWorkspaceId().equals(workspaceId)) {
      throw new AccessDeniedException("CheckPolicy không thuộc về Workspace này.");
    }

    return mapper.toDto(savedEndpoint, policy);
  }

  @Override
  public EndpointDto getEndpoint(Long workspaceId, Long id) {
    MonitoredEndpoint endpoint =
        endpointRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("MonitoredEndpoint", id));

    if (!endpoint.getWorkspaceId().equals(workspaceId)) {
      throw new AccessDeniedException("Endpoint không thuộc về Workspace này.");
    }
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
  @Transactional
  public void deleteEndpoint(Long workspaceId, Long id) {
    MonitoredEndpoint endpoint =
        endpointRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("MonitoredEndpoint", id));

    if (!endpoint.getWorkspaceId().equals(workspaceId)) {
      throw new AccessDeniedException("Endpoint không thuộc về Workspace này.");
    }

    endpointRepository.deleteById(id);
  }
}
