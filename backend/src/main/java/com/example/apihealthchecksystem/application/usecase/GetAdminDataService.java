package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.response.AdminUserDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDto;
import com.example.apihealthchecksystem.application.port.in.GetAdminDataUseCase;
import com.example.apihealthchecksystem.application.port.out.UserRepository;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import java.util.List;

public class GetAdminDataService implements GetAdminDataUseCase {
  private final UserRepository userRepository;
  private final WorkspaceRepository workspaceRepository;

  public GetAdminDataService(
      UserRepository userRepository, WorkspaceRepository workspaceRepository) {
    this.userRepository = userRepository;
    this.workspaceRepository = workspaceRepository;
  }

  @Override
  public List<AdminUserDto> getUsers() {
    return userRepository.findAll().stream()
        .map(
            user ->
                new AdminUserDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getRole().name(),
                    user.getIsActive()))
        .toList();
  }

  @Override
  public List<WorkspaceDto> getWorkspaces() {
    return workspaceRepository.findAll().stream()
        .map(
            workspace ->
                new WorkspaceDto(
                    workspace.getId(),
                    workspace.getName(),
                    workspace.getDescription(),
                    workspace.getSlug(),
                    workspace.getOwnerId(),
                    workspace.getIsActive(),
                    workspace.getCreatedAt()))
        .toList();
  }
}
