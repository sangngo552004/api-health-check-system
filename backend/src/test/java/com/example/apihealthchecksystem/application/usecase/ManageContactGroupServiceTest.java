package com.example.apihealthchecksystem.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.apihealthchecksystem.application.dto.request.ContactGroupCreateCommand;
import com.example.apihealthchecksystem.application.dto.response.ContactGroupDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.mapper.ContactGroupDtoMapper;
import com.example.apihealthchecksystem.application.port.out.ContactGroupRepository;
import com.example.apihealthchecksystem.domain.model.ContactGroup;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManageContactGroupServiceTest {

  @Mock private ContactGroupRepository repository;
  @Mock private ContactGroupDtoMapper mapper;

  @InjectMocks private ManageContactGroupService service;

  @Test
  void createContactGroup_shouldSaveAndReturnDto() {
    Long workspaceId = 1L;
    ContactGroupCreateCommand command =
        new ContactGroupCreateCommand("Devs", "Desc", List.of(1L), List.of("a@b.com"), List.of());
    ContactGroup group = ContactGroup.builder().name("Devs").workspaceId(workspaceId).build();
    ContactGroupDto dto =
        new ContactGroupDto(
            1L, "Devs", "Desc", workspaceId, true, List.of(1L), List.of("a@b.com"), List.of());

    when(mapper.toDomain(command)).thenReturn(group);
    when(repository.save(any())).thenReturn(group);
    when(mapper.toDto(group)).thenReturn(dto);

    ContactGroupDto result = service.createContactGroup(workspaceId, command);

    assertNotNull(result);
    assertEquals(workspaceId, result.workspaceId());
  }

  @Test
  void getContactGroupsByWorkspace_shouldReturnPagedResponse() {
    Long workspaceId = 1L;
    int page = 0;
    int size = 10;
    ContactGroup g = ContactGroup.builder().id(1L).workspaceId(workspaceId).build();
    ContactGroupDto d =
        new ContactGroupDto(1L, "G", "D", workspaceId, true, List.of(), List.of(), List.of());

    when(repository.findByWorkspaceId(workspaceId, page, size)).thenReturn(List.of(g));
    when(repository.countByWorkspaceId(workspaceId)).thenReturn(1L);
    when(mapper.toDto(g)).thenReturn(d);

    PagedResponseDto<ContactGroupDto> result =
        service.getContactGroupsByWorkspace(workspaceId, page, size);

    assertEquals(1, result.items().size());
  }

  @Test
  void updateContactGroup_shouldSaveAndReturnDto() {
    Long id = 1L;
    com.example.apihealthchecksystem.application.dto.request.ContactGroupUpdateCommand command =
        new com.example.apihealthchecksystem.application.dto.request.ContactGroupUpdateCommand(
            id, "New", "D", true, List.of(), List.of(), List.of());
    Long workspaceId = 1L;
    ContactGroup group = ContactGroup.builder().id(id).workspaceId(workspaceId).build();
    ContactGroupDto dto =
        new ContactGroupDto(id, "New", "D", workspaceId, true, List.of(), List.of(), List.of());

    when(repository.findById(id)).thenReturn(Optional.of(group));
    when(repository.save(any())).thenReturn(group);
    when(mapper.toDto(group)).thenReturn(dto);

    ContactGroupDto result = service.updateContactGroup(workspaceId, command);
    assertNotNull(result);
    assertEquals("New", result.name());
  }

  @Test
  void deleteContactGroup_shouldCallRepository() {
    Long workspaceId = 1L;
    when(repository.findById(1L))
        .thenReturn(Optional.of(ContactGroup.builder().workspaceId(workspaceId).build()));
    service.deleteContactGroup(workspaceId, 1L);
    verify(repository).deleteById(1L);
  }
}
