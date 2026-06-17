package com.example.apihealthchecksystem.delivery.rest;

import com.example.apihealthchecksystem.application.dto.request.AdminUserCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AdminUserUpdateCommand;
import com.example.apihealthchecksystem.application.dto.request.AdminWorkspaceCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AdminWorkspaceUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.AdminUserDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDto;
import com.example.apihealthchecksystem.application.port.in.ManageAdminUseCase;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {
  private final ManageAdminUseCase adminUseCase;

  @GetMapping("/users")
  public ApiResponse<PagedResponseDto<AdminUserDto>> getUsers(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String role,
      @RequestParam(required = false) Boolean isActive,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {
    return ApiResponse.success(
        adminUseCase.getUsers(search, role, isActive, page, size, sortBy, sortDir));
  }

  @GetMapping("/users/{id}")
  public ApiResponse<AdminUserDto> getUserById(@PathVariable Long id) {
    return ApiResponse.success(adminUseCase.getUserById(id));
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<AdminUserDto> createUser(@Valid @RequestBody AdminUserCreateCommand command) {
    return ApiResponse.success(adminUseCase.createUser(command));
  }

  @PutMapping("/users/{id}")
  public ApiResponse<AdminUserDto> updateUser(
      @PathVariable Long id, @Valid @RequestBody AdminUserUpdateCommand command) {
    return ApiResponse.success(adminUseCase.updateUser(id, command));
  }

  @DeleteMapping("/users/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable Long id) {
    adminUseCase.deleteUser(id);
  }

  @GetMapping("/workspaces")
  public ApiResponse<PagedResponseDto<WorkspaceDto>> getWorkspaces(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) Boolean isActive,
      @RequestParam(required = false) Long ownerId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir) {
    return ApiResponse.success(
        adminUseCase.getWorkspaces(search, isActive, ownerId, page, size, sortBy, sortDir));
  }

  @GetMapping("/workspaces/{id}")
  public ApiResponse<WorkspaceDto> getWorkspaceById(@PathVariable Long id) {
    return ApiResponse.success(adminUseCase.getWorkspaceById(id));
  }

  @PostMapping("/workspaces")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<WorkspaceDto> createWorkspace(
      @Valid @RequestBody AdminWorkspaceCreateCommand command) {
    return ApiResponse.success(adminUseCase.createWorkspace(command));
  }

  @PutMapping("/workspaces/{id}")
  public ApiResponse<WorkspaceDto> updateWorkspace(
      @PathVariable Long id, @Valid @RequestBody AdminWorkspaceUpdateCommand command) {
    return ApiResponse.success(adminUseCase.updateWorkspace(id, command));
  }

  @DeleteMapping("/workspaces/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteWorkspace(@PathVariable Long id) {
    adminUseCase.deleteWorkspace(id);
  }
}
