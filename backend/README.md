# Backend

Module backend cho `API Health Check System`.

## Chạy cục bộ

1. Khởi động PostgreSQL bằng `docker compose up -d`.
2. Vào thư mục `backend`.
3. Chạy `mvn spring-boot:run`.

## Kiến trúc thư mục

- `domain`: mô hình nghiệp vụ thuần.
- `application`: use case, port, DTO.
- `infrastructure`: adapter cho database, HTTP, scheduler, notification.
- `delivery`: REST API và xử lý request/response.
