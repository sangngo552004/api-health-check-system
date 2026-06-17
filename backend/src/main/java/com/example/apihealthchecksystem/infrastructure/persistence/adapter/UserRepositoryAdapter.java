package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.dto.response.PageResult;
import com.example.apihealthchecksystem.application.port.out.UserRepository;
import com.example.apihealthchecksystem.domain.model.User;
import com.example.apihealthchecksystem.domain.valueobject.UserRole;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.UserMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.UserJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("id", "username", "email", "phoneNumber", "role", "isActive", "createdAt");

  private final UserJpaRepository jpaRepository;
  private final UserMapper mapper;

  @Override
  public List<User> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public PageResult<User> search(
      String search,
      UserRole role,
      Boolean isActive,
      int page,
      int size,
      String sortBy,
      String sortDir) {
    var pageable = PageRequest.of(page, size, buildSort(sortBy, sortDir));
    var result = jpaRepository.search(normalizeSearch(search), role, isActive, pageable);
    return new PageResult<>(
        result.getContent().stream().map(mapper::toDomain).toList(), result.getTotalElements());
  }

  @Override
  public Optional<User> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return jpaRepository.findByUsername(username).map(mapper::toDomain);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return jpaRepository.findByEmail(email).map(mapper::toDomain);
  }

  @Override
  public List<User> findAllByIds(List<Long> ids) {
    return jpaRepository.findAllById(ids).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsByUsername(String username) {
    return jpaRepository.existsByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpaRepository.existsByEmail(email);
  }

  @Override
  public User save(User user) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(user)));
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  private Sort buildSort(String sortBy, String sortDir) {
    String normalizedSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
    Sort.Direction direction =
        "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    return Sort.by(direction, normalizedSortBy);
  }

  private String normalizeSearch(String search) {
    if (search == null || search.isBlank()) {
      return null;
    }
    return search.trim();
  }
}
