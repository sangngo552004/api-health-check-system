package com.example.apihealthchecksystem.application.port.in;

import com.example.apihealthchecksystem.application.dto.request.WorkspaceCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.WorkspaceUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceMemberDto;
import java.util.List;

public interface ManageWorkspaceUseCase {
  WorkspaceDto createWorkspace(WorkspaceCreateCommand command, Long userId);

  WorkspaceDto updateWorkspace(WorkspaceUpdateCommand command);

  WorkspaceDto getWorkspace(Long id);

  List<WorkspaceDto> getMyWorkspaces(Long userId);

  void deleteWorkspace(Long id);

  void addMember(Long workspaceId, Long userId);

  void removeMember(Long workspaceId, Long userId);

  List<WorkspaceMemberDto> getMembers(Long workspaceId);
}
