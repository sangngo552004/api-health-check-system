package com.example.apihealthchecksystem.infrastructure.tcp;

import static org.junit.jupiter.api.Assertions.*;

import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.CheckStatus;
import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import java.io.IOException;
import java.net.ServerSocket;
import org.junit.jupiter.api.Test;

class TcpHealthCheckExecutorTest {

  private final TcpHealthCheckExecutor executor = new TcpHealthCheckExecutor();

  @Test
  void supports_shouldReturnTrueForTcp() {
    assertTrue(executor.supports(CheckType.TCP));
    assertFalse(executor.supports(CheckType.HTTP));
  }

  @Test
  void execute_shouldReturnUp_whenPortIsOpen() throws IOException {
    // Open an ephemeral server socket to simulate a live port
    try (ServerSocket serverSocket = new ServerSocket(0)) {
      int port = serverSocket.getLocalPort();

      MonitoredEndpoint endpoint =
          MonitoredEndpoint.builder().id(1L).workspaceId(10L).url("127.0.0.1:" + port).build();

      CheckPolicy policy = CheckPolicy.builder().timeoutMillis(1000).build();

      HealthCheckResult result = executor.execute(endpoint, policy);

      assertNotNull(result);
      assertEquals(CheckStatus.UP, result.getStatus());
      assertTrue(result.getSuccess());
      assertNull(result.getErrorMessage());
      assertEquals(1L, result.getEndpointId());
      assertEquals(10L, result.getWorkspaceId());
    }
  }

  @Test
  void execute_shouldReturnDown_whenPortIsClosed() {
    // Port 65530 is highly likely to be closed
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder().id(2L).workspaceId(10L).url("tcp://127.0.0.1:65530").build();

    CheckPolicy policy = CheckPolicy.builder().timeoutMillis(500).build();

    HealthCheckResult result = executor.execute(endpoint, policy);

    assertNotNull(result);
    assertEquals(CheckStatus.DOWN, result.getStatus());
    assertFalse(result.getSuccess());
    assertNotNull(result.getErrorMessage());
    assertTrue(result.getErrorMessage().contains("Connection failed"));
  }

  @Test
  void execute_shouldReturnDegraded_whenLatencyExceedsThreshold() throws IOException {
    try (ServerSocket serverSocket = new ServerSocket(0)) {
      int port = serverSocket.getLocalPort();

      MonitoredEndpoint endpoint =
          MonitoredEndpoint.builder().id(3L).workspaceId(10L).url("127.0.0.1:" + port).build();

      // Configure a 0ms threshold to force it into DEGRADED state
      CheckPolicy policy =
          CheckPolicy.builder().timeoutMillis(1000).latencyThresholdMillis(0).build();

      HealthCheckResult result = executor.execute(endpoint, policy);

      assertNotNull(result);
      assertEquals(CheckStatus.DEGRADED, result.getStatus());
      assertTrue(result.getSuccess());
      assertNotNull(result.getErrorMessage());
      assertTrue(result.getErrorMessage().contains("High latency"));
    }
  }
}
