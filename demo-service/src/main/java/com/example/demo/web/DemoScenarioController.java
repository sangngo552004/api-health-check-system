package com.example.demo.web;

import com.example.demo.domain.DemoMode;
import com.example.demo.service.DemoScenarioService;
import com.example.demo.service.DemoScenarioService.DemoResult;
import com.example.demo.service.DemoScenarioService.DemoState;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
@Validated
public class DemoScenarioController {
  private final DemoScenarioService scenarioService;

  public DemoScenarioController(DemoScenarioService scenarioService) {
    this.scenarioService = scenarioService;
  }

  @GetMapping("/health/ok")
  public ResponseEntity<DemoResult> healthOk() {
    return ResponseEntity.ok(scenarioService.ok());
  }

  @GetMapping("/health/slow")
  public ResponseEntity<DemoResult> healthSlow(
      @RequestParam(required = false) @Min(0) Long delayMs) throws InterruptedException {
    return ResponseEntity.ok(scenarioService.slow(delayMs));
  }

  @GetMapping("/health/error")
  public ResponseEntity<DemoResult> healthError(
      @RequestParam(required = false) @Min(400) @Max(599) Integer statusCode) {
    DemoResult result = scenarioService.error(statusCode);
    return ResponseEntity.status(result.statusCode()).body(result);
  }

  @GetMapping("/health/flaky")
  public ResponseEntity<DemoResult> healthFlaky() {
    DemoResult result = scenarioService.flaky();
    return ResponseEntity.status(result.statusCode()).body(result);
  }

  @GetMapping("/health/toggle")
  public ResponseEntity<DemoResult> healthToggle() throws InterruptedException {
    DemoResult result = scenarioService.toggle();
    return ResponseEntity.status(result.statusCode()).body(result);
  }

  @GetMapping("/control/state")
  public ResponseEntity<DemoState> currentState() {
    return ResponseEntity.ok(scenarioService.getCurrentState());
  }

  @PostMapping("/control/mode")
  public ResponseEntity<DemoState> updateMode(@RequestBody UpdateModeRequest request) {
    return ResponseEntity.ok(
        scenarioService.updateMode(request.mode(), request.slowDelayMs(), request.errorStatus()));
  }

  @PostMapping("/control/reset")
  public ResponseEntity<DemoState> reset() {
    return ResponseEntity.ok(scenarioService.reset());
  }

  public record UpdateModeRequest(
      @NotNull DemoMode mode, @Min(0) Long slowDelayMs, @Min(400) @Max(599) Integer errorStatus) {}
}
