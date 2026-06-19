package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.WorkspaceCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.WorkspaceUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceMemberDto;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.port.in.ManageWorkspaceUseCase;
import com.example.apihealthchecksystem.application.port.out.UserRepository;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.domain.model.User;
import com.example.apihealthchecksystem.domain.model.Workspace;
import com.example.apihealthchecksystem.domain.model.WorkspaceMember;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ManageWorkspaceService implements ManageWorkspaceUseCase {

  private final WorkspaceRepository workspaceRepository;
  private final UserRepository userRepository;

  @Override
  public WorkspaceDto createWorkspace(WorkspaceCreateCommand command, Long userId) {
    Workspace workspace =
        Workspace.builder()
            .name(command.name())
            .description(command.description())
            .slug(command.slug())
            .ownerId(userId)
            .isActive(true)
            .build();

    Workspace saved = workspaceRepository.save(workspace);
    workspaceRepository.addMember(saved.getId(), userId);

    return toDto(saved);
  }

  @Override
  public WorkspaceDto updateWorkspace(WorkspaceUpdateCommand command) {
    Workspace workspace = getWorkspaceById(command.id());

    workspace.setName(command.name());
    workspace.setDescription(command.description());
    if (command.isActive() != null) {
      workspace.setIsActive(command.isActive());
    }

    return toDto(workspaceRepository.save(workspace));
  }

  @Override
  public WorkspaceDto getWorkspace(Long id) {
    return toDto(getWorkspaceById(id));
  }

  @Override
  public List<WorkspaceDto> getMyWorkspaces(Long userId) {
    return workspaceRepository.findByUserId(userId).stream()
        .filter(workspace -> Boolean.TRUE.equals(workspace.getIsActive()))
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteWorkspace(Long id) {
    getWorkspaceById(id);
    workspaceRepository.deleteById(id);
  }

  @Override
  public void addMember(Long workspaceId, Long userId) {
    getWorkspaceById(workspaceId);
    workspaceRepository.addMember(workspaceId, userId);
  }

  @Override
  public void removeMember(Long workspaceId, Long userId) {
    getWorkspaceById(workspaceId);
    workspaceRepository.removeMember(workspaceId, userId);
  }

  @Override
  public List<WorkspaceMemberDto> getMembers(Long workspaceId) {
    getWorkspaceById(workspaceId);
    var members = workspaceRepository.getMembers(workspaceId);
    var userIds = members.stream().map(WorkspaceMember::getUserId).collect(Collectors.toList());

    Map<Long, User> userMap =
        userRepository.findAllByIds(userIds).stream()
            .collect(Collectors.toMap(User::getId, u -> u));

    return members.stream()
        .map(
            m -> {
              User user = userMap.get(m.getUserId());
              return new WorkspaceMemberDto(
                  m.getUserId(),
                  user != null ? user.getUsername() : "Unknown",
                  user != null ? user.getEmail() : "Unknown",
                  m.getJoinedAt());
            })
        .collect(Collectors.toList());
  }

  private Workspace getWorkspaceById(Long workspaceId) {
    return workspaceRepository
        .findById(workspaceId)
        .orElseThrow(
            () -> new ResourceNotFoundException(AppErrorCode.WORKSPACE_NOT_FOUND, workspaceId));
  }

  private WorkspaceDto toDto(Workspace workspace) {
    return new WorkspaceDto(
        workspace.getId(),
        workspace.getName(),
        workspace.getDescription(),
        workspace.getSlug(),
        workspace.getOwnerId(),
        workspace.getIsActive(),
        workspace.getCreatedAt());
  }
}
