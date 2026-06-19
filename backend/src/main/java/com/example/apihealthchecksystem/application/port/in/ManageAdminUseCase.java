package com.example.apihealthchecksystem.application.port.in;

import com.example.apihealthchecksystem.application.dto.request.AdminUserCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AdminUserUpdateCommand;
import com.example.apihealthchecksystem.application.dto.request.AdminWorkspaceCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AdminWorkspaceUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.AdminUserDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceMemberDto;
import java.util.List;

public interface ManageAdminUseCase {
  PagedResponseDto<AdminUserDto> getUsers(
      String search,
      String role,
      Boolean isActive,
      int page,
      int size,
      String sortBy,
      String sortDir);

  AdminUserDto getUserById(Long id);

  AdminUserDto createUser(AdminUserCreateCommand command);

  AdminUserDto updateUser(Long id, AdminUserUpdateCommand command);

  void deleteUser(Long id);

  PagedResponseDto<WorkspaceDto> getWorkspaces(
      String search,
      Boolean isActive,
      Long ownerId,
      int page,
      int size,
      String sortBy,
      String sortDir);

  WorkspaceDto getWorkspaceById(Long id);

  WorkspaceDto createWorkspace(AdminWorkspaceCreateCommand command, Long currentUserId);

  WorkspaceDto updateWorkspace(Long id, AdminWorkspaceUpdateCommand command);

  void deleteWorkspace(Long id);

  void addWorkspaceMember(Long workspaceId, Long userId);

  void removeWorkspaceMember(Long workspaceId, Long userId);

  List<WorkspaceMemberDto> getWorkspaceMembers(Long workspaceId);
}
