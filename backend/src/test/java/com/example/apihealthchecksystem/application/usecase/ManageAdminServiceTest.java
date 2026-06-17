package com.example.apihealthchecksystem.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.apihealthchecksystem.application.dto.request.AdminUserCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AdminUserUpdateCommand;
import com.example.apihealthchecksystem.application.dto.request.AdminWorkspaceCreateCommand;
import com.example.apihealthchecksystem.application.dto.response.PageResult;
import com.example.apihealthchecksystem.application.exception.AppException;
import com.example.apihealthchecksystem.application.port.out.UserRepository;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.domain.model.User;
import com.example.apihealthchecksystem.domain.model.Workspace;
import com.example.apihealthchecksystem.domain.valueobject.UserRole;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ManageAdminServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private WorkspaceRepository workspaceRepository;
  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private ManageAdminService service;

  @Test
  void getUsers_shouldReturnPagedResponse() {
    User user =
        User.builder()
            .id(1L)
            .username("admin")
            .email("admin@healthcheck.com")
            .role(UserRole.SUPER_ADMIN)
            .isActive(true)
            .build();
    when(userRepository.search(null, null, null, 0, 10, "createdAt", "desc"))
        .thenReturn(new PageResult<>(List.of(user), 1));

    var result = service.getUsers(null, null, null, 0, 10, "createdAt", "desc");

    assertEquals(1, result.items().size());
    assertEquals("admin", result.items().get(0).username());
    assertEquals(1, result.totalItems());
  }

  @Test
  void createUser_shouldEncodePasswordAndSave() {
    AdminUserCreateCommand command =
        new AdminUserCreateCommand(
            "alice", "alice@example.com", "0900000000", "secret123", UserRole.USER, true, false);
    when(passwordEncoder.encode("secret123")).thenReturn("encoded-secret");
    when(userRepository.save(any()))
        .thenAnswer(
            invocation -> {
              User user = invocation.getArgument(0);
              user.setId(10L);
              return user;
            });

    var result = service.createUser(command);

    assertEquals(10L, result.id());
    verify(passwordEncoder).encode("secret123");
    verify(userRepository).save(any());
  }

  @Test
  void updateUser_shouldRejectDuplicatedEmail() {
    AdminUserUpdateCommand command =
        new AdminUserUpdateCommand(
            "alice", "existing@example.com", "0900000000", null, UserRole.USER, true, false);
    when(userRepository.findById(10L))
        .thenReturn(
            Optional.of(User.builder().id(10L).username("alice").role(UserRole.USER).build()));
    when(userRepository.findByUsername("alice"))
        .thenReturn(Optional.of(User.builder().id(10L).username("alice").build()));
    when(userRepository.findByEmail("existing@example.com"))
        .thenReturn(Optional.of(User.builder().id(99L).email("existing@example.com").build()));

    assertThrows(AppException.class, () -> service.updateUser(10L, command));
  }

  @Test
  void createWorkspace_shouldSaveAndAddOwnerAsMember() {
    AdminWorkspaceCreateCommand command =
        new AdminWorkspaceCreateCommand("Demo", "Workspace demo", "demo", 5L, true);
    when(userRepository.findById(5L))
        .thenReturn(
            Optional.of(User.builder().id(5L).username("viewer").role(UserRole.USER).build()));
    when(workspaceRepository.save(any()))
        .thenAnswer(
            invocation -> {
              Workspace workspace = invocation.getArgument(0);
              workspace.setId(20L);
              return workspace;
            });

    var result = service.createWorkspace(command);

    assertEquals(20L, result.id());
    verify(workspaceRepository).addMember(20L, 5L);
  }

  @Test
  void deleteUser_shouldRejectWhenUserOwnsWorkspace() {
    when(userRepository.findById(8L))
        .thenReturn(
            Optional.of(User.builder().id(8L).username("owner").role(UserRole.USER).build()));
    when(workspaceRepository.existsByOwnerId(8L)).thenReturn(true);

    assertThrows(AppException.class, () -> service.deleteUser(8L));
  }
}
