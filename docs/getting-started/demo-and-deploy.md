# Demo and Deploy

Tài liệu này gom lại đúng những gì nhóm cần trước buổi thi: chạy local ổn định, deploy online tối thiểu, và một kịch bản demo ngắn gọn nhưng đủ thuyết phục.

## 1. Biến môi trường quan trọng

### Backend

| Biến | Mặc định | Mục đích |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/api_health_check` | Kết nối PostgreSQL |
| `DB_USERNAME` | `api_health_user` | User DB |
| `DB_PASSWORD` | `api_health_password` | Password DB |
| `SERVER_PORT` | `8080` | Port backend |
| `FLYWAY_ENABLED` | `true` | Bật migration |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Cho phép frontend gọi API khi deploy tách host |
| `APP_NOTIFICATION_WEBHOOK_URL` | rỗng | Webhook chung để demo incident alert/recovery |
| `APP_NOTIFICATION_WEBHOOK_TIMEOUT_MS` | `5000` | Timeout khi gửi webhook |
| `app.jwt.secret` | có default trong code | Secret JWT, nên đặt riêng khi deploy |

### Frontend

| Biến | Mặc định | Mục đích |
|---|---|---|
| `VITE_API_BASE_URL` | `http://localhost:8080/api/v1` | Base URL REST API |

## 2. Mô hình deploy khuyến nghị

Để giảm rủi ro trước giờ thi, nên chọn mô hình:

- `Backend`: Spring Boot + PostgreSQL
- `Frontend`: static build từ Vite
- `Webhook demo`: một URL duy nhất như `webhook.site`, `Discord webhook` hoặc `Slack incoming webhook`

Không cần thêm Redis, queue hay service phụ cho vòng thi.

## 3. Checklist deploy nhanh

### Backend

1. Tạo PostgreSQL và cấu hình `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
2. Đặt `APP_CORS_ALLOWED_ORIGINS` theo domain frontend
3. Đặt `APP_NOTIFICATION_WEBHOOK_URL` để nhận alert khi demo
4. Đặt `app.jwt.secret` thành chuỗi Base64 ổn định
5. Chạy backend và kiểm tra:
   - `/actuator/health`
   - `/swagger-ui/index.html`
   - `/v3/api-docs`

### Frontend

1. Đặt `VITE_API_BASE_URL` trỏ tới backend online
2. Chạy `npm run build`
3. Deploy thư mục build tĩnh
4. Đăng nhập thử và xác nhận dashboard/incident hoạt động

## 3.1. Đóng gói nhanh bằng Docker

Repo hiện có đủ artefact để đóng gói tối thiểu:

- [backend/Dockerfile](../../backend/Dockerfile)
- [frontend/Dockerfile](../../frontend/Dockerfile)
- [demo-service/Dockerfile](../../demo-service/Dockerfile)
- [docker-compose.app.yml](../../docker-compose.app.yml)

Để chạy full stack local theo hướng gần với deploy:

```powershell
docker compose -f docker-compose.app.yml up --build
```

Khi đó:

- frontend: `http://localhost:3000`
- backend: `http://localhost:8080`
- demo-service: `http://localhost:8086`
- postgres: `localhost:5432`

## 4. Demo script đề xuất

1. Đăng nhập bằng account demo hoặc account seed.
2. Chọn workspace demo.
3. Vào `Endpoints` để chỉ ra endpoint đang được monitor.
4. Trigger một endpoint lỗi hoặc dùng endpoint lỗi đã chuẩn bị sẵn.
5. Chờ scheduler mở incident.
6. Vào `Dashboard` để chỉ ra active incidents board đã đổi trạng thái.
7. Vào `Incidents` để xem list, filter theo `OPEN`, rồi mở detail.
8. Mở tab webhook receiver để chứng minh hệ thống đã gửi cảnh báo thật.
9. Phục hồi endpoint để incident chuyển sang `RESOLVED`.
10. Làm mới `Incidents` hoặc `Dashboard` và cho thấy webhook recovery cũng được gửi.

Chi tiết hơn xem thêm:

- [Demo Runbook](demo-runbook.md)

## 5. Gợi ý account demo

- `admin / password123`: demo toàn bộ CRUD + incident
- `viewer / password123`: demo góc nhìn chỉ đọc nếu cần

Nếu seed local lệch với dữ liệu này, hãy tạo account tương đương trước buổi thi và cập nhật lại đúng vào tài liệu nhóm dùng nội bộ.

## 6. Quality gate nên kiểm tra trước buổi thi

Những lệnh đã được verify pass trên máy phát triển hiện tại:

```powershell
cd frontend
npm run build
```

```powershell
cd demo-service
mvn -DskipTests compile
```

```powershell
cd backend
mvn test
```

```powershell
cd backend
mvn test -Dtest=SwaggerGeneratorTest -DgenerateSwagger=true
```

Ghi chú môi trường:

- `backend` hiện chạy được full test suite và generate lại `docs/api/openapi.json`.
- Trong lúc test có warning từ Mockito/ByteBuddy về dynamic agent trên JDK 21, nhưng build hiện vẫn `SUCCESS` và không chặn demo.
- Nếu đổi máy hoặc đổi JDK, hãy ưu tiên chạy lại đúng 4 lệnh trên để xác nhận môi trường trước buổi thi.
