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
    endpoint.setCreatedAt(LocalDateTime.now());
    endpoint.setUpdatedAt(LocalDateTime.now());

    MonitoredEndpoint savedEndpoint = endpointRepository.save(endpoint);

    CheckPolicy policy = mapper.toPolicyDomain(command);
    policy.setEndpointId(savedEndpoint.getId());

    CheckPolicy savedPolicy = checkPolicyRepository.save(policy);
    savedEndpoint.setPolicy(savedPolicy);

    return mapper.toDto(savedEndpoint, savedPolicy);
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
    endpoint.setExpectedStatusCode(command.expectedStatusCode());
    if (command.isActive() != null) {
      endpoint.setIsActive(command.isActive());
    }
    endpoint.setUpdatedAt(LocalDateTime.now());

    MonitoredEndpoint savedEndpoint = endpointRepository.save(endpoint);

    CheckPolicy policy =
        checkPolicyRepository
            .findByEndpointId(command.id())
            .orElseThrow(
                () ->
                    new IllegalArgumentException("Policy not found for endpoint: " + command.id()));

    policy.setIntervalSeconds(command.intervalSeconds());
    policy.setTimeoutMillis(command.timeoutMillis());
    policy.setRetryCount(command.retryCount());
    policy.setFailureThreshold(command.failureThreshold());
    policy.setLatencyThresholdMillis(command.latencyThresholdMillis());

    CheckPolicy savedPolicy = checkPolicyRepository.save(policy);
    savedEndpoint.setPolicy(savedPolicy);

    return mapper.toDto(savedEndpoint, savedPolicy);
  }

  @Override
  public EndpointDto getEndpoint(Long id) {
    MonitoredEndpoint endpoint =
        endpointRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Endpoint not found with id: " + id));
    CheckPolicy policy = checkPolicyRepository.findByEndpointId(id).orElse(null);
    return mapper.toDto(endpoint, policy);
  }

  @Override
  public List<EndpointDto> getAllEndpoints() {
    return endpointRepository.findAll().stream()
        .map(
            endpoint -> {
              CheckPolicy policy =
                  checkPolicyRepository.findByEndpointId(endpoint.getId()).orElse(null);
              return mapper.toDto(endpoint, policy);
            })
        .collect(Collectors.toList());
  }

  @Override
  public void deleteEndpoint(Long id) {
    checkPolicyRepository.deleteByEndpointId(id);
    endpointRepository.deleteById(id);
  }
}
