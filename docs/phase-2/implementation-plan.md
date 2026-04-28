# Giai đoạn 2 - Kế hoạch dựng khung dự án

## 1. Mục tiêu

Giai đoạn 2 tạo ra nền kỹ thuật chung để cả team có thể bắt đầu phát triển tính năng mà không phải tranh luận lại từ đầu về build tool, cấu trúc thư mục, cách chia trách nhiệm giữa các lớp và cách chạy hệ thống cục bộ.

Deliverable chính:

- Khung `backend` dùng `Spring Boot + Maven`.
- Khung `frontend` dùng `React + Vite + TypeScript`.
- Cấu trúc thư mục rõ vai trò cho cả backend và frontend.
- Môi trường cục bộ bằng `Docker Compose` cho `PostgreSQL`.
- Tài liệu giải thích kiến trúc và quy ước làm việc cho team.
- Bộ kiểm tra chất lượng code chạy local qua Git hook.

## 2. Phạm vi thực hiện

### Trong giai đoạn này

- Tạo module `backend/`.
- Tạo module `frontend/`.
- Tạo package và thư mục khung theo kiến trúc đã chốt.
- Thêm cấu hình `application.yml`, `docker-compose.yml`, file env mẫu.
- Thêm endpoint kiểm tra sống cơ bản cho backend.
- Thêm dashboard scaffold cơ bản cho frontend.
- Thêm tài liệu onboarding kiến trúc và chất lượng code.
- Thêm kiểm tra local trước khi commit hoặc push.

### Chưa làm ở giai đoạn này

- Chưa triển khai đầy đủ nghiệp vụ endpoint monitoring.
- Chưa làm scheduler chạy health check thật.
- Chưa kết nối frontend với API thật.
- Chưa thêm migration nghiệp vụ chính thức ngoài migration placeholder.
- Chưa có CI pipeline.

## 3. Cấu trúc repo sau giai đoạn 2

```text
api-health-check-system/
  backend/
  frontend/
  docs/
    phase-1/
    phase-2/
  scripts/
  .githooks/
  docker-compose.yml
  README.md
```

## 4. Cấu trúc backend và công dụng từng phần

```text
backend/
  src/main/java/com/example/apihealthchecksystem/
    domain/
      model/
      valueobject/
      service/
      event/
      exception/
    application/
      port/in/
      port/out/
      dto/
      usecase/
    infrastructure/
      config/
      persistence/
      http/
      scheduler/
      notification/
      event/
    delivery/
      rest/
      rest/advice/
```

Giải thích:

- `domain/model`: entity nghiệp vụ cốt lõi như `MonitoredEndpoint`, `Incident`, `HealthCheckResult`.
- `domain/valueobject`: kiểu dữ liệu biểu diễn ý nghĩa nghiệp vụ như `EndpointStatus`, `CheckType`, `Environment`.
- `domain/service`: logic nghiệp vụ thuần không thuộc riêng một entity.
- `domain/event`: sự kiện nghiệp vụ như `IncidentCreatedEvent`, `IncidentResolvedEvent`.
- `domain/exception`: lỗi nghiệp vụ thuần.
- `application/port/in`: use case mà bên ngoài được phép gọi vào.
- `application/port/out`: interface mô tả nhu cầu ra ngoài như lưu DB, gọi HTTP, gửi notification.
- `application/dto`: DTO dùng trao đổi dữ liệu giữa delivery và application.
- `application/usecase`: nơi cài đặt orchestration của use case.
- `infrastructure/config`: bean cấu hình framework, datasource, scheduler, OpenAPI.
- `infrastructure/persistence`: JPA entity, Spring Data repository, mapper persistence.
- `infrastructure/http`: adapter gọi HTTP ra bên ngoài.
- `infrastructure/scheduler`: adapter kích hoạt health check theo lịch.
- `infrastructure/notification`: adapter gửi cảnh báo.
- `infrastructure/event`: publisher hoặc subscriber cho event nội bộ.
- `delivery/rest`: controller REST.
- `delivery/rest/advice`: xử lý lỗi và response chung.

## 5. Cấu trúc frontend và công dụng từng phần

```text
frontend/
  src/
    app/
    pages/
    components/
    features/
    services/
    hooks/
    types/
    utils/
```

Giải thích:

- `app`: cấu hình application level như router, provider, layout gốc, bootstrapping.
- `pages`: các màn hình theo route như dashboard, endpoint list, incident list.
- `components`: component dùng lại được ở nhiều nơi như badge, card, table, modal.
- `features`: nhóm code theo nghiệp vụ màn hình như `endpoint-management`, `incident-overview`.
- `services`: lớp gọi API, chuẩn hóa request/response, mapping dữ liệu.
- `hooks`: custom React hook tái sử dụng.
- `types`: kiểu dữ liệu dùng chung cho frontend.
- `utils`: hàm tiện ích thuần, không giữ state UI.

## 6. Nguyên tắc tổ chức code ở giai đoạn 2

- Chỉ dựng khung đủ để team bắt đầu code tiếp, không nhét logic giả quá nhiều.
- Mỗi thư mục phải đại diện cho một trách nhiệm rõ ràng.
- Không để framework chi phối lớp nghiệp vụ.
- Không để frontend trộn lẫn page-level concern với reusable component.
- Mọi quyết định ở phase 2 phải hỗ trợ phase 3 và phase 5, không tạo nợ kỹ thuật sớm.

## 7. Checklist hoàn thành

- Có `backend/pom.xml`.
- Có `backend/src/main/java/.../ApiHealthCheckSystemApplication.java`.
- Có package khung cho `domain`, `application`, `infrastructure`, `delivery`.
- Có `frontend/package.json`, `vite.config.ts`, `src/`.
- Có `docker-compose.yml` cho `PostgreSQL`.
- Có tài liệu giải thích cấu trúc thư mục cho team.
- Có Git hook kiểm tra chất lượng code local.
