# Roadmap

Roadmap này bám theo trạng thái code hiện tại và ưu tiên những phần còn thiếu ảnh hưởng trực tiếp tới tính hoàn chỉnh của MVP.

## Ưu tiên gần

### 1. Hoàn thiện incidents như một module đầy đủ

- thêm API list/filter/detail incident
- thêm màn hình incidents thật ở frontend
- liên kết incident với endpoint, workspace và lịch sử check

### 2. Hoàn thiện notification thực tế

- thêm email adapter
- thêm webhook adapter
- tách rõ contact routing theo `alert rule` và `endpoint override`

### 3. Hoàn thiện user onboarding

- thêm register API hoặc quyết định bỏ route register khỏi UI
- chuẩn hóa bootstrap admin/dev account cho local setup

### 4. Đồng bộ scheduler với policy

- dùng `CheckPolicy.intervalSeconds` đúng nghĩa
- tránh chỉ quét global fixed interval cho toàn hệ thống

## Ưu tiên tiếp theo

- audit log cho thay đổi tài nguyên
- incident history và endpoint history phân trang tốt hơn
- dashboard query tối ưu hơn khi dữ liệu tăng
- refresh token rotation và session hardening
- cải thiện text và UX cho frontend

## Ưu tiên trung hạn

- maintenance window
- retry và circuit breaker cho health checker
- outbox pattern cho notification
- dashboard realtime
- Prometheus/Grafana observability hoàn chỉnh

## Nguyên tắc phát triển tiếp

- ưu tiên đóng khoảng cách giữa UI và backend trước khi thêm tính năng mới xa hơn
- mọi tính năng mới nên cập nhật `docs/project/project-status.md` và `docs/api/openapi.json` nếu có đổi API
- tránh tạo thêm tài liệu phase tạm thời ở root; tài liệu chính thức nên nằm trong `docs/`
