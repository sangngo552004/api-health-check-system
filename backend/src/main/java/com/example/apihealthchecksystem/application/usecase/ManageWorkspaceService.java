package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.WorkspaceCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.WorkspaceUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceMemberDto;
import com.example.apihealthchecksystem.application.port.in.ManageWorkspaceUseCase;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.domain.model.Workspace;
import com.example.apihealthchecksystem.domain.valueobject.WorkspaceRole;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class ManageWorkspaceService implements ManageWorkspaceUseCase {

  private final WorkspaceRepository workspaceRepository;

  @Override
  @Transactional
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
    workspaceRepository.addMember(saved.getId(), userId, WorkspaceRole.ADMIN);

    return toDto(saved);
  }

  @Override
  @Transactional
  public WorkspaceDto updateWorkspace(WorkspaceUpdateCommand command) {
    Workspace workspace =
        workspaceRepository
            .findById(command.id())
            .orElseThrow(() -> new RuntimeException("Workspace not found"));

    workspace.setName(command.name());
    workspace.setDescription(command.description());
    if (command.isActive() != null) {
      workspace.setIsActive(command.isActive());
    }

    return toDto(workspaceRepository.save(workspace));
  }

  @Override
  @Transactional(readOnly = true)
  public WorkspaceDto getWorkspace(Long id) {
    return workspaceRepository
        .findById(id)
        .map(this::toDto)
        .orElseThrow(() -> new RuntimeException("Workspace not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<WorkspaceDto> getMyWorkspaces(Long userId) {
    return workspaceRepository.findByUserId(userId).stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteWorkspace(Long id) {
    workspaceRepository.deleteById(id);
  }

  @Override
  @Transactional
  public void addMember(Long workspaceId, Long userId, String role) {
    WorkspaceRole workspaceRole = WorkspaceRole.valueOf(role.toUpperCase());
    workspaceRepository.addMember(workspaceId, userId, workspaceRole);
  }

  @Override
  @Transactional
  public void removeMember(Long workspaceId, Long userId) {
    workspaceRepository.removeMember(workspaceId, userId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<WorkspaceMemberDto> getMembers(Long workspaceId) {
    return workspaceRepository.getMembers(workspaceId).stream()
        .map(
            m ->
                new WorkspaceMemberDto(
                    m.getUserId(), m.getUsername(), m.getEmail(), m.getRole(), m.getJoinedAt()))
        .collect(Collectors.toList());
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
