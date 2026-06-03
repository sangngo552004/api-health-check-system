# API Health Check System

Nền tảng giám sát API và service theo mô hình multi-workspace. Dự án hiện gồm backend Spring Boot, frontend React và một bộ tài liệu tập trung trong `docs/` để mô tả đúng trạng thái code hiện tại, các giới hạn đang có và hướng phát triển tiếp theo.

## Điểm chính

- Quản lý `workspace`, thành viên và phân quyền theo ngữ cảnh workspace.
- CRUD cho `endpoint`, `check policy`, `alert rule`, `contact group`.
- Health check nền cho endpoint active, có lưu lịch sử và tự mở/đóng incident.
- Hỗ trợ check `HTTP` và `TCP`.
- Dashboard workspace với thống kê tổng quan, incident đang mở và biểu đồ độ trễ.
- Xác thực bằng JWT access token + refresh token.
- OpenAPI được generate và kiểm tra đồng bộ trong CI.

## Trạng thái hiện tại

Dự án đã vượt giai đoạn khởi tạo và đang ở mức MVP đang phát triển tiếp.

- Backend đã có API thật cho auth, workspace, dashboard và các tài nguyên giám sát chính.
- Frontend đã kết nối API cho dashboard, endpoint, policy, alert, contact, member.
- Một số phần vẫn đang hoàn thiện, tiêu biểu là `register`, màn hình `incidents`, notification adapter thật và các luồng alert nâng cao.

Chi tiết hơn xem:

- [Tổng quan tài liệu](docs/README.md)
- [Cách đọc bộ docs](docs/getting-started/how-to-read-docs.md)
- [Tài liệu nghiệp vụ](docs/business/business-requirements.md)
- [Trạng thái dự án](docs/project/project-status.md)
- [Roadmap](docs/project/roadmap.md)

## Khởi động nhanh

### 1. Chạy database

```powershell
docker compose up -d
```

### 2. Chạy backend

```powershell
cd backend
mvn spring-boot:run
```

### 3. Chạy frontend

```powershell
cd frontend
npm install
npm run dev
```

Frontend mặc định gọi backend tại `http://localhost:8080/api/v1`.

## Tài liệu

- [Thiết lập môi trường local](docs/getting-started/local-development.md)
- [Kiến trúc hệ thống](docs/architecture/architecture.md)
- [Thiết kế database](docs/architecture/database-design.md)
- [Tài liệu backend](docs/engineering/backend.md)
- [Tài liệu frontend](docs/engineering/frontend.md)
- [API và OpenAPI](docs/api/README.md)
- [Code quality và CI](docs/engineering/code-quality.md)
- [Testing và quality gates](docs/engineering/testing-and-quality.md)

## Cấu trúc repo

```text
.
|-- backend/
|-- frontend/
|-- docs/
|   |-- api/
|   |-- architecture/
|   |-- business/
|   |-- engineering/
|   |-- getting-started/
|   `-- project/
|-- scripts/
`-- docker-compose.yml
```

