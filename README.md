# API Health Check System

Hệ thống giám sát sức khỏe API và dịch vụ, phục vụ mục tiêu học tập về thiết kế kiến trúc phần mềm, design pattern, và xây dựng một sản phẩm có thể demo được end-to-end.

## Mục tiêu dự án

- Quản lý danh sách endpoint/API cần giám sát.
- Tự động kiểm tra trạng thái endpoint theo chu kỳ.
- Lưu lịch sử kiểm tra gồm mã phản hồi, độ trễ, trạng thái thành công hoặc thất bại.
- Hiển thị dashboard tổng quan và lịch sử kiểm tra.
- Ghi nhận incident và phát cảnh báo khi dịch vụ gặp lỗi liên tục.
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

Giai đoạn đang triển khai: `Giai đoạn 1. Phân tích và đặc tả`

Tài liệu chi tiết:

- [Đặc tả yêu cầu giai đoạn 1](docs/phase-1/requirements-specification.md)
- [Sơ đồ use case](docs/phase-1/use-case-diagram.md)
- [Sơ đồ thành phần](docs/phase-1/component-diagram.md)
- [Tài liệu thiết kế ban đầu](thiet-ke-do-an-api-health-check-system.md)
