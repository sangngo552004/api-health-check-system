package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.AdminUserCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AdminUserUpdateCommand;
import com.example.apihealthchecksystem.application.dto.request.AdminWorkspaceCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.AdminWorkspaceUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.AdminUserDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceMemberDto;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.BusinessRuleViolationException;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.exception.ValidationException;
import com.example.apihealthchecksystem.application.port.in.ManageAdminUseCase;
import com.example.apihealthchecksystem.application.port.out.UserRepository;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.domain.model.User;
import com.example.apihealthchecksystem.domain.model.Workspace;
import com.example.apihealthchecksystem.domain.model.WorkspaceMember;
import com.example.apihealthchecksystem.domain.valueobject.UserRole;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
      throw new BusinessRuleViolationException(AppErrorCode.USER_IS_WORKSPACE_OWNER);
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
  public WorkspaceDto createWorkspace(AdminWorkspaceCreateCommand command, Long currentUserId) {
    String normalizedSlug = normalizeSlug(command.slug());
    validateUniqueSlug(normalizedSlug, null);
    getUser(currentUserId);

    Workspace saved =
        workspaceRepository.save(
            Workspace.builder()
                .name(command.name().trim())
                .description(normalizeNullable(command.description()))
                .slug(normalizedSlug)
                .ownerId(currentUserId)
                .isActive(command.isActive() != null ? command.isActive() : Boolean.TRUE)
                .build());
    return toWorkspaceDto(saved);
  }

  @Override
  public WorkspaceDto updateWorkspace(Long id, AdminWorkspaceUpdateCommand command) {
    Workspace existing = getWorkspace(id);
    String normalizedSlug = normalizeSlug(command.slug());
    validateUniqueSlug(normalizedSlug, id);

    existing.setName(command.name().trim());
    existing.setDescription(normalizeNullable(command.description()));
    existing.setSlug(normalizedSlug);
    existing.setIsActive(command.isActive() != null ? command.isActive() : existing.getIsActive());

    return toWorkspaceDto(workspaceRepository.save(existing));
  }

  @Override
  public void deleteWorkspace(Long id) {
    getWorkspace(id);
    try {
      workspaceRepository.deleteById(id);
    } catch (DataIntegrityViolationException ex) {
      throw new BusinessRuleViolationException(AppErrorCode.WORKSPACE_HAS_DEPENDENT_DATA);
    }
  }

  @Override
  public void addWorkspaceMember(Long workspaceId, Long userId) {
    getWorkspace(workspaceId);
    User user = getUser(userId);
    if (user.getRole() == UserRole.ADMIN) {
      throw new BusinessRuleViolationException(AppErrorCode.ADMIN_CANNOT_JOIN_WORKSPACE);
    }
    workspaceRepository.addMember(workspaceId, userId);
  }

  @Override
  public void removeWorkspaceMember(Long workspaceId, Long userId) {
    getWorkspace(workspaceId);
    getUser(userId);
    workspaceRepository.removeMember(workspaceId, userId);
  }

  @Override
  public List<WorkspaceMemberDto> getWorkspaceMembers(Long workspaceId) {
    getWorkspace(workspaceId);
    var members = workspaceRepository.getMembers(workspaceId);
    var userIds = members.stream().map(WorkspaceMember::getUserId).toList();
    Map<Long, User> userMap =
        userRepository.findAllByIds(userIds).stream()
            .collect(Collectors.toMap(User::getId, user -> user));

    return members.stream()
        .map(
            member -> {
              User user = userMap.get(member.getUserId());
              return new WorkspaceMemberDto(
                  member.getUserId(),
                  user != null ? user.getUsername() : "Unknown",
                  user != null ? user.getEmail() : "Unknown",
                  member.getJoinedAt());
            })
        .toList();
  }

  private User getUser(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(AppErrorCode.USER_NOT_FOUND, id));
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
              throw new ValidationException(AppErrorCode.USERNAME_ALREADY_EXISTS);
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
              throw new ValidationException(AppErrorCode.EMAIL_ALREADY_EXISTS);
            });
  }

  private void validateUniqueSlug(String slug, Long currentWorkspaceId) {
    workspaceRepository
        .findBySlug(normalizeSlug(slug))
        .filter(
            workspace ->
                currentWorkspaceId == null || !workspace.getId().equals(currentWorkspaceId))
        .ifPresent(
            workspace -> {
              throw new ValidationException(AppErrorCode.WORKSPACE_SLUG_ALREADY_EXISTS);
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

  private String normalizeSlug(String slug) {
    if (slug == null) {
      return null;
    }
    String normalized =
        slug
            .trim()
            .toLowerCase()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-+|-+$", "");
    if (normalized.isEmpty()) {
      throw new ValidationException(AppErrorCode.VALIDATION_ERROR);
    }
    return normalized;
  }

  private UserRole parseUserRole(String role) {
    String normalizedRole = normalizeNullable(role);
    if (normalizedRole == null) {
      return null;
    }
    try {
      return UserRole.valueOf(normalizedRole.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new ValidationException(AppErrorCode.INVALID_ROLE);
    }
  }
}
