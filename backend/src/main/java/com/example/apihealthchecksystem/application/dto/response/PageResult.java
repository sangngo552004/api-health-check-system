package com.example.apihealthchecksystem.application.dto.response;

import java.util.List;

public record PageResult<T>(List<T> items, long totalItems) {}
