# Backend

## Công nghệ chính

- `Java 17`
- `Spring Boot 3.5`
- `Spring Web`
- `Spring Validation`
- `Spring Data JPA`
- `Spring Security`
- `Flyway`
- `PostgreSQL`
- `Springdoc OpenAPI`
- `Micrometer Prometheus`
- `MapStruct`
- `JUnit 5`, `ArchUnit`, `JaCoCo`, `Checkstyle`, `Spotless`

## Tài liệu nên đọc cùng

- [Architecture](../architecture/architecture.md)
- [Architecture and Design Patterns](../architecture/architecture-and-patterns.md)
- [Clean Architecture Implementation Guide](../architecture/clean-architecture-implementation.md)
- [Database Design](../architecture/database-design.md)

## API hiện có

### Auth

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`

### Workspaces

- `POST /api/v1/workspaces`
- `PUT /api/v1/workspaces/{id}`
- `GET /api/v1/workspaces/{id}`
- `GET /api/v1/workspaces/my`
- `DELETE /api/v1/workspaces/{id}`
- `POST /api/v1/workspaces/{id}/members`
- `DELETE /api/v1/workspaces/{id}/members/{userId}`
- `GET /api/v1/workspaces/{id}/members`

### Endpoints

- `POST /api/v1/endpoints`
- `PUT /api/v1/endpoints/{id}`
- `GET /api/v1/endpoints/{id}`
- `GET /api/v1/endpoints`
- `DELETE /api/v1/endpoints/{id}`

### Check policies

- `POST /api/v1/check-policies`
- `GET /api/v1/check-policies`
- `GET /api/v1/check-policies/{id}`
- `PUT /api/v1/check-policies/{id}`
- `DELETE /api/v1/check-policies/{id}`

### Alert rules

- `POST /api/v1/alert-rules`
- `GET /api/v1/alert-rules`
- `GET /api/v1/alert-rules/{id}`
- `PUT /api/v1/alert-rules/{id}`
- `DELETE /api/v1/alert-rules/{id}`

### Contact groups

- `POST /api/v1/contact-groups`
- `GET /api/v1/contact-groups`
- `GET /api/v1/contact-groups/{id}`
- `PUT /api/v1/contact-groups/{id}`
- `DELETE /api/v1/contact-groups/{id}`

### Dashboard

- `GET /api/v1/dashboard/stats`
- `GET /api/v1/dashboard/endpoints/{endpointId}/latency`

### Health and docs

- `GET /api/v1/health`
- `GET /actuator/health`
- `GET /actuator/prometheus`
- `GET /v3/api-docs`

## Response contract

REST API trả về envelope dạng:

```json
{
  "success": true,
  "code": "20000",
  "message": "Thành công",
  "data": {},
  "timestamp": "2026-06-03T10:00:00"
}
```

Lỗi validation trả thêm trường `errors` theo field.

## Core business rules hiện tại

- Endpoint chỉ được monitor nếu `isActive = true`.
- Mỗi endpoint cần `policyId` để scheduler thực thi check đúng.
- HTTP executor kiểm tra status code, response body, regex và latency threshold.
- TCP executor kiểm tra khả năng kết nối socket và latency threshold.
- Incident được quyết định dựa trên tập kết quả gần nhất và `failureThreshold` trong policy.
- Khi check xong, trạng thái endpoint được map từ `CheckStatus` sang `EndpointStatus`.

## Security model

- Tất cả API trừ auth, actuator và swagger đều yêu cầu xác thực.
- API theo workspace dùng `X-Workspace-Id` + `@PreAuthorize`.
- Quyền `member`: đọc dữ liệu trong workspace.
- Quyền `workspace admin`: tạo/sửa/xóa tài nguyên trong workspace.
- Quyền `ADMIN`: tạo/xóa workspace toàn cục.

## Persistence

- Migration nằm ở `backend/src/main/resources/db/migration`.
- Flyway chạy ở startup khi `FLYWAY_ENABLED=true`.
- Test profile dùng H2 và tắt Flyway để tối ưu test.

## Điểm chưa hoàn chỉnh cần biết

- Chưa có API đăng ký người dùng mới.
- Chưa có REST API riêng cho incident listing/history.
- Notification adapter thật chưa có; `LogNotificationAdapter` chủ yếu phục vụ demo và debug.
- `alertRuleIds` và `contactGroupIds` đã có mặt trong model endpoint nhưng luồng notification hiện chưa tận dụng đầy đủ.
