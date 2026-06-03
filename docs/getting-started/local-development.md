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

Password đang được lưu dưới dạng hash trong migration. Nếu cần đăng nhập local ổn định cho demo, nên xác nhận lại mật khẩu seed thực tế hoặc tạo user mới trực tiếp qua database/test fixture.

## URL hữu ích

- Backend API root: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON runtime: `http://localhost:8080/v3/api-docs`
- Health endpoint: `http://localhost:8080/api/v1/health`
- Actuator health: `http://localhost:8080/actuator/health`
- Prometheus metrics: `http://localhost:8080/actuator/prometheus`

## Lưu ý thực tế

- Frontend cần `refresh_token` và `workspace_id` trong local storage để giữ phiên.
- API của nhiều module yêu cầu header `X-Workspace-Id`.
- Scheduler luôn chạy khi backend khởi động vì ứng dụng bật `@EnableScheduling`.
