package com.example.apihealthchecksystem.delivery.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.apihealthchecksystem.application.dto.response.IncidentDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.port.in.GetIncidentUseCase;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IncidentControllerTest {
  @Mock private GetIncidentUseCase incidentUseCase;

  @InjectMocks private IncidentController controller;

  @Test
  void getIncidents_shouldDelegateToUseCase() {
    Long workspaceId = 1L;
    IncidentDto dto =
        new IncidentDto(
            100L,
            10L,
            "Payment API",
            workspaceId,
            LocalDateTime.of(2026, 6, 11, 10, 0),
            null,
            "OPEN",
            "HTTP 500",
            3,
            "CRITICAL",
            null,
            List.of(1L, 2L, 3L));
    PagedResponseDto<IncidentDto> paged = PagedResponseDto.of(List.of(dto), 0, 10, 1);
    when(incidentUseCase.getIncidents(workspaceId, "OPEN", 10L, 0, 10)).thenReturn(paged);

    PagedResponseDto<IncidentDto> result =
        controller.getIncidents(workspaceId, "OPEN", 10L, 0, 10).getData();

    assertEquals(1, result.items().size());
    verify(incidentUseCase).getIncidents(workspaceId, "OPEN", 10L, 0, 10);
  }

  @Test
  void getIncidentById_shouldReturnData() {
    Long workspaceId = 1L;
    IncidentDto dto =
        new IncidentDto(
            100L,
            10L,
            "Payment API",
            workspaceId,
            LocalDateTime.of(2026, 6, 11, 10, 0),
            null,
            "OPEN",
            "HTTP 500",
            3,
            "CRITICAL",
            null,
            List.of(1L, 2L, 3L));
    when(incidentUseCase.getIncidentById(workspaceId, 100L)).thenReturn(dto);

    IncidentDto result = controller.getIncidentById(workspaceId, 100L).getData();

    assertEquals("Payment API", result.endpointName());
    verify(incidentUseCase).getIncidentById(workspaceId, 100L);
  }
}
