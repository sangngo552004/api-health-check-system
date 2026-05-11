package com.example.apihealthchecksystem.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.apihealthchecksystem.application.dto.request.CheckPolicyCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.CheckPolicyUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.CheckPolicyDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.mapper.CheckPolicyDtoMapper;
import com.example.apihealthchecksystem.application.port.out.CheckPolicyRepository;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManageCheckPolicyServiceTest {

  @Mock private CheckPolicyRepository repository;
  @Mock private CheckPolicyDtoMapper mapper;

  @InjectMocks private ManageCheckPolicyService service;

  @Test
  void createPolicy_shouldSaveAndReturnDto() {
    Long workspaceId = 1L;
    CheckPolicyCreateCommand command =
        new CheckPolicyCreateCommand("Standard", 60, 5000, 3, 3, 2000, 200, null, null);
    CheckPolicy policy = CheckPolicy.builder().name("Standard").workspaceId(workspaceId).build();
    CheckPolicyDto dto =
        new CheckPolicyDto(1L, "Standard", 60, 5000, 3, 3, 2000, workspaceId, 200, null, null);

    when(mapper.toDomain(command)).thenReturn(policy);
    when(repository.save(any())).thenReturn(policy);
    when(mapper.toDto(policy)).thenReturn(dto);

    CheckPolicyDto result = service.createPolicy(workspaceId, command);

    assertNotNull(result);
    assertEquals("Standard", result.name());
    assertEquals(workspaceId, result.workspaceId());
  }

  @Test
  void getPoliciesByWorkspace_shouldReturnPagedResponse() {
    Long workspaceId = 1L;
    int page = 0;
    int size = 10;
    CheckPolicy p = CheckPolicy.builder().id(1L).workspaceId(workspaceId).build();
    CheckPolicyDto d =
        new CheckPolicyDto(1L, "P", 60, 5000, 3, 3, 2000, workspaceId, 200, null, null);

    when(repository.findByWorkspaceId(workspaceId, page, size)).thenReturn(List.of(p));
    when(repository.countByWorkspaceId(workspaceId)).thenReturn(1L);
    when(mapper.toDto(p)).thenReturn(d);

    PagedResponseDto<CheckPolicyDto> result =
        service.getPoliciesByWorkspace(workspaceId, page, size);

    assertEquals(1, result.items().size());
    assertEquals(1, result.totalItems());
  }

  @Test
  void updatePolicy_shouldUpdateAndReturnDto() {
    Long id = 1L;
    CheckPolicyUpdateCommand command =
        new CheckPolicyUpdateCommand(id, "Updated", 30, 2000, 2, 2, 500, 201, "OK", ".*");
    Long workspaceId = 1L;
    CheckPolicy existing =
        CheckPolicy.builder().id(id).name("Old").workspaceId(workspaceId).build();
    CheckPolicyDto updatedDto =
        new CheckPolicyDto(id, "Updated", 30, 2000, 2, 2, 500, workspaceId, 201, "OK", ".*");

    when(repository.findById(id)).thenReturn(Optional.of(existing));
    when(repository.save(any())).thenReturn(existing);
    when(mapper.toDto(existing)).thenReturn(updatedDto);

    CheckPolicyDto result = service.updatePolicy(workspaceId, command);

    assertNotNull(result);
    assertEquals("Updated", result.name());
    assertEquals(30, existing.getIntervalSeconds());
  }

  @Test
  void deletePolicy_shouldCallRepository() {
    Long workspaceId = 1L;
    when(repository.findById(1L))
        .thenReturn(Optional.of(CheckPolicy.builder().workspaceId(workspaceId).build()));
    service.deletePolicy(workspaceId, 1L);
    verify(repository).deleteById(1L);
  }

  @Test
  void getPolicy_shouldThrowException_whenNotFound() {
    Long workspaceId = 1L;
    when(repository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getPolicy(workspaceId, 1L));
  }

  @Test
  void updatePolicy_shouldThrowException_whenNotFound() {
    Long workspaceId = 1L;
    CheckPolicyUpdateCommand command =
        new CheckPolicyUpdateCommand(1L, "N", 60, 5000, 3, 3, 2000, 200, "OK", ".*");
    when(repository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.updatePolicy(workspaceId, command));
  }

  @Test
  void deletePolicy_shouldThrowException_whenNotFound() {
    Long workspaceId = 1L;
    when(repository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.deletePolicy(workspaceId, 1L));
  }
}
