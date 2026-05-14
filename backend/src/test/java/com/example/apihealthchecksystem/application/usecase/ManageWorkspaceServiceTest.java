package com.example.apihealthchecksystem.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.apihealthchecksystem.application.dto.request.WorkspaceCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.WorkspaceUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDto;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.port.out.UserRepository;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.domain.model.Workspace;
import com.example.apihealthchecksystem.domain.valueobject.WorkspaceRole;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManageWorkspaceServiceTest {

  @Mock private WorkspaceRepository workspaceRepository;
  @Mock private UserRepository userRepository;

  @InjectMocks private ManageWorkspaceService service;

  @Test
  void createWorkspace_shouldSaveAndAddMember() {
    Long userId = 1L;
    WorkspaceCreateCommand command = new WorkspaceCreateCommand("Team A", "Desc", "team-a");
    Workspace savedWorkspace =
        Workspace.builder().id(10L).name("Team A").slug("team-a").ownerId(userId).build();

    when(workspaceRepository.save(any())).thenReturn(savedWorkspace);

    WorkspaceDto result = service.createWorkspace(command, userId);

    assertNotNull(result);
    assertEquals("Team A", result.name());
    verify(workspaceRepository).save(any());
    verify(workspaceRepository).addMember(10L, userId, WorkspaceRole.ADMIN);
  }

  @Test
  void getWorkspace_shouldReturnDto_whenFound() {
    Long id = 10L;
    Workspace workspace = Workspace.builder().id(id).name("Team A").build();
    when(workspaceRepository.findById(id)).thenReturn(Optional.of(workspace));

    WorkspaceDto result = service.getWorkspace(id);

    assertNotNull(result);
    assertEquals(id, result.id());
  }

  @Test
  void updateWorkspace_shouldSaveAndReturnDto() {
    WorkspaceUpdateCommand command = new WorkspaceUpdateCommand(10L, "New Name", "New Desc", true);
    Workspace workspace = Workspace.builder().id(10L).name("New Name").build();
    when(workspaceRepository.findById(10L)).thenReturn(Optional.of(workspace));
    when(workspaceRepository.save(any())).thenReturn(workspace);

    WorkspaceDto result = service.updateWorkspace(command);

    assertNotNull(result);
    assertEquals("New Name", result.name());
    verify(workspaceRepository).save(any());
  }

  @Test
  void getMyWorkspaces_shouldReturnList() {
    Long userId = 1L;
    when(workspaceRepository.findByUserId(userId))
        .thenReturn(java.util.List.of(Workspace.builder().id(1L).build()));

    java.util.List<WorkspaceDto> results = service.getMyWorkspaces(userId);

    assertEquals(1, results.size());
  }

  @Test
  void getWorkspace_shouldThrow_whenNotFound() {
    when(workspaceRepository.findById(10L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.getWorkspace(10L));
  }

  @Test
  void deleteWorkspace_shouldCallRepository() {
    Long id = 10L;
    when(workspaceRepository.findById(id))
        .thenReturn(Optional.of(Workspace.builder().id(id).build()));
    service.deleteWorkspace(id);
    verify(workspaceRepository).deleteById(id);
  }

  @Test
  void addMember_shouldCallRepository() {
    when(workspaceRepository.findById(10L))
        .thenReturn(Optional.of(Workspace.builder().id(10L).build()));
    service.addMember(10L, 2L, "MEMBER");
    verify(workspaceRepository).addMember(10L, 2L, WorkspaceRole.MEMBER);
  }

  @Test
  void removeMember_shouldCallRepository() {
    when(workspaceRepository.findById(10L))
        .thenReturn(Optional.of(Workspace.builder().id(10L).build()));
    service.removeMember(10L, 2L);
    verify(workspaceRepository).removeMember(10L, 2L);
  }

  @Test
  void getMembers_shouldReturnList() {
    Long id = 10L;
    when(workspaceRepository.findById(id))
        .thenReturn(Optional.of(Workspace.builder().id(id).build()));
    when(workspaceRepository.getMembers(id)).thenReturn(java.util.List.of());
    service.getMembers(id);
    verify(workspaceRepository).getMembers(id);
  }
}
