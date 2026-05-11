package com.example.apihealthchecksystem.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ContactGroupCreateCommand(
    @NotBlank String name,
    String description,
    List<Long> userIds,
    List<String> emailAddresses,
    List<String> webhookUrls) {}
