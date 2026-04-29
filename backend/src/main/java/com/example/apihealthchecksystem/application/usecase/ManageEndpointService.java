package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.EndpointCreateCommand;
import com.example.apihealthchecksystem.application.dto.EndpointDto;
import com.example.apihealthchecksystem.application.dto.EndpointUpdateCommand;
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

  @Override
  public EndpointDto createEndpoint(EndpointCreateCommand command) {
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder()
            .name(command.name())
            .url(command.url())
            .method(command.method())
            .environment(command.environment())
            .checkType(command.checkType())
            .expectedStatusCode(
                command.expectedStatusCode() != null ? command.expectedStatusCode() : 200)
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    MonitoredEndpoint savedEndpoint = endpointRepository.save(endpoint);

    CheckPolicy policy =
        CheckPolicy.builder()
            .endpointId(savedEndpoint.getId())
            .intervalSeconds(command.intervalSeconds() != null ? command.intervalSeconds() : 60)
            .timeoutMillis(command.timeoutMillis() != null ? command.timeoutMillis() : 5000)
            .retryCount(command.retryCount() != null ? command.retryCount() : 3)
            .failureThreshold(command.failureThreshold() != null ? command.failureThreshold() : 3)
            .latencyThresholdMillis(
                command.latencyThresholdMillis() != null ? command.latencyThresholdMillis() : 2000)
            .build();

    CheckPolicy savedPolicy = checkPolicyRepository.save(policy);
    savedEndpoint.setPolicy(savedPolicy);

    return mapToDto(savedEndpoint, savedPolicy);
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

    return mapToDto(savedEndpoint, savedPolicy);
  }

  @Override
  public EndpointDto getEndpoint(Long id) {
    MonitoredEndpoint endpoint =
        endpointRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Endpoint not found with id: " + id));
    CheckPolicy policy = checkPolicyRepository.findByEndpointId(id).orElse(null);
    return mapToDto(endpoint, policy);
  }

  @Override
  public List<EndpointDto> getAllEndpoints() {
    return endpointRepository.findAll().stream()
        .map(
            endpoint -> {
              CheckPolicy policy =
                  checkPolicyRepository.findByEndpointId(endpoint.getId()).orElse(null);
              return mapToDto(endpoint, policy);
            })
        .collect(Collectors.toList());
  }

  @Override
  public void deleteEndpoint(Long id) {
    checkPolicyRepository.deleteByEndpointId(id);
    endpointRepository.deleteById(id);
  }

  private EndpointDto mapToDto(MonitoredEndpoint endpoint, CheckPolicy policy) {
    return new EndpointDto(
        endpoint.getId(),
        endpoint.getName(),
        endpoint.getUrl(),
        endpoint.getMethod(),
        endpoint.getEnvironment(),
        endpoint.getCheckType(),
        endpoint.getExpectedStatusCode(),
        endpoint.getIsActive(),
        endpoint.getCreatedAt(),
        endpoint.getUpdatedAt(),
        policy != null ? policy.getIntervalSeconds() : null,
        policy != null ? policy.getTimeoutMillis() : null,
        policy != null ? policy.getRetryCount() : null,
        policy != null ? policy.getFailureThreshold() : null,
        policy != null ? policy.getLatencyThresholdMillis() : null);
  }
}
