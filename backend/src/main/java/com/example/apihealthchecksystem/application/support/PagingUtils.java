package com.example.apihealthchecksystem.application.support;

public final class PagingUtils {
  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_SIZE = 10;
  private static final int MAX_SIZE = 100;

  private PagingUtils() {}

  public static int normalizePage(int page) {
    return Math.max(page, DEFAULT_PAGE);
  }

  public static int normalizeSize(int size) {
    if (size <= 0) {
      return DEFAULT_SIZE;
    }
    return Math.min(size, MAX_SIZE);
  }
}
