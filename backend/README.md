# Backend

Backend là ứng dụng Spring Boot 3 chạy phần lõi của hệ thống giám sát: auth, workspace, quản lý tài nguyên giám sát, scheduler health check, incident và dashboard query.

## Chạy cục bộ

```powershell
docker compose up -d
cd backend
mvn spring-boot:run
```

Mặc định backend chạy ở `http://localhost:8080`.

## Thành phần chính

- `domain`: entity, value object, domain event, domain service.
- `application`: use case, port vào/ra, DTO, mapper.
- `infrastructure`: JPA adapter, security, scheduler, notification, executor HTTP/TCP.
- `delivery`: REST controller, response envelope, exception handler.

## Tài liệu chi tiết

- [Thiết lập local](../docs/getting-started/local-development.md)
- [Kiến trúc hệ thống](../docs/architecture/architecture.md)
- [Thiết kế database](../docs/architecture/database-design.md)
- [Tài liệu backend](../docs/engineering/backend.md)
- [API và OpenAPI](../docs/api/README.md)
