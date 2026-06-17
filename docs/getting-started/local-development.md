# Local Development

## Yêu cầu môi trường

- `Java 17`
- `Maven 3.9+`
- `Node.js 20+`
- `npm`
- `Docker` và `Docker Compose`

## Khởi động nhanh

### 1. Database

```powershell
docker compose up -d
```

Compose hiện chỉ khởi tạo PostgreSQL:

- Host: `localhost`
- Port: `5432`
- Database: `api_health_check`
- Username: `api_health_user`
- Password: `api_health_password`

### 2. Backend

```powershell
cd backend
mvn spring-boot:run
```

### 3. Frontend

```powershell
cd frontend
npm install
npm run dev
```

### 4. Demo target service

```powershell
cd demo-service
mvn spring-boot:run
```

Service nay cung cap cac endpoint de backend monitor trong luc demo:

- `http://localhost:8086/api/demo/health/ok`
- `http://localhost:8086/api/demo/health/slow`
- `http://localhost:8086/api/demo/health/error`
- `http://localhost:8086/api/demo/health/flaky`
- `http://localhost:8086/api/demo/health/toggle`

Neu muon doi mode nhanh trong luc demo:

```powershell
.\scripts\demo-set-mode.ps1 -Mode OK
.\scripts\demo-set-mode.ps1 -Mode ERROR
.\scripts\demo-set-mode.ps1 -Mode SLOW -SlowDelayMs 7000
```

## Cấu hình backend

Backend đọc cấu hình từ `application.yml` và hỗ trợ nạp thêm biến môi trường qua file `.env` ở thời điểm khởi động.

### Biến cấu hình thường dùng

| Biến | Mặc định | Ghi chú |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/api_health_check` | JDBC URL |
| `DB_USERNAME` | `api_health_user` | Tài khoản DB |
| `DB_PASSWORD` | `api_health_password` | Mật khẩu DB |
| `SERVER_PORT` | `8080` | Cổng backend |
| `FLYWAY_ENABLED` | `true` | Bật migration |
| `app.scheduler.interval-ms` | `60000` | Chu kỳ scheduler global |
| `app.jwt.secret` | có default trong code | Phải là chuỗi Base64 hợp lệ để JWT hoạt động ổn định |
| `app.jwt.expiration-ms` | `3600000` | Access token TTL |
| `app.jwt.refresh-expiration-ms` | `86400000` | Refresh token TTL |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Origin được phép gọi API |
| `APP_NOTIFICATION_WEBHOOK_URL` | rỗng | Webhook chung để nhận incident alert/recovery |
| `APP_NOTIFICATION_WEBHOOK_TIMEOUT_MS` | `5000` | Timeout gửi webhook |

## Cấu hình frontend

| Biến | Mặc định | Ghi chú |
|---|---|---|
| `VITE_API_BASE_URL` | `http://localhost:8080/api/v1` | Base URL cho REST API |

## Seed data

Repo đang có migration seed.

- `V5__full_seed_data.sql` thêm user, policy, alert rule, endpoint mẫu.
- `V8__add_workspaces_and_membership.sql` tạo `Default Workspace` và gắn admin mẫu vào workspace này.

Tài khoản seed hiện có trong migration:

- Username: `admin`
- Email: `admin@healthcheck.com`
- Role: `SUPER_ADMIN`

Password đang được lưu dưới dạng hash trong migration. Nếu cần đăng nhập local ổn định cho demo, nên xác nhận lại mật khẩu seed thực tế hoặc tạo user mới trực tiếp qua database/test fixture.

## URL hữu ích

- Backend API root: `http://localhost:8080/api/v1`
- Demo target service: `http://localhost:8086/api/demo`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON runtime: `http://localhost:8080/v3/api-docs`
- Health endpoint: `http://localhost:8080/api/v1/health`
- Actuator health: `http://localhost:8080/actuator/health`
- Prometheus metrics: `http://localhost:8080/actuator/prometheus`

## Lưu ý thực tế

- Frontend cần `workspace_id` trong local storage để giữ ngữ cảnh workspace; `refresh_token` nằm trong HttpOnly cookie của backend.
- API của nhiều module yêu cầu header `X-Workspace-Id`.
- Scheduler luôn chạy khi backend khởi động vì ứng dụng bật `@EnableScheduling`.
- Route `register` không còn là luồng demo chính; nên dùng account seed hoặc account demo cố định để tránh rủi ro trước giờ thi.
- Nếu cần demo alert thật, chỉ cần cấu hình `APP_NOTIFICATION_WEBHOOK_URL` là đủ để hệ thống gửi webhook khi incident mở hoặc phục hồi.
- Neu muon demo mo/resolve incident chu dong, hay monitor URL `http://localhost:8086/api/demo/health/toggle` roi doi mode qua `OK`, `ERROR`, `SLOW` hoac `FLAKY` thong qua `POST /api/demo/control/mode`.
- Neu muon chay full stack bang container de kiem tra nhanh, co the dung `docker compose -f docker-compose.app.yml up --build`.
