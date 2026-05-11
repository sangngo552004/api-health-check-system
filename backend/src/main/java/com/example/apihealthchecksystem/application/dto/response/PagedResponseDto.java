package com.example.apihealthchecksystem.application.dto.response;

import java.util.List;

public record PagedResponseDto<T>(
    List<T> items, int page, int size, long totalItems, int totalPages) {
  public static <T> PagedResponseDto<T> of(List<T> items, int page, int size, long totalItems) {
    int totalPages = (int) Math.ceil((double) totalItems / size);
    return new PagedResponseDto<>(items, page, size, totalItems, totalPages);
  }
}
