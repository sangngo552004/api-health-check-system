# API Health Check System

Hệ thống giám sát sức khỏe API và dịch vụ, phục vụ mục tiêu học tập về thiết kế kiến trúc phần mềm, design pattern, và xây dựng một sản phẩm có thể demo được end-to-end.

## Mục tiêu dự án

- Quản lý danh sách endpoint/API cần giám sát.
- Tự động kiểm tra trạng thái endpoint theo chu kỳ.
- Lưu lịch sử kiểm tra gồm mã phản hồi, độ trễ, trạng thái thành công hoặc thất bại.
- Kiểm tra nội dung phản hồi (Response Body) qua chuỗi cố định hoặc Regex.
- Hiển thị dashboard tổng quan và lịch sử kiểm tra.
- Ghi nhận incident và phát cảnh báo khi dịch vụ gặp lỗi liên tục.
- Quản lý nhóm nhận thông báo (Contact Groups) linh hoạt qua Email, Webhook.
- Thể hiện rõ cách áp dụng `Clean Architecture`, `SOLID`, và design pattern phù hợp.

## Kiến trúc định hướng

Backend dự kiến dùng `Clean Architecture` kết hợp tư duy `Hexagonal Architecture`:

- `Domain`: entity, value object, business rule, domain service.
- `Application`: use case, port vào/ra, DTO, orchestration.
- `Infrastructure`: database, scheduler, HTTP client, notification, cấu hình.
- `Delivery`: REST API phục vụ frontend.
- `Frontend`: dashboard quản trị và giám sát.

## Công nghệ dự kiến

### Backend

- `Java 17`
- `Spring Boot`
- `Spring Web`
- `Spring Data JPA`
- `Spring Validation`
- `Spring Scheduler`
- `PostgreSQL`
- `Flyway`
- `JUnit 5`
- `Mockito`

### Frontend

- `React`
- `Vite`
- `TypeScript`
- `Tailwind CSS` hoặc `MUI`
- `Recharts` hoặc `Chart.js`

### DevOps

- `Docker`
- `Docker Compose`
- `OpenAPI / Swagger`

## Lộ trình triển khai

### Giai đoạn 1. Phân tích và đặc tả

- Hoàn thiện tài liệu yêu cầu.
- Xác định actor, use case, business rule.
- Chốt kiến trúc và công nghệ.
- Vẽ sơ đồ use case và sơ đồ thành phần.

### Giai đoạn 2. Dựng khung dự án

- Khởi tạo backend Spring Boot.
- Khởi tạo frontend React.
- Tạo cấu trúc thư mục theo kiến trúc đã chọn.
- Cấu hình database, migration, Docker Compose.

### Giai đoạn 3. Xây dựng domain và use case cốt lõi

- Thiết kế entity và repository interface.
- Xây dựng use case quản lý endpoint.
- Xây dựng use case health check.
- Xây dựng use case incident và alert.

### Giai đoạn 4. Tích hợp hạ tầng

- Triển khai repository JPA.
- Tích hợp scheduler.
- Tích hợp HTTP client.
- Tích hợp notification adapter.

### Giai đoạn 5. Xây dựng frontend dashboard

- Dựng layout dashboard.
- Tích hợp API backend.
- Hiển thị biểu đồ, trạng thái, lịch sử.
- Tạo form CRUD endpoint.

### Giai đoạn 6. Hoàn thiện và kiểm thử

- Viết unit test và integration test.
- Tối ưu luồng xử lý lỗi.
- Hoàn thiện logging và tài liệu API.
- Chạy demo toàn hệ thống.

### Giai đoạn 7. Chuẩn bị báo cáo

- Viết mô tả bài toán và phân tích kiến trúc.
- Trình bày design pattern đã dùng.
- Bổ sung UML, ERD, sequence diagram.
- Chuẩn bị demo và thuyết trình.

## Trạng thái hiện tại

Giai đoạn đang triển khai: `Giai đoạn 4. Tích hợp hạ tầng`

Tài liệu chi tiết:

- [Đặc tả yêu cầu giai đoạn 1](docs/phase-1/requirements-specification.md)
- [Sơ đồ use case](docs/phase-1/use-case-diagram.md)
- [Sơ đồ thành phần](docs/phase-1/component-diagram.md)
- [Kế hoạch triển khai giai đoạn 2](docs/phase-2/implementation-plan.md)
- [Quyết định kỹ thuật giai đoạn 2](docs/phase-2/technical-decisions.md)
- [Hướng dẫn Clean Architecture cho team](docs/phase-2/clean-architecture-guide.md)
- [Hướng dẫn kiểm tra chất lượng code local](docs/phase-2/code-quality-guide.md)
- [Tổng kết Giai đoạn 3](docs/phase-3/implementation-summary.md)
- [Tài liệu thiết kế ban đầu](thiet-ke-do-an-api-health-check-system.md)

---

## Định hướng mở rộng tương lai

> Phần này ghi lại các ý tưởng mở rộng sau khi hoàn thành MVP. Vẫn giữ scope **internal tool**, tập trung vào học kỹ năng thực tế.

### 🟢 Nhóm 1 — Business Logic (Dễ làm, học được nhiều)

#### 1. Maintenance Window (Cửa sổ bảo trì có kế hoạch)

Cho phép đặt lịch downtime có kế hoạch cho endpoint. Trong khoảng thời gian đó, hệ thống không tạo incident và không gửi alert dù endpoint thật sự DOWN.

- Domain: `MaintenanceWindow` (endpointId, startAt, endAt, reason)
- Scheduler kiểm tra trước khi tạo incident: `if (endpoint.isInMaintenance()) → skip alert`
- **Học được:** Time-based business rule, domain state phức tạp.

#### 2. Alert Escalation (Leo thang cảnh báo)

Nếu incident mở quá X phút mà không ai resolve, tự động leo thang cảnh báo lên kênh khác hoặc người khác.

- Domain: `AlertEscalationPolicy` (incidentId, delayMinutes, escalationChannel)
- `@Scheduled` mỗi 5 phút: kiểm tra incident `open > threshold` → escalate
- **Học được:** Scheduled job phức tạp hơn, state machine cho incident lifecycle.

#### 3. Endpoint Dependency Graph (Đồ thị phụ thuộc)

Khai báo "Endpoint A phụ thuộc Endpoint B". Khi B DOWN, hệ thống hiểu A DOWN là *do B* chứ không phải lỗi riêng → tránh tạo incident thừa.

- Domain: `EndpointDependency` (endpointId, dependsOnEndpointId)
- `RootCauseAnalyzer`: phân tích ai DOWN trước, ai DOWN do dependency
- **Học được:** Graph traversal, root cause analysis pattern.

#### 4. SLA Tracking & Uptime Report

Tính uptime % của từng endpoint theo tuần/tháng, so với SLA target (ví dụ 99.9%).

- `SlaReport` (endpointId, period, uptimePct, slaTarget, slaBreached)
- **Học được:** Aggregation query phức tạp, reporting pattern.

#### 5. Smart Alert — Anomaly Detection đơn giản

Thay vì alert khi response time vượt ngưỡng cố định, so với **baseline động** (trung bình rolling 7 ngày). Nếu response time > `baseline × 2` → cảnh báo DEGRADED.

- **Học được:** Rolling average, sliding window pattern.

---

### 🟡 Nhóm 2 — System Design Patterns (Trung bình, kỹ năng backend thực tế)

#### 6. Redis Cache cho Dashboard

Dashboard query tổng hợp từ nhiều bảng → chậm theo thời gian. Cache với TTL 30 giây, invalidate khi có kết quả check mới.

- Spring Cache abstraction + Redis
- `@Cacheable("dashboard:summary")`, `@CacheEvict` khi có `EndpointCheckedEvent`
- **Học được:** Caching strategy, cache invalidation problem.

#### 7. Outbox Pattern cho Notification ⭐

Thay vì gửi notification trực tiếp trong transaction (nếu gửi fail thì mất), dùng Transactional Outbox:

1. Transaction: lưu `HealthCheckResult` + lưu `NotificationOutbox` (status=PENDING) cùng 1 transaction
2. `@Scheduled` đọc PENDING → gửi → update SENT/FAILED
3. Retry tự động cho FAILED

- **Học được:** At-least-once delivery, transactional outbox — pattern cực kỳ phổ biến trong production.

#### 8. Circuit Breaker cho Health Checker ⭐

Khi gọi API bên ngoài liên tục timeout, không nên cứ gọi mãi. Circuit Breaker tự "mở" sau N lần fail, ngừng gọi một thời gian rồi thử lại.

- Dùng `Resilience4j CircuitBreaker`
- States: `CLOSED → OPEN → HALF_OPEN → CLOSED`
- **Học được:** Resilience4j, Circuit Breaker pattern — chuẩn microservices.

#### 9. Retry với Exponential Backoff

Khi check fail, retry ngay với delay tăng dần thay vì đợi hết interval:

```
Lần 1 fail → retry sau 5s → Lần 2 fail → retry sau 10s → Lần 3 fail → ghi nhận DOWN
```

- Dùng `Resilience4j Retry`
- **Học được:** Exponential backoff strategy, Resilience4j.

#### 10. Structured Logging + Correlation ID ⭐

Mỗi lần health check tạo `checkId` (UUID), gắn vào tất cả log trong luồng đó qua MDC. Log ra JSON format thay vì plain text.

```java
MDC.put("checkId", UUID.randomUUID().toString());
MDC.put("endpointId", endpoint.getId().toString());
// → {"checkId":"abc", "endpointId":"1", "msg":"Health check started"}
```

- **Học được:** MDC (Mapped Diagnostic Context), structured logging, observability concept.

#### 11. Cursor-based Pagination cho HealthCheckResult

Kết quả health check tăng rất nhanh → offset pagination chậm dần. Dùng cursor:

```
GET /results?cursor=<lastCheckedAt>&limit=50
→ WHERE checked_at < :cursor ORDER BY checked_at DESC LIMIT 50
```

- **Học được:** Cursor pagination vs offset, query optimization.

#### 15. Self-monitoring — Hệ thống tự giám sát mình

Hệ thống health check nên tự giám sát scheduler của mình có đang chạy không:

```java
@Component
class SchedulerHealthIndicator implements HealthIndicator {
    // DOWN nếu lastRun > 2 × interval (scheduler bị treo)
}
```

- Expose qua `/actuator/health`
- **Học được:** Spring Boot Actuator custom indicator, meta-monitoring.

---

### 🔴 Nhóm 3 — Advanced System Design (Khó, rất ấn tượng nếu làm được)

#### 12. WebSocket Real-time Dashboard

Dashboard tự cập nhật không cần F5 khi có kết quả check mới:

- Backend: `@EventListener(EndpointCheckedEvent)` → `SimpMessagingTemplate.send("/topic/status")`
- Frontend: STOMP client subscribe → update React state real-time
- **Học được:** WebSocket, STOMP protocol, event-driven frontend.

#### 13. Metrics Export → Prometheus → Grafana ⭐

Expose custom metrics qua `/actuator/prometheus`, Prometheus scrape, Grafana visualize:

```java
// Micrometer
Counter.builder("health.check.total").tag("status", "UP").register(registry).increment();
Gauge.builder("health.endpoint.response_time_ms", ...).register(registry);
```

- **Học được:** Micrometer, Prometheus, Grafana — bộ observability stack tiêu chuẩn ngành.

#### 14. CQRS Light — Tách Read/Write Model

Tách rõ write model (lưu raw data) và read model (optimize cho query dashboard):

- Write: `ExecuteHealthCheckService` → lưu raw `HealthCheckResult`
- Read: `DashboardQueryService` → dùng DB view / materialized query được optimize riêng
- **Học được:** CQRS concept, read model optimization.

---

### Tóm tắt ưu tiên nếu chỉ chọn được một số

| Tính năng | Độ khó | Lý do nên làm |
|-----------|--------|--------------|
| Outbox Pattern | 🟡 | At-least-once delivery — cực kỳ thực tế |
| Circuit Breaker | 🟡 | Resilience4j — chuẩn industry |
| Structured Logging | 🟢 | Nền tảng observability, dễ làm ngay |
| Maintenance Window | 🟢 | Business logic thú vị, không khó |
| SLA Tracking | 🟢 | Có giá trị demo cao |
| Prometheus + Grafana | 🔴 | Ấn tượng nhất khi trình bày |

