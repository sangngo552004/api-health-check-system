package com.example.apihealthchecksystem.infrastructure.http;

import com.example.apihealthchecksystem.application.port.out.HealthCheckExecutor;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.CheckStatus;
import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HttpHealthCheckExecutor implements HealthCheckExecutor {

  @Override
  public boolean supports(CheckType checkType) {
    return CheckType.HTTP.equals(checkType);
  }

  @Override
  public HealthCheckResult execute(MonitoredEndpoint endpoint, CheckPolicy policy) {
    LocalDateTime checkedAt = LocalDateTime.now();
    long startTime = System.currentTimeMillis();

    int timeoutMillis = policy.effectiveTimeoutMillis();

    try {
      HttpClient client =
          HttpClient.newBuilder().connectTimeout(Duration.ofMillis(timeoutMillis)).build();

      HttpRequest.Builder requestBuilder =
          HttpRequest.newBuilder()
              .uri(URI.create(endpoint.getUrl()))
              .timeout(Duration.ofMillis(timeoutMillis))
              .method(
                  endpoint.getMethod().name(),
                  endpoint.getRequestBody() != null && !endpoint.getRequestBody().isBlank()
                      ? HttpRequest.BodyPublishers.ofString(endpoint.getRequestBody())
                      : HttpRequest.BodyPublishers.noBody());

      if (endpoint.getHeaders() != null) {
        for (Map.Entry<String, String> entry : endpoint.getHeaders().entrySet()) {
          requestBuilder.header(entry.getKey(), entry.getValue());
        }
      }

      HttpResponse<String> response =
          client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

      long responseTime = System.currentTimeMillis() - startTime;
      int statusCode = response.statusCode();

      boolean success = true;
      String errorMessage = null;

      // 1. Kiểm tra Status Code
      int expectedStatus = policy.effectiveExpectedStatusCode();
      if (statusCode != expectedStatus && (expectedStatus != 200 || statusCode >= 400)) {
        success = false;
        errorMessage =
            "Status code mismatch. Expected: " + expectedStatus + ", Actual: " + statusCode;
      }

      // 2. Kiểm tra Response Body (nếu có cấu hình)
      if (success && policy.hasExpectedResponseBody()) {
        if (response.body() == null
            || !response.body().contains(policy.getExpectedResponseBody())) {
          success = false;
          errorMessage = "Response body does not contain expected string.";
        }
      }

      // 3. Kiểm tra Regex (nếu có cấu hình)
      if (success && policy.hasResponseRegex()) {
        if (response.body() == null || !response.body().matches(policy.getResponseRegex())) {
          success = false;
          errorMessage = "Response body does not match regex.";
        }
      }

      CheckStatus status = success ? CheckStatus.UP : CheckStatus.DOWN;

      // Kiểm tra DEGRADED (phản hồi chậm nhưng vẫn thành công)
      if (success
          && policy.hasLatencyThreshold()
          && responseTime > policy.getLatencyThresholdMillis()) {
        status = CheckStatus.DEGRADED;
        errorMessage = "High latency: " + responseTime + "ms";
      }

      return HealthCheckResult.builder()
          .endpointId(endpoint.getId())
          .workspaceId(endpoint.getWorkspaceId())
          .checkedAt(checkedAt)
          .status(status)
          .httpStatusCode(statusCode)
          .responseTimeMillis(responseTime)
          .success(success)
          .errorMessage(errorMessage)
          .responsePayload(
              response.body().length() > 1000
                  ? response.body().substring(0, 1000)
                  : response.body())
          .nodeId(System.getProperty("app.scheduler.node-id", "local"))
          .build();

    } catch (Exception e) {
      long responseTime = System.currentTimeMillis() - startTime;
      log.error("Lỗi khi gọi HTTP health check tới {}: {}", endpoint.getUrl(), e.getMessage());
      return HealthCheckResult.builder()
          .endpointId(endpoint.getId())
          .workspaceId(endpoint.getWorkspaceId())
          .checkedAt(checkedAt)
          .status(CheckStatus.DOWN)
          .responseTimeMillis(responseTime)
          .success(false)
          .errorMessage("Connection failed: " + e.getMessage())
          .nodeId(System.getProperty("app.scheduler.node-id", "local"))
          .build();
    }
  }
}
