package com.example.apihealthchecksystem.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.apihealthchecksystem.application.dto.EndpointCreateCommand;
import com.example.apihealthchecksystem.application.dto.EndpointDto;
import com.example.apihealthchecksystem.application.port.out.CheckPolicyRepository;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManageEndpointServiceTest {

  @Mock private EndpointRepository endpointRepository;

  @Mock private CheckPolicyRepository checkPolicyRepository;

  @InjectMocks private ManageEndpointService manageEndpointService;

  @Test
  void createEndpoint_shouldSaveEndpointAndPolicy() {
    EndpointCreateCommand command =
        new EndpointCreateCommand(
            "Test API",
            "http://test.com",
            HttpMethod.GET,
            "DEV",
            CheckType.HTTP,
            200,
            60,
            5000,
            3,
            3,
            2000);

    MonitoredEndpoint mockEndpoint =
        MonitoredEndpoint.builder()
            .id(1L)
            .name("Test API")
            .url("http://test.com")
            .method(HttpMethod.GET)
            .environment("DEV")
            .checkType(CheckType.HTTP)
            .expectedStatusCode(200)
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    CheckPolicy mockPolicy =
        CheckPolicy.builder()
            .id(10L)
            .endpointId(1L)
            .intervalSeconds(60)
            .timeoutMillis(5000)
            .retryCount(3)
            .failureThreshold(3)
            .latencyThresholdMillis(2000)
            .build();

    when(endpointRepository.save(any(MonitoredEndpoint.class))).thenReturn(mockEndpoint);
    when(checkPolicyRepository.save(any(CheckPolicy.class))).thenReturn(mockPolicy);

    EndpointDto result = manageEndpointService.createEndpoint(command);

    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("Test API", result.name());
    assertEquals(60, result.intervalSeconds());

    verify(endpointRepository, times(1)).save(any(MonitoredEndpoint.class));
    verify(checkPolicyRepository, times(1)).save(any(CheckPolicy.class));
  }
}
