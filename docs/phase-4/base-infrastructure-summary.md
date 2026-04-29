# Giai đoạn 4.1 - Thiết lập Base Infrastructure

Tài liệu này tổng hợp lại các công việc đã thực hiện trong bước đệm **Giai đoạn 4.1** của dự án API Health Check System, nhằm tạo nền móng vững chắc trước khi code các API cụ thể.

## 1. Mục tiêu đã hoàn thành

- Thiết lập chuẩn giao tiếp đầu ra (API Response) thống nhất cho toàn bộ hệ thống.
- Xây dựng cơ chế xử lý lỗi tập trung (Global Exception Handling) tự động bắt và trả về mã lỗi chuẩn.
- Tích hợp MapStruct để ánh xạ tự động giữa Domain Entity và JPA Entity, đảm bảo tuân thủ nghiêm ngặt Clean Architecture.
- Cấu hình Flyway để tự động quản lý schema database bằng script SQL.
- Cấu hình tự động nạp dữ liệu mẫu (Seed Data) thông qua script SQL Migration.
- Tích hợp hệ thống giám sát (Monitoring) thông qua Spring Boot Actuator và Prometheus.

## 2. Chuẩn hóa API và Xử lý Lỗi Toàn Cục

- **`ApiResponse<T>`**: Wrapper để chuẩn hóa JSON trả về theo định dạng `{ success, code, message, data, errors, timestamp }`.
- **`AppErrorCode` (Tầng Application)**: Enum trung tâm định nghĩa các mã lỗi (VD: `40401`). Được đặt ở tầng Application để tránh vi phạm kiến trúc khi tầng Delivery truy cập.
- **`GlobalExceptionHandler`**: Đón lõng các Exception tự động và định dạng lại thành `ApiResponse` với HTTP Status tương ứng.
- **Validation**: Tích hợp `spring-boot-starter-validation` và các annotation (`@NotBlank`, v.v.) vào các DTO.

## 3. Phân tách Kiến Trúc và Exception Flow

Hệ thống tuân thủ nghiêm ngặt Clean Architecture, đảm bảo các tầng bên trong không phụ thuộc vào tầng bên ngoài.

### Sơ đồ phân cấp Exception (Inside-Out):

1.  **`DomainException` (Tầng Domain)**:
    *   Kế thừa `RuntimeException`.
    *   Chỉ nhận `String message` thuần túy.
    *   Dùng để báo hiệu các vi phạm quy tắc nghiệp vụ cốt lõi.
2.  **`AppException` (Tầng Application)**:
    *   Kế thừa `DomainException`.
    *   Nhận thêm `AppErrorCode`.
    *   Là cầu nối gán mã lỗi kỹ thuật cho các lỗi nghiệp vụ.
3.  **`ResourceNotFoundException` (Tầng Application)**:
    *   Kế thừa `AppException`.
    *   Chuyên biệt cho lỗi không tìm thấy dữ liệu (trả về 404).

**Lợi ích:** Tầng Web (`GlobalExceptionHandler`) chỉ cần làm việc với các Exception của tầng `Application`, giúp tách biệt hoàn toàn logic hiển thị lỗi khỏi tầng `Domain`.

## 4. Quản trị Database và Seed Data

- Sử dụng Flyway để quản lý phiên bản cơ sở dữ liệu.
- **`V1__init_schema.sql`**: Khởi tạo cấu trúc các bảng.
- **`V2__insert_seed_data.sql`**: Tự động nạp dữ liệu mẫu (Google Search, Local Actuator) ngay khi ứng dụng khởi chạy lần đầu, thay thế cho cách dùng Java CommandLineRunner cũ để đảm bảo tính đồng bộ và sạch sẽ của code base.

## 5. Giám sát hệ thống (Monitoring)

- Kích hoạt **Spring Boot Actuator** và **Micrometer Prometheus Registry**.
- Endpoint phơi bày: `/actuator/prometheus`.

## 6. Chuẩn hóa Đa ngôn ngữ (i18n)

- **Backend**: Trả về i18n Key (VD: `validation.endpoint.name.not_blank`) thay vì chuỗi tiếng Việt cứng.
- **Frontend**: Sử dụng `vi.json` và `en.json` trong `src/locales/` để tra cứu và hiển thị ngôn ngữ tương ứng.

---
**Trạng thái**: Hoàn tất Phase 4.1. Cấu trúc đã vượt qua các bài kiểm tra tự động `CleanArchitectureTest`.
