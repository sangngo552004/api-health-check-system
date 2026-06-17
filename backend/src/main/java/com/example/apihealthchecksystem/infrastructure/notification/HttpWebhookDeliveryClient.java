package com.example.apihealthchecksystem.infrastructure.notification;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HttpWebhookDeliveryClient implements WebhookDeliveryClient {
  private final HttpClient httpClient;

  public HttpWebhookDeliveryClient(
      @Value("${app.notification.webhook-timeout-ms:5000}") long timeoutMs) {
    this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(timeoutMs)).build();
  }

  @Override
  public DeliveryResult post(String webhookUrl, String payload) {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create(webhookUrl))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(10))
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();

    try {
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() < 200 || response.statusCode() >= 300) {
        String errorMessage =
            "Webhook tra ve status " + response.statusCode() + " voi body " + response.body();
        log.warn(
            "Webhook trả về status {} cho URL {} với body {}",
            response.statusCode(),
            webhookUrl,
            response.body());
        return DeliveryResult.failure(errorMessage);
      }
      return DeliveryResult.success();
    } catch (IOException ex) {
      log.warn("Không thể gửi webhook tới {}", webhookUrl, ex);
      return DeliveryResult.failure("Khong the gui webhook: " + ex.getMessage());
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      log.warn("Webhook bị gián đoạn khi gửi tới {}", webhookUrl, ex);
      return DeliveryResult.failure("Gui webhook bi gian doan: " + ex.getMessage());
    } catch (IllegalArgumentException ex) {
      log.warn("Webhook URL không hợp lệ: {}", webhookUrl, ex);
      return DeliveryResult.failure("Webhook URL khong hop le: " + ex.getMessage());
    }
  }
}
