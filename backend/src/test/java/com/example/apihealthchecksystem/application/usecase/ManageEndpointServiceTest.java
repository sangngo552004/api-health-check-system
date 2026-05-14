package com.example.apihealthchecksystem.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.apihealthchecksystem.application.dto.request.EndpointCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.EndpointUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.EndpointDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
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
    Long workspaceId = 1L;
    Long currentUserId = 99L;
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
        MonitoredEndpoint.builder()
            .id(1L)
            .name("Test API")
            .policyId(policyId)
            .workspaceId(workspaceId)
            .build();
    CheckPolicy mockPolicy =
        CheckPolicy.builder().id(policyId).expectedStatusCode(200).workspaceId(workspaceId).build();
    EndpointDto mockDto =
        new EndpointDto(
            1L,
            "Test API",
            "http://test.com",
            HttpMethod.GET,
            "DEV",
            CheckType.HTTP,
            workspaceId,
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

    EndpointDto result = manageEndpointService.createEndpoint(workspaceId, currentUserId, command);

    assertNotNull(result);
    assertEquals(workspaceId, result.workspaceId());
    verify(endpointRepository)
        .save(argThat(endpoint -> currentUserId.equals(endpoint.getCreatedBy())));
  }

  @Test
  void getEndpointsByWorkspace_shouldReturnPagedResponse() {
    Long workspaceId = 1L;
    int page = 0;
    int size = 10;
    MonitoredEndpoint e =
        MonitoredEndpoint.builder().id(1L).policyId(10L).workspaceId(workspaceId).build();
    CheckPolicy p = CheckPolicy.builder().id(10L).build();
    EndpointDto d =
        new EndpointDto(
            1L,
            "N",
            "U",
            HttpMethod.GET,
            "E",
            CheckType.HTTP,
            workspaceId,
            200,
            true,
            null,
            null,
            null,
            null,
            60,
            5000,
            3,
            3,
            2000);

    when(endpointRepository.findByWorkspaceId(workspaceId, page, size)).thenReturn(List.of(e));
    when(endpointRepository.countByWorkspaceId(workspaceId)).thenReturn(1L);
    when(checkPolicyRepository.findById(10L)).thenReturn(Optional.of(p));
    when(mapper.toDto(e, p)).thenReturn(d);

    PagedResponseDto<EndpointDto> result =
        manageEndpointService.getEndpointsByWorkspace(workspaceId, page, size);

    assertEquals(1, result.items().size());
    assertEquals(1, result.totalItems());
    assertEquals(workspaceId, result.items().get(0).workspaceId());
  }

  @Test
  void getEndpoint_shouldReturnDto_whenFound() {
    Long id = 1L;
    Long workspaceId = 1L;
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder().id(id).policyId(10L).workspaceId(workspaceId).build();
    CheckPolicy policy = CheckPolicy.builder().id(10L).workspaceId(workspaceId).build();
    EndpointDto dto =
        new EndpointDto(
            id,
            "N",
            "U",
            HttpMethod.GET,
            "E",
            CheckType.HTTP,
            workspaceId,
            200,
            true,
            null,
            null,
            null,
            null,
            60,
            5000,
            3,
            3,
            2000);

    when(endpointRepository.findById(id)).thenReturn(Optional.of(endpoint));
    when(checkPolicyRepository.findById(10L)).thenReturn(Optional.of(policy));
    when(mapper.toDto(endpoint, policy)).thenReturn(dto);

    EndpointDto result = manageEndpointService.getEndpoint(workspaceId, id);

    assertNotNull(result);
    verify(endpointRepository).findById(id);
  }

  @Test
  void getEndpoint_shouldThrowException_whenNotFound() {
    when(endpointRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> manageEndpointService.getEndpoint(1L, 1L));
  }

  @Test
  void deleteEndpoint_shouldCallRepository() {
    Long workspaceId = 1L;
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder().id(1L).workspaceId(workspaceId).build();
    when(endpointRepository.findById(1L)).thenReturn(Optional.of(endpoint));
    manageEndpointService.deleteEndpoint(workspaceId, 1L);
    verify(endpointRepository).deleteById(1L);
  }

  @Test
  void updateEndpoint_shouldSaveAndReturnDto() {
    Long id = 1L;
    Long policyId = 10L;
    Long workspaceId = 1L;
    EndpointUpdateCommand command =
        new EndpointUpdateCommand(
            id,
            "New",
            "http://test.com",
            HttpMethod.POST,
            "E",
            CheckType.HTTP,
            true,
            policyId,
            List.of(),
            List.of(),
            Map.of(),
            "{}");
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder().id(id).policyId(policyId).workspaceId(workspaceId).build();
    CheckPolicy policy = CheckPolicy.builder().id(policyId).workspaceId(workspaceId).build();
    EndpointDto dto =
        new EndpointDto(
            id,
            "New",
            "U",
            HttpMethod.POST,
            "E",
            CheckType.HTTP,
            workspaceId,
            200,
            true,
            null,
            null,
            null,
            null,
            60,
            5000,
            3,
            3,
            2000);

    when(endpointRepository.findById(id)).thenReturn(Optional.of(endpoint));
    when(endpointRepository.save(any())).thenReturn(endpoint);
    when(checkPolicyRepository.findById(policyId)).thenReturn(Optional.of(policy));
    when(mapper.toDto(any(), any())).thenReturn(dto);

    EndpointDto result = manageEndpointService.updateEndpoint(workspaceId, command);

    assertNotNull(result);
    assertEquals("New", result.name());
  }
}
