package com.example.demo.service;

import com.example.demo.domain.DemoMode;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Service;

@Service
public class DemoScenarioService {
  private static final long DEFAULT_SLOW_DELAY_MS = 4000L;
  private static final int DEFAULT_ERROR_STATUS = 500;

  private final AtomicReference<DemoMode> mode = new AtomicReference<>(DemoMode.OK);
  private final AtomicInteger flakyCounter = new AtomicInteger(0);
  private final AtomicReference<Long> slowDelayMs = new AtomicReference<>(DEFAULT_SLOW_DELAY_MS);
  private final AtomicReference<Integer> errorStatus = new AtomicReference<>(DEFAULT_ERROR_STATUS);
  private final AtomicReference<LocalDateTime> updatedAt = new AtomicReference<>(LocalDateTime.now());

  public DemoState getCurrentState() {
    return new DemoState(
        mode.get(), slowDelayMs.get(), errorStatus.get(), flakyCounter.get(), updatedAt.get());
  }

  public DemoResult ok() {
    return DemoResult.ok("Demo service is healthy", getCurrentState());
  }

  public DemoResult slow(Long requestedDelayMs) throws InterruptedException {
    long delay = requestedDelayMs != null ? requestedDelayMs : slowDelayMs.get();
    Thread.sleep(Math.max(delay, 0L));
    return DemoResult.ok("Slow endpoint completed after delay", getCurrentState());
  }

  public DemoResult error(Integer requestedStatusCode) {
    int status = requestedStatusCode != null ? requestedStatusCode : errorStatus.get();
    return DemoResult.error(status, "Demo service forced an error response", getCurrentState());
  }

  public DemoResult flaky() {
    int attempt = flakyCounter.incrementAndGet();
    if (attempt % 2 == 0) {
      return DemoResult.ok("Flaky endpoint recovered on this attempt", getCurrentState());
    }
    return DemoResult.error(errorStatus.get(), "Flaky endpoint failed on this attempt", getCurrentState());
  }

  public DemoResult toggle() throws InterruptedException {
    return switch (mode.get()) {
      case OK -> ok();
      case SLOW -> slow(null);
      case ERROR -> error(null);
      case FLAKY -> flaky();
    };
  }

  public DemoState updateMode(DemoMode nextMode, Long nextSlowDelayMs, Integer nextErrorStatus) {
    mode.set(nextMode);
    if (nextSlowDelayMs != null) {
      slowDelayMs.set(Math.max(nextSlowDelayMs, 0L));
    }
    if (nextErrorStatus != null) {
      errorStatus.set(nextErrorStatus);
    }
    updatedAt.set(LocalDateTime.now());
    return getCurrentState();
  }

  public DemoState reset() {
    mode.set(DemoMode.OK);
    slowDelayMs.set(DEFAULT_SLOW_DELAY_MS);
    errorStatus.set(DEFAULT_ERROR_STATUS);
    flakyCounter.set(0);
    updatedAt.set(LocalDateTime.now());
    return getCurrentState();
  }

  public record DemoState(
      DemoMode mode,
      long slowDelayMs,
      int errorStatus,
      int flakyCounter,
      LocalDateTime updatedAt) {}

  public record DemoResult(
      boolean success,
      int statusCode,
      String message,
      DemoState state,
      LocalDateTime timestamp) {
    public static DemoResult ok(String message, DemoState state) {
      return new DemoResult(true, 200, message, state, LocalDateTime.now());
    }

    public static DemoResult error(int statusCode, String message, DemoState state) {
      return new DemoResult(false, statusCode, message, state, LocalDateTime.now());
    }
  }
}
