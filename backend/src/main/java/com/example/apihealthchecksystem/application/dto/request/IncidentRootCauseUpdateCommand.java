package com.example.apihealthchecksystem.application.dto.request;

import jakarta.validation.constraints.Size;

public record IncidentRootCauseUpdateCommand(@Size(max = 4000) String rootCause) {}
