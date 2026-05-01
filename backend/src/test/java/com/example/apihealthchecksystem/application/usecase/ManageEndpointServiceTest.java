package com.example.apihealthchecksystem.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.apihealthchecksystem.application.dto.EndpointCreateCommand;
import com.example.apihealthchecksystem.application.dto.EndpointDto;
import com.example.apihealthchecksystem.application.dto.EndpointUpdateCommand;
import com.example.apihealthchecksystem.application.mapper.EndpointDtoMapper;
import com.example.apihealthchecksystem.application.port.out.CheckPolicyRepository;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManageEndpointServiceTest {

  @Mock private EndpointRepository endpointRepository;
  @Mock private CheckPolicyRepository checkPolicyRepository;
  @Mock private EndpointDtoMapper mapper;

  @InjectMocks private ManageEndpointService manageEndpointService;

  @Test
  void createEndpoint_shouldSaveEndpointAndReturnDto() {
    Long policyId = 10L;
    EndpointCreateCommand command =
        new EndpointCreateCommand(
            "Test API",
            "http://test.com",
            HttpMethod.GET,
            "DEV",
            CheckType.HTTP,
            policyId,
            List.of(1L),
            List.of("test"),
            Map.of("Auth", "Bearer abc"),
            "{}");

    MonitoredEndpoint mockEndpoint =
        MonitoredEndpoint.builder().id(1L).name("Test API").policyId(policyId).build();
    CheckPolicy mockPolicy = CheckPolicy.builder().id(policyId).expectedStatusCode(200).build();
    EndpointDto mockDto =
        new EndpointDto(
            1L,
            "Test API",
            "http://test.com",
            HttpMethod.GET,
            "DEV",
            CheckType.HTTP,
            200,
            true,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Map.of("Auth", "Bearer abc"),
            "{}",
            60,
            5000,
            3,
            3,
            2000);

    when(mapper.toDomain(command)).thenReturn(mockEndpoint);
    when(endpointRepository.save(any())).thenReturn(mockEndpoint);
    when(checkPolicyRepository.findById(policyId)).thenReturn(Optional.of(mockPolicy));
    when(mapper.toDto(mockEndpoint, mockPolicy)).thenReturn(mockDto);

    EndpointDto result = manageEndpointService.createEndpoint(command);

    assertNotNull(result);
    verify(endpointRepository).save(any());
    assertEquals("Bearer abc", result.headers().get("Auth"));
  }

  @Test
  void updateEndpoint_shouldUpdateFieldsAndReturnDto() {
    Long endpointId = 1L;
    Long policyId = 10L;
    EndpointUpdateCommand command =
        new EndpointUpdateCommand(
            endpointId,
            "Updated",
            "http://up.com",
            HttpMethod.POST,
            "PROD",
            CheckType.HTTP,
            true,
            policyId,
            List.of(1L),
            List.of("tag"),
            Map.of("X-Header", "Val"),
            "payload");

    MonitoredEndpoint existing = MonitoredEndpoint.builder().id(endpointId).build();
    CheckPolicy mockPolicy = CheckPolicy.builder().id(policyId).build();
    EndpointDto updatedDto =
        new EndpointDto(
            endpointId,
            "Updated",
            "http://up.com",
            HttpMethod.POST,
            "PROD",
            CheckType.HTTP,
            200,
            true,
            null,
            null,
            Map.of("X-Header", "Val"),
            "payload",
            60,
            5000,
            3,
            3,
            2000);

    when(endpointRepository.findById(endpointId)).thenReturn(Optional.of(existing));
    when(endpointRepository.save(any())).thenReturn(existing);
    when(checkPolicyRepository.findById(policyId)).thenReturn(Optional.of(mockPolicy));
    when(mapper.toDto(any(), any())).thenReturn(updatedDto);

    EndpointDto result = manageEndpointService.updateEndpoint(command);

    assertNotNull(result);
    assertEquals("payload", existing.getRequestBody());
    assertEquals("Val", existing.getHeaders().get("X-Header"));
  }
}
