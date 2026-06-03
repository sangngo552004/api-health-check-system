package com.example.apihealthchecksystem.application.dto.response;

import java.time.LocalDateTime;

public record EndpointLatencyDto(
    LocalDateTime checkedAt, Long responseTimeMillis, Boolean success) {}
