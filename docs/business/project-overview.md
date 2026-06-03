# Project Overview

## Mục tiêu

`API Health Check System` là một ứng dụng nội bộ để theo dõi sức khỏe endpoint và service theo workspace. Mỗi workspace có thể quản lý tài nguyên giám sát riêng, theo dõi sự cố riêng và xem dashboard riêng.

## Vấn đề hệ thống giải quyết

- Tập trung danh sách endpoint cần giám sát.
- Tự động kiểm tra định kỳ thay vì kiểm tra thủ công.
- Lưu lịch sử phản hồi và độ trễ để nhìn lại xu hướng.
- Tự động mở incident khi lỗi lặp lại theo policy.
- Tạo nền tảng để gửi cảnh báo theo nhóm liên hệ.

## Phạm vi hiện tại

### Đã có trong code

- Auth với `login` và `refresh token`.
- Multi-workspace và membership.
- CRUD cho `endpoint`, `check policy`, `alert rule`, `contact group`.
- Dashboard workspace.
- Scheduler health check nền.
- Health check `HTTP` và `TCP`.
- Incident lifecycle cơ bản: mở khi fail liên tiếp, đóng khi phục hồi.

### Có mặt trong UI hoặc domain nhưng chưa hoàn chỉnh

- Màn hình `register` ở frontend chưa có backend API tương ứng.
- Màn hình `incidents` chưa hoàn thiện.
- Notification hiện mới đi qua `LogNotificationAdapter`, chưa có email/webhook adapter thật.
- Alert/contact override theo endpoint vẫn còn đơn giản hóa trong luồng xử lý sự kiện.

## Người dùng và vai trò

- `ADMIN`: quản trị hệ thống ở mức global, có thể tạo/xóa workspace.
- `Workspace Admin`: quản lý tài nguyên trong workspace và thành viên của workspace đó.
- `Workspace Member`: xem dashboard và dữ liệu trong workspace mà mình thuộc về.

## Tài nguyên nghiệp vụ chính

- `Workspace`: không gian làm việc logic để tách dữ liệu.
- `MonitoredEndpoint`: endpoint hoặc service cần được check.
- `CheckPolicy`: rule xác định timeout, failure threshold, regex, expected status.
- `AlertRule`: rule dùng cho cảnh báo.
- `ContactGroup`: nhóm người nhận cảnh báo.
- `HealthCheckResult`: lịch sử kết quả check.
- `Incident`: sự cố đang mở hoặc đã đóng.

## Non-goals hiện tại

- Chưa nhắm tới triển khai production multi-node hoàn chỉnh.
- Chưa có luồng onboarding người dùng đầy đủ.
- Chưa có notification delivery production-grade.
- Chưa có self-service analytics hoặc report SLA hoàn chỉnh.
