package com.example.apihealthchecksystem.infrastructure.persistence.support;

import java.util.Set;
import org.springframework.data.domain.Sort;

public final class RepositoryQuerySupport {
  private RepositoryQuerySupport() {}

  public static Sort buildSort(
      String sortBy, String sortDir, Set<String> allowedSortFields, String defaultSortField) {
    String normalizedSortBy =
        allowedSortFields.contains(sortBy) ? sortBy : defaultSortField;
    Sort.Direction direction =
        "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    return Sort.by(direction, normalizedSortBy);
  }

  public static String normalizeSearch(String search) {
    if (search == null || search.isBlank()) {
      return "";
    }
    return search.trim();
  }
}
