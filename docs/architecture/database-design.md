# Database Design

Tài liệu này mô tả thiết kế database của dự án theo trạng thái hiện tại của migration và persistence model.

## Công nghệ và quản lý schema

- database chính: `PostgreSQL`
- migration tool: `Flyway`
- local DB: `docker-compose`
- test profile: `H2` theo mode PostgreSQL cho một số test

Schema evolution nằm trong:

- `backend/src/main/resources/db/migration`

## Tư duy thiết kế dữ liệu

Database xoay quanh các trục:

- `workspace` để phân vùng dữ liệu
- `endpoint` là đối tượng monitor trung tâm
- `check policy` để định nghĩa rule kiểm tra
- `health check result` để lưu lịch sử
- `incident` để biểu diễn sự cố nghiệp vụ
- `alert/contact` để phục vụ cảnh báo
- `user/membership` cho phân quyền

## Tiến hóa schema theo migration

### Giai đoạn nền tảng

- `monitored_endpoints`
- `check_policies`
- `health_check_results`
- `incidents`

### Giai đoạn mở rộng monitoring và contacts

- `users`
- `contact_groups`
- `alert_rules`
- các bảng quan hệ giữa endpoint, alert rule và contact group

### Giai đoạn align schema

- thêm `endpoint_headers`
- thêm `endpoint_tags`
- mở rộng incident với `failure_count`, `severity`, `root_cause`
- thêm `notifications`

### Giai đoạn security

- `refresh_tokens`
- field bảo mật bổ sung cho `users`

### Giai đoạn multi-workspace

- `workspaces`
- `workspace_members`
- thêm `workspace_id` vào các bảng nghiệp vụ chính
- thêm index theo workspace

### Giai đoạn tối ưu runtime

- thêm `last_checked_at`
- thêm index cho endpoint active, result history và incident status

## Các bảng cốt lõi

### `workspaces`

Đơn vị phân vùng dữ liệu chính.

Field nổi bật:

- `name`
- `description`
- `slug`
- `owner_id`
- `is_active`

### `workspace_members`

Liên kết user với workspace, kèm `role`.

### `users`

Lưu danh tính người dùng và thông tin auth cơ bản.

Field nổi bật:

- `username`
- `email`
- `phone_number`
- `password_hash`
- `role`
- `requires_password_change`
- `is_active`

### `refresh_tokens`

Lưu refresh token cho auth flow.

Index nổi bật:

- `idx_refresh_token`

### `monitored_endpoints`

Bảng trung tâm của hệ thống.

Field nổi bật:

- `name`
- `url`
- `method`
- `environment`
- `check_type`
- `workspace_id`
- `is_active`
- `status`
- `policy_id`
- `request_body`
- `created_by`
- `last_checked_at`

Bảng phụ liên quan:

- `endpoint_headers`
- `endpoint_tags`
- `endpoint_alert_rules`
- `endpoint_contact_groups`

### `check_policies`

Định nghĩa cách endpoint được kiểm tra.

Field nổi bật:

- `interval_seconds`
- `timeout_millis`
- `retry_count`
- `failure_threshold`
- `latency_threshold_millis`
- `expected_status_code`
- `expected_response_body`
- `response_regex`
- `workspace_id`

### `health_check_results`

Lưu lịch sử từng lần check.

Field nổi bật:

- `endpoint_id`
- `workspace_id`
- `checked_at`
- `status`
- `http_status_code`
- `response_time_millis`
- `error_message`
- `response_payload`
- `node_id`
- `success`

Index quan trọng:

- `idx_result_endpoint_checked_at`

### `incidents`

Biểu diễn sự cố nghiệp vụ đang mở hoặc đã đóng.

Field nổi bật:

- `endpoint_id`
- `workspace_id`
- `started_at`
- `resolved_at`
- `status`
- `reason`
- `failure_count`
- `severity`
- `root_cause`

Bảng phụ:

- `incident_failing_results`

Index quan trọng:

- `idx_incident_endpoint_status`

### `contact_groups`

Nhóm người nhận cảnh báo.

Bảng phụ:

- `contact_group_users`
- `contact_group_emails`
- `contact_group_webhooks`

### `alert_rules`

Lưu rule cảnh báo ở mức cấu hình.

Bảng phụ:

- `alert_rule_contact_groups`
- `endpoint_alert_rules`

### `notifications`

Schema đã chuẩn bị bảng notification, nhưng runtime delivery hiện vẫn còn đơn giản hóa.

## Multi-tenancy và phân quyền dữ liệu

Sau migration workspace, phần lớn bảng nghiệp vụ chính đều có:

- `workspace_id`

Điều này phục vụ:

- lọc dữ liệu theo workspace
- authorization theo context
- dashboard và query theo team

## Index và tối ưu hiện có

Các index nổi bật:

- `idx_endpoint_workspace`
- `idx_policy_workspace`
- `idx_rule_workspace`
- `idx_group_workspace`
- `idx_result_workspace`
- `idx_incident_workspace`
- `idx_endpoint_is_active`
- `idx_result_endpoint_checked_at`
- `idx_incident_endpoint_status`

## Điểm mạnh hiện tại

- schema bám khá sát domain
- đã chuyển sang multi-workspace rõ ràng
- đã có index cho query runtime quan trọng
- migration history phản ánh được tiến hóa của hệ thống

## Điểm còn có thể cải tiến

- `interval_seconds` đã có trong schema nhưng runtime scheduler chưa tận dụng hết
- `health_check_results` sẽ là bảng tăng nhanh nhất, tương lai có thể cần retention/archive/partitioning
- `notifications` hiện chưa được khai thác đầy đủ ở runtime
- incident API/UI vẫn chưa tận dụng hết tiềm năng schema
