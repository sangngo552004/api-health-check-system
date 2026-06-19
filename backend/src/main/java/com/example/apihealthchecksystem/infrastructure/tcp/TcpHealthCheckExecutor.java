package com.example.apihealthchecksystem.infrastructure.tcp;

import com.example.apihealthchecksystem.application.port.out.HealthCheckExecutor;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.CheckStatus;
import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TcpHealthCheckExecutor implements HealthCheckExecutor {

  @Override
  public boolean supports(CheckType checkType) {
    return CheckType.TCP.equals(checkType);
  }

  @Override
  public HealthCheckResult execute(MonitoredEndpoint endpoint, CheckPolicy policy) {
    LocalDateTime checkedAt = LocalDateTime.now();
    long startTime = System.currentTimeMillis();

    int timeoutMillis = policy.effectiveTimeoutMillis();

    String host = endpoint.getUrl();
    int port = 80; // default TCP fallback port

    try {
      if (host.startsWith("tcp://")) {
        URI uri = URI.create(host);
        host = uri.getHost();
        port = uri.getPort() != -1 ? uri.getPort() : 80;
      } else if (host.contains("://")) {
        URI uri = URI.create(host);
        host = uri.getHost();
        port = uri.getPort() != -1 ? uri.getPort() : 80;
      } else if (host.contains(":")) {
        String[] parts = host.split(":");
        host = parts[0];
        port = Integer.parseInt(parts[1]);
      }
    } catch (Exception ex) {
      log.warn(
          "Lỗi phân tích URL TCP '{}', sử dụng giá trị mặc định: {}",
          endpoint.getUrl(),
          ex.getMessage());
    }

    Exception lastException = null;
    for (int attempt = 0; attempt <= policy.effectiveRetryCount(); attempt++) {
      try (Socket socket = new Socket()) {
        socket.connect(new InetSocketAddress(host, port), timeoutMillis);

        long responseTime = System.currentTimeMillis() - startTime;
        CheckStatus status = CheckStatus.UP;
        String errorMessage = null;

        if (policy.hasDegradedResponseTimeThreshold()
            && responseTime > policy.getDegradedResponseTimeMillis()) {
          status = CheckStatus.DEGRADED;
          errorMessage = "High latency: " + responseTime + "ms";
        }

        return HealthCheckResult.builder()
            .endpointId(endpoint.getId())
            .workspaceId(endpoint.getWorkspaceId())
            .checkedAt(checkedAt)
            .status(status)
            .responseTimeMillis(responseTime)
            .success(true)
            .errorMessage(errorMessage)
            .nodeId(System.getProperty("app.scheduler.node-id", "local"))
            .build();

      } catch (Exception e) {
        lastException = e;
      }
    }

    if (lastException != null) {
      long responseTime = System.currentTimeMillis() - startTime;
      log.error("Lỗi khi kết nối TCP tới {}:{}: {}", host, port, lastException.getMessage());
      return HealthCheckResult.builder()
          .endpointId(endpoint.getId())
          .workspaceId(endpoint.getWorkspaceId())
          .checkedAt(checkedAt)
          .status(CheckStatus.DOWN)
          .responseTimeMillis(responseTime)
          .success(false)
          .errorMessage("Connection failed: " + lastException.getMessage())
          .nodeId(System.getProperty("app.scheduler.node-id", "local"))
          .build();
    }

    throw new IllegalStateException("TCP health check failed without exception");
  }
}
