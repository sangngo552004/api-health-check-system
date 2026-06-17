# Architecture

## Tổng quan

Hệ thống gồm hai ứng dụng:

- `backend`: Spring Boot phục vụ API, scheduler và persistence.
- `frontend`: React dashboard cho người dùng nội bộ.

Backend đi theo hướng `Clean Architecture` kết hợp tư duy `ports and adapters`.

Nếu cần phần giải thích sâu về cách các quy tắc này được hiện thực trong code, xem thêm:

- [Clean Architecture Implementation Guide](clean-architecture-implementation.md)
- [Architecture and Design Patterns](architecture-and-patterns.md)

## Cấu trúc backend

### `domain`

Chứa mô hình nghiệp vụ thuần:

- entity như `MonitoredEndpoint`, `CheckPolicy`, `Incident`, `Workspace`
- value object như `CheckType`, `EndpointStatus`, `UserRole`
- domain event như `EndpointCheckedEvent`, `IncidentOpenedEvent`, `IncidentResolvedEvent`
- domain service như `IncidentAnalyzer`

### `application`

Chứa orchestration và use case:

- input port: `ManageEndpointUseCase`, `GetDashboardStatsUseCase`, `AuthUseCase`
- output port: repository, executor, notification, authentication
- DTO request/response
- mapper DTO

### `infrastructure`

Chứa adapter triển khai kỹ thuật:

- JPA repository + mapper + entity
- security JWT
- scheduler
- executor HTTP/TCP
- notification adapter
- wiring cấu hình

### `delivery`

Chứa REST API và error handling:

- controller
- `ApiResponse`
- `GlobalExceptionHandler`

## Monitoring flow hiện tại

1. `HealthCheckScheduler` chạy theo `app.scheduler.interval-ms`, mặc định 60 giây.
2. Scheduler gọi `MonitorEndpointService.runHealthCheckForAll()`.
3. Service lấy toàn bộ endpoint active.
4. Với mỗi endpoint, service chọn `HealthCheckExecutor` phù hợp theo `CheckType`.
5. Executor trả về `HealthCheckResult`.
6. Kết quả được lưu xuống database.
7. `MonitoredEndpoint` được cập nhật trạng thái hiện tại và `lastCheckedAt`.
8. Hệ thống phát `EndpointCheckedEvent`.
9. `IncidentAnalyzer` đọc 10 kết quả gần nhất để quyết định mở, đóng hay giữ nguyên incident.
10. Nếu incident thay đổi, hệ thống phát `IncidentOpenedEvent` hoặc `IncidentResolvedEvent`.
11. `IncidentEventListener` bắt sự kiện và gọi `NotificationPort`.

## Multi-tenancy và phân quyền

- Dữ liệu nghiệp vụ chính có `workspace_id`.
- Frontend gửi workspace hiện tại qua header `X-Workspace-Id`.
- Backend dùng `@PreAuthorize` kết hợp `WorkspaceSecurityEvaluator` để kiểm tra quyền `member` hoặc `admin` trong workspace.
- Tạo, sửa, xóa workspace và quản lý membership yêu cầu `SUPER_ADMIN`.

## Auth flow

- Public API: `/api/v1/auth/**`
- Access token được frontend giữ trong memory.
- Refresh token được lưu ở `localStorage`.
- Frontend tự gọi `/auth/refresh` khi khởi tạo phiên nếu còn refresh token.

## Quan sát và tài liệu API

- Spring Actuator bật `health`, `info`, `prometheus`.
- Springdoc OpenAPI phục vụ `/v3/api-docs` và Swagger UI.
- File `docs/api/openapi.json` được generate từ test để đồng bộ với code.

## Giới hạn kiến trúc hiện tại

- Scheduler đang chạy theo chu kỳ global; `CheckPolicy.intervalSeconds` chưa được dùng để lập lịch riêng cho từng endpoint.
- Notification chưa có adapter gửi thật; hiện chỉ log.
- Luồng contact group khi incident mở/đóng đang lấy toàn bộ contact group của workspace.
- Chưa có cơ chế distributed lock hoặc leader election cho scheduler nếu chạy nhiều instance.
