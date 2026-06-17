package com.example.apihealthchecksystem.delivery.rest;

import com.example.apihealthchecksystem.application.dto.request.WorkspaceCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.WorkspaceUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceMemberDto;
import com.example.apihealthchecksystem.application.port.in.ManageWorkspaceUseCase;
import com.example.apihealthchecksystem.delivery.rest.common.ApiResponse;
import com.example.apihealthchecksystem.delivery.rest.common.security.CurrentUserId;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

  private final ManageWorkspaceUseCase workspaceUseCase;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ApiResponse<WorkspaceDto> createWorkspace(
      @Valid @RequestBody WorkspaceCreateCommand command, @CurrentUserId Long userId) {
    return ApiResponse.success(workspaceUseCase.createWorkspace(command, userId));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ApiResponse<WorkspaceDto> updateWorkspace(
      @PathVariable Long id, @Valid @RequestBody WorkspaceUpdateCommand command) {
    WorkspaceUpdateCommand withId =
        new WorkspaceUpdateCommand(id, command.name(), command.description(), command.isActive());
    return ApiResponse.success(workspaceUseCase.updateWorkspace(withId));
  }

  @GetMapping("/{id}")
  @PreAuthorize("@workspaceSecurity.canAccessWorkspaceArea(#id, authentication.principal.id)")
  public ApiResponse<WorkspaceDto> getWorkspace(@PathVariable Long id) {
    return ApiResponse.success(workspaceUseCase.getWorkspace(id));
  }

  @GetMapping("/my")
  public ApiResponse<List<WorkspaceDto>> getMyWorkspaces(@CurrentUserId Long userId) {
    return ApiResponse.success(workspaceUseCase.getMyWorkspaces(userId));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public void deleteWorkspace(@PathVariable Long id) {
    workspaceUseCase.deleteWorkspace(id);
  }

  @PostMapping("/{id}/members")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public void addMember(@PathVariable Long id, @RequestParam Long userId) {
    workspaceUseCase.addMember(id, userId);
  }

  @DeleteMapping("/{id}/members/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public void removeMember(@PathVariable Long id, @PathVariable Long userId) {
    workspaceUseCase.removeMember(id, userId);
  }

  @GetMapping("/{id}/members")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ApiResponse<List<WorkspaceMemberDto>> getMembers(@PathVariable Long id) {
    return ApiResponse.success(workspaceUseCase.getMembers(id));
  }
}
