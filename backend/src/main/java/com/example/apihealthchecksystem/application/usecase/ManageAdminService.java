package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.AdminUserCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AdminUserUpdateCommand;
import com.example.apihealthchecksystem.application.dto.request.AdminWorkspaceCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AdminWorkspaceUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.AdminUserDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDto;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.AppException;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.port.in.ManageAdminUseCase;
import com.example.apihealthchecksystem.application.port.out.UserRepository;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.domain.model.User;
import com.example.apihealthchecksystem.domain.model.Workspace;
import com.example.apihealthchecksystem.domain.valueobject.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class ManageAdminService implements ManageAdminUseCase {
  private static final int DEFAULT_PAGE_SIZE = 10;
  private static final int MAX_PAGE_SIZE = 100;

  private final UserRepository userRepository;
  private final WorkspaceRepository workspaceRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public PagedResponseDto<AdminUserDto> getUsers(
      String search,
      String role,
      Boolean isActive,
      int page,
      int size,
      String sortBy,
      String sortDir) {
    int safePage = Math.max(page, 0);
    int safeSize = normalizeSize(size);
    var result =
        userRepository.search(
            search, parseUserRole(role), isActive, safePage, safeSize, sortBy, sortDir);
    return PagedResponseDto.of(
        result.items().stream().map(this::toAdminUserDto).toList(),
        safePage,
        safeSize,
        result.totalItems());
  }

  @Override
  public AdminUserDto getUserById(Long id) {
    return toAdminUserDto(getUser(id));
  }

  @Override
  public AdminUserDto createUser(AdminUserCreateCommand command) {
    validateUniqueUsername(command.username(), null);
    validateUniqueEmail(command.email(), null);

    User saved =
        userRepository.save(
            User.builder()
                .username(command.username().trim())
                .email(normalizeNullable(command.email()))
                .phoneNumber(normalizeNullable(command.phoneNumber()))
                .passwordHash(passwordEncoder.encode(command.password()))
                .role(command.role())
                .isActive(command.isActive() != null ? command.isActive() : Boolean.TRUE)
                .requiresPasswordChange(
                    command.requiresPasswordChange() != null
                        ? command.requiresPasswordChange()
                        : Boolean.FALSE)
                .build());
    return toAdminUserDto(saved);
  }

  @Override
  public AdminUserDto updateUser(Long id, AdminUserUpdateCommand command) {
    User existing = getUser(id);
    validateUniqueUsername(command.username(), id);
    validateUniqueEmail(command.email(), id);

    existing.setUsername(command.username().trim());
    existing.setEmail(normalizeNullable(command.email()));
    existing.setPhoneNumber(normalizeNullable(command.phoneNumber()));
    existing.setRole(command.role());
    existing.setIsActive(command.isActive() != null ? command.isActive() : existing.getIsActive());
    existing.setRequiresPasswordChange(
        command.requiresPasswordChange() != null
            ? command.requiresPasswordChange()
            : existing.getRequiresPasswordChange());
    if (command.password() != null && !command.password().isBlank()) {
      existing.setPasswordHash(passwordEncoder.encode(command.password()));
    }

    return toAdminUserDto(userRepository.save(existing));
  }

  @Override
  public void deleteUser(Long id) {
    getUser(id);
    if (workspaceRepository.existsByOwnerId(id)) {
      throw new AppException(
          AppErrorCode.VALIDATION_ERROR,
          "Không thể xóa user đang được gán làm owner của workspace.");
    }
    userRepository.deleteById(id);
  }

  @Override
  public PagedResponseDto<WorkspaceDto> getWorkspaces(
      String search,
      Boolean isActive,
      Long ownerId,
      int page,
      int size,
      String sortBy,
      String sortDir) {
    int safePage = Math.max(page, 0);
    int safeSize = normalizeSize(size);
    var result =
        workspaceRepository.search(search, isActive, ownerId, safePage, safeSize, sortBy, sortDir);
    return PagedResponseDto.of(
        result.items().stream().map(this::toWorkspaceDto).toList(),
        safePage,
        safeSize,
        result.totalItems());
  }

  @Override
  public WorkspaceDto getWorkspaceById(Long id) {
    return toWorkspaceDto(getWorkspace(id));
  }

  @Override
  public WorkspaceDto createWorkspace(AdminWorkspaceCreateCommand command) {
    validateUniqueSlug(command.slug(), null);
    getUser(command.ownerId());

    Workspace saved =
        workspaceRepository.save(
            Workspace.builder()
                .name(command.name().trim())
                .description(normalizeNullable(command.description()))
                .slug(command.slug().trim())
                .ownerId(command.ownerId())
                .isActive(command.isActive() != null ? command.isActive() : Boolean.TRUE)
                .build());
    workspaceRepository.addMember(saved.getId(), command.ownerId());
    return toWorkspaceDto(saved);
  }

  @Override
  public WorkspaceDto updateWorkspace(Long id, AdminWorkspaceUpdateCommand command) {
    Workspace existing = getWorkspace(id);
    validateUniqueSlug(command.slug(), id);
    getUser(command.ownerId());

    existing.setName(command.name().trim());
    existing.setDescription(normalizeNullable(command.description()));
    existing.setSlug(command.slug().trim());
    existing.setOwnerId(command.ownerId());
    existing.setIsActive(command.isActive() != null ? command.isActive() : existing.getIsActive());

    Workspace saved = workspaceRepository.save(existing);
    workspaceRepository.addMember(saved.getId(), command.ownerId());
    return toWorkspaceDto(saved);
  }

  @Override
  public void deleteWorkspace(Long id) {
    getWorkspace(id);
    try {
      workspaceRepository.deleteById(id);
    } catch (DataIntegrityViolationException ex) {
      throw new AppException(
          AppErrorCode.VALIDATION_ERROR,
          "Không thể xóa workspace khi vẫn còn dữ liệu nghiệp vụ phụ thuộc bên trong.");
    }
  }

  private User getUser(Long id) {
    return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user", id));
  }

  private Workspace getWorkspace(Long id) {
    return workspaceRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(AppErrorCode.WORKSPACE_NOT_FOUND, id));
  }

  private void validateUniqueUsername(String username, Long currentUserId) {
    userRepository
        .findByUsername(username.trim())
        .filter(user -> currentUserId == null || !user.getId().equals(currentUserId))
        .ifPresent(
            user -> {
              throw new AppException(
                  AppErrorCode.VALIDATION_ERROR, "Username đã tồn tại trong hệ thống.");
            });
  }

  private void validateUniqueEmail(String email, Long currentUserId) {
    String normalizedEmail = normalizeNullable(email);
    if (normalizedEmail == null) {
      return;
    }
    userRepository
        .findByEmail(normalizedEmail)
        .filter(user -> currentUserId == null || !user.getId().equals(currentUserId))
        .ifPresent(
            user -> {
              throw new AppException(
                  AppErrorCode.VALIDATION_ERROR, "Email đã tồn tại trong hệ thống.");
            });
  }

  private void validateUniqueSlug(String slug, Long currentWorkspaceId) {
    workspaceRepository
        .findBySlug(slug.trim())
        .filter(
            workspace ->
                currentWorkspaceId == null || !workspace.getId().equals(currentWorkspaceId))
        .ifPresent(
            workspace -> {
              throw new AppException(
                  AppErrorCode.VALIDATION_ERROR, "Slug workspace đã tồn tại trong hệ thống.");
            });
  }

  private AdminUserDto toAdminUserDto(User user) {
    return new AdminUserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getPhoneNumber(),
        user.getRole().name(),
        user.getIsActive());
  }

  private WorkspaceDto toWorkspaceDto(Workspace workspace) {
    return new WorkspaceDto(
        workspace.getId(),
        workspace.getName(),
        workspace.getDescription(),
        workspace.getSlug(),
        workspace.getOwnerId(),
        workspace.getIsActive(),
        workspace.getCreatedAt());
  }

  private int normalizeSize(int size) {
    if (size <= 0) {
      return DEFAULT_PAGE_SIZE;
    }
    return Math.min(size, MAX_PAGE_SIZE);
  }

  private String normalizeNullable(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  private UserRole parseUserRole(String role) {
    String normalizedRole = normalizeNullable(role);
    if (normalizedRole == null) {
      return null;
    }
    try {
      return UserRole.valueOf(normalizedRole.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new AppException(
          AppErrorCode.VALIDATION_ERROR, "Role không hợp lệ. Chỉ hỗ trợ SUPER_ADMIN hoặc USER.");
    }
  }
}
