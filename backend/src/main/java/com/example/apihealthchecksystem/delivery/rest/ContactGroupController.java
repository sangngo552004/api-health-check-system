package com.example.apihealthchecksystem.delivery.rest;

import com.example.apihealthchecksystem.application.dto.request.ContactGroupCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.ContactGroupUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.ContactGroupDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.port.in.ManageContactGroupUseCase;
import com.example.apihealthchecksystem.delivery.rest.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contact-groups")
@RequiredArgsConstructor
public class ContactGroupController {

  private final ManageContactGroupUseCase useCase;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public ApiResponse<ContactGroupDto> create(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @Valid @RequestBody ContactGroupCreateCommand command) {
    return ApiResponse.success(useCase.createContactGroup(workspaceId, command));
  }

  @GetMapping
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public ApiResponse<PagedResponseDto<ContactGroupDto>> getByWorkspace(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.success(useCase.getContactGroupsByWorkspace(workspaceId, page, size));
  }

  @GetMapping("/{id}")
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public ApiResponse<ContactGroupDto> getById(
      @RequestHeader("X-Workspace-Id") Long workspaceId, @PathVariable Long id) {
    return ApiResponse.success(useCase.getContactGroup(workspaceId, id));
  }

  @PutMapping("/{id}")
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public ApiResponse<ContactGroupDto> update(
      @RequestHeader("X-Workspace-Id") Long workspaceId,
      @PathVariable Long id,
      @Valid @RequestBody ContactGroupUpdateCommand command) {
    ContactGroupUpdateCommand withId =
        new ContactGroupUpdateCommand(
            id,
            command.name(),
            command.description(),
            command.isActive(),
            command.userIds(),
            command.emailAddresses(),
            command.webhookUrls());
    return ApiResponse.success(useCase.updateContactGroup(workspaceId, withId));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "@workspaceSecurity.canAccessWorkspaceArea(#workspaceId, authentication.principal.id)")
  public void delete(@RequestHeader("X-Workspace-Id") Long workspaceId, @PathVariable Long id) {
    useCase.deleteContactGroup(workspaceId, id);
  }
}
