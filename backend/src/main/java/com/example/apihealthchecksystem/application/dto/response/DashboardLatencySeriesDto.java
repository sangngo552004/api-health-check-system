package com.example.apihealthchecksystem.application.dto.response;

import java.util.List;

public record DashboardLatencySeriesDto(
    Long endpointId, String endpointName, List<EndpointLatencyDto> points) {}
