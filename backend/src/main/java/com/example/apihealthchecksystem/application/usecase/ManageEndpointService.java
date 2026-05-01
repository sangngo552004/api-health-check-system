package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.EndpointCreateCommand;
import com.example.apihealthchecksystem.application.dto.EndpointDto;
import com.example.apihealthchecksystem.application.dto.EndpointUpdateCommand;
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

@RequiredArgsConstructor
public class ManageEndpointService implements ManageEndpointUseCase {

  private final EndpointRepository endpointRepository;
  private final CheckPolicyRepository checkPolicyRepository;
  private final EndpointDtoMapper mapper;

  @Override
  public EndpointDto createEndpoint(EndpointCreateCommand command) {
    MonitoredEndpoint endpoint = mapper.toDomain(command);
    endpoint.setStatus(EndpointStatus.UP);
    endpoint.setCreatedAt(LocalDateTime.now());
    endpoint.setUpdatedAt(LocalDateTime.now());

    // Giả định userId được lấy từ Security Context (hiện tại fix cứng 1L để demo)
    endpoint.setCreatedBy(1L);

    MonitoredEndpoint savedEndpoint = endpointRepository.save(endpoint);
    CheckPolicy policy =
        checkPolicyRepository
            .findById(savedEndpoint.getPolicyId())
            .orElseThrow(() -> new IllegalArgumentException("Policy template not found"));

    return mapper.toDto(savedEndpoint, policy);
  }

  @Override
  public EndpointDto updateEndpoint(EndpointUpdateCommand command) {
    MonitoredEndpoint endpoint =
        endpointRepository
            .findById(command.id())
            .orElseThrow(
                () -> new IllegalArgumentException("Endpoint not found with id: " + command.id()));

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

    return mapper.toDto(savedEndpoint, policy);
  }

  @Override
  public EndpointDto getEndpoint(Long id) {
    MonitoredEndpoint endpoint =
        endpointRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Endpoint not found with id: " + id));
    CheckPolicy policy = checkPolicyRepository.findById(endpoint.getPolicyId()).orElse(null);
    return mapper.toDto(endpoint, policy);
  }

  @Override
  public List<EndpointDto> getAllEndpoints() {
    return endpointRepository.findAll().stream()
        .map(
            endpoint -> {
              CheckPolicy policy =
                  checkPolicyRepository.findById(endpoint.getPolicyId()).orElse(null);
              return mapper.toDto(endpoint, policy);
            })
        .collect(Collectors.toList());
  }

  @Override
  public void deleteEndpoint(Long id) {
    endpointRepository.deleteById(id);
  }
}
