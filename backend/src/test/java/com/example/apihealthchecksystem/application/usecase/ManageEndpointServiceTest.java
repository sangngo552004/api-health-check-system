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
  void createEndpoint_shouldSaveEndpointAndPolicy() {
    EndpointCreateCommand command =
        new EndpointCreateCommand(
            "Test API",
            "http://test.com",
            HttpMethod.GET,
            "DEV",
            CheckType.HTTP,
            200,
            60,
            5000,
            3,
            3,
            2000);

    MonitoredEndpoint mockEndpoint =
        MonitoredEndpoint.builder()
            .id(1L)
            .name("Test API")
            .url("http://test.com")
            .method(HttpMethod.GET)
            .environment("DEV")
            .checkType(CheckType.HTTP)
            .expectedStatusCode(200)
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    CheckPolicy mockPolicy =
        CheckPolicy.builder()
            .id(10L)
            .endpointId(1L)
            .intervalSeconds(60)
            .timeoutMillis(5000)
            .retryCount(3)
            .failureThreshold(3)
            .latencyThresholdMillis(2000)
            .build();

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
            null,
            null,
            60,
            5000,
            3,
            3,
            2000);

    when(mapper.toDomain(any(EndpointCreateCommand.class))).thenReturn(mockEndpoint);
    when(mapper.toPolicyDomain(any(EndpointCreateCommand.class))).thenReturn(mockPolicy);
    when(endpointRepository.save(any(MonitoredEndpoint.class))).thenReturn(mockEndpoint);
    when(checkPolicyRepository.save(any(CheckPolicy.class))).thenReturn(mockPolicy);
    when(mapper.toDto(any(MonitoredEndpoint.class), any(CheckPolicy.class))).thenReturn(mockDto);

    EndpointDto result = manageEndpointService.createEndpoint(command);

    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("Test API", result.name());
    assertEquals(60, result.intervalSeconds());

    verify(endpointRepository, times(1)).save(any(MonitoredEndpoint.class));
    verify(checkPolicyRepository, times(1)).save(any(CheckPolicy.class));
  }

  @Test
  void updateEndpoint_shouldUpdateAndReturnDto() {
    Long endpointId = 1L;
    EndpointUpdateCommand command =
        new EndpointUpdateCommand(
            endpointId,
            "Updated Name",
            "http://updated.com",
            HttpMethod.POST,
            "PROD",
            CheckType.HTTP,
            201,
            true,
            30,
            3000,
            2,
            5,
            1000);

    MonitoredEndpoint existingEndpoint = MonitoredEndpoint.builder().id(endpointId).build();
    CheckPolicy existingPolicy = CheckPolicy.builder().endpointId(endpointId).build();
    EndpointDto updatedDto =
        new EndpointDto(
            endpointId,
            "Updated Name",
            "http://updated.com",
            HttpMethod.POST,
            "PROD",
            CheckType.HTTP,
            201,
            true,
            null,
            null,
            30,
            3000,
            2,
            5,
            1000);

    when(endpointRepository.findById(endpointId)).thenReturn(Optional.of(existingEndpoint));
    when(checkPolicyRepository.findByEndpointId(endpointId))
        .thenReturn(Optional.of(existingPolicy));
    when(endpointRepository.save(any())).thenReturn(existingEndpoint);
    when(checkPolicyRepository.save(any())).thenReturn(existingPolicy);
    when(mapper.toDto(any(), any())).thenReturn(updatedDto);

    EndpointDto result = manageEndpointService.updateEndpoint(command);

    assertNotNull(result);
    assertEquals("Updated Name", result.name());
    verify(endpointRepository).save(existingEndpoint);
  }

  @Test
  void updateEndpoint_shouldThrowException_whenNotFound() {
    when(endpointRepository.findById(1L)).thenReturn(Optional.empty());
    EndpointUpdateCommand command =
        new EndpointUpdateCommand(
            1L,
            "Name",
            "url",
            HttpMethod.GET,
            "DEV",
            CheckType.HTTP,
            200,
            true,
            60,
            5000,
            3,
            3,
            2000);

    assertThrows(
        IllegalArgumentException.class, () -> manageEndpointService.updateEndpoint(command));
  }

  @Test
  void getEndpoint_shouldReturnDto() {
    Long id = 1L;
    MonitoredEndpoint endpoint = MonitoredEndpoint.builder().id(id).build();
    CheckPolicy policy = CheckPolicy.builder().endpointId(id).build();
    EndpointDto dto =
        new EndpointDto(
            id,
            "Test",
            "url",
            HttpMethod.GET,
            "DEV",
            CheckType.HTTP,
            200,
            true,
            null,
            null,
            60,
            5000,
            3,
            3,
            2000);

    when(endpointRepository.findById(id)).thenReturn(Optional.of(endpoint));
    when(checkPolicyRepository.findByEndpointId(id)).thenReturn(Optional.of(policy));
    when(mapper.toDto(endpoint, policy)).thenReturn(dto);

    EndpointDto result = manageEndpointService.getEndpoint(id);

    assertNotNull(result);
    assertEquals(id, result.id());
  }

  @Test
  void getAllEndpoints_shouldReturnList() {
    MonitoredEndpoint endpoint = MonitoredEndpoint.builder().id(1L).build();
    CheckPolicy policy = CheckPolicy.builder().endpointId(1L).build();
    EndpointDto dto =
        new EndpointDto(
            1L,
            "Test",
            "url",
            HttpMethod.GET,
            "DEV",
            CheckType.HTTP,
            200,
            true,
            null,
            null,
            60,
            5000,
            3,
            3,
            2000);

    when(endpointRepository.findAll()).thenReturn(List.of(endpoint));
    when(checkPolicyRepository.findByEndpointId(1L)).thenReturn(Optional.of(policy));
    when(mapper.toDto(endpoint, policy)).thenReturn(dto);

    List<EndpointDto> result = manageEndpointService.getAllEndpoints();

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  void deleteEndpoint_shouldCallRepositories() {
    Long id = 1L;
    doNothing().when(checkPolicyRepository).deleteByEndpointId(id);
    doNothing().when(endpointRepository).deleteById(id);

    manageEndpointService.deleteEndpoint(id);

    verify(checkPolicyRepository).deleteByEndpointId(id);
    verify(endpointRepository).deleteById(id);
  }
}
