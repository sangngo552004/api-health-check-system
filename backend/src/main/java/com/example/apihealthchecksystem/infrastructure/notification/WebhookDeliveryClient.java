package com.example.apihealthchecksystem.infrastructure.notification;

public interface WebhookDeliveryClient {
  DeliveryResult post(String webhookUrl, String payload);

  record DeliveryResult(boolean delivered, String errorMessage) {
    public static DeliveryResult success() {
      return new DeliveryResult(true, null);
    }

    public static DeliveryResult failure(String errorMessage) {
      return new DeliveryResult(false, errorMessage);
    }
  }
}
