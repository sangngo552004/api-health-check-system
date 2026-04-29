# Giai đoạn 3 - Xây dựng domain và use case cốt lõi

Tài liệu này tổng hợp lại các công việc đã thực hiện trong **Giai đoạn 3** của dự án API Health Check System, theo định hướng Clean Architecture.

## 1. Mục tiêu đã hoàn thành

- Thiết kế các Entity, Value Object cốt lõi phản ánh nghiệp vụ.
- Thiết kế các Interface (Port Out) cho kho lưu trữ dữ liệu (Repository).
- Xây dựng DTO, Interface (Port In) và Use Case cho tính năng Quản lý Endpoint.
- Viết Unit Test cho Use Case bằng Mockito.
- Tích hợp tự động hóa sinh tài liệu Swagger/OpenAPI qua Unit Test.
- Tích hợp kiểm tra thay đổi Swagger vào quá trình pre-commit hook.

## 2. Kiến trúc Tầng Domain

Tầng Domain là trái tim của hệ thống, không chứa bất kỳ framework annotation nào (trừ Lombok để giảm boilerplate code).

### Các Entity chính
- **`MonitoredEndpoint`**: Đại diện cho một API hoặc dịch vụ cần giám sát.
- **`CheckPolicy`**: Quy định chu kỳ, ngưỡng timeout, số lần thử lại cho một endpoint cụ thể.
- **`HealthCheckResult`**: Ghi nhận kết quả của từng lần ping kiểm tra.
- **`Incident`**: Đại diện cho sự cố phát sinh khi một hệ thống con gặp gián đoạn.

### Các Value Object (Enums)
- **`HttpMethod`**: `GET`, `POST`, `PUT`, `DELETE`...
- **`CheckType`**: `HTTP`, `TCP`, `DATABASE`.
- **`EndpointStatus`**: `UP`, `DOWN`, `DEGRADED`.
- **`IncidentStatus`**: `OPEN`, `RESOLVED`, `CLOSED`.

*(Tất cả Entity sử dụng Id kiểu `Long` để thuận tiện tích hợp Database Relational sau này)*

## 3. Kiến trúc Tầng Application

### Port Out (Repositories)
Các Interface được khai báo để Use Case có thể lưu và truy xuất dữ liệu từ các Entity:
- `EndpointRepository`
- `CheckPolicyRepository`
- `HealthCheckResultRepository`
- `IncidentRepository`

### Port In & DTOs
Để tương tác với Use Case, chúng ta sử dụng `record` của Java 17 nhằm tối ưu hóa sự trong sáng và bất biến của DTO:
- `EndpointCreateCommand`
- `EndpointUpdateCommand`
- `EndpointDto`

### Use Case
`ManageEndpointService` triển khai `ManageEndpointUseCase`. Service này sử dụng logic thuần Java để thực hiện việc tạo mới, cập nhật và xóa endpoint cùng với policy tương ứng. Không hề sử dụng `@Service` hay `@Transactional` của Spring tại tầng này.

## 4. Kiểm thử và Chất lượng (Quality Assurance)

### Unit Test
- Viết `ManageEndpointServiceTest` áp dụng JUnit 5 và Mockito.
- Kiểm tra chi tiết luồng xử lý `createEndpoint`, đảm bảo dữ liệu chuẩn được truyền qua cho các Repository port out.

### ArchUnit Test
- Dự án vẫn duy trì `CleanArchitectureTest` để đảm bảo không ai được phép import `java.sql.*` hoặc thư viện của Spring Framework vào tầng Domain.

### Format Code & Linting
- **Backend**: Áp dụng `spotless-maven-plugin` (chuẩn Google Java Format) và `maven-checkstyle-plugin` để tự động kiểm tra định dạng và logic code cơ bản.
- **Frontend**: Áp dụng `Prettier` và `ESLint` để rà soát chất lượng mã nguồn TypeScript/React.

### Git Hooks (Local Fast Feedback)
- Cấu hình `pre-commit` để chạy nhanh các tác vụ: check format Frontend, check format/checkstyle Backend, ArchUnit tests, và kiểm tra tính đồng bộ của Swagger Docs. Quá trình chỉ mất vài giây, tối ưu trải nghiệm Dev (DX).
- Cấu hình `commit-msg` để buộc các commit tuân theo chuẩn Conventional Commits (ví dụ: `feat:`, `fix:`).

### Continuous Integration - CI (Comprehensive Checks)
- Thiết lập GitHub Actions (`ci.yml`) tự động kích hoạt khi có Push/Pull Request.
- **Backend CI**: Tự động build, kiểm tra format, chạy toàn bộ Unit/Integration Tests và xuất báo cáo Code Coverage bằng `JaCoCo`.
- **Frontend CI**: Tự động cài dependencies, chạy lint, check format và build production test.

## 5. Tự động hóa Swagger & Pre-commit Hook

Nhằm hỗ trợ tốt cho Frontend trong Giai đoạn tới, OpenAPI (Swagger) đã được đưa vào một cách tự động và nghiêm ngặt:

1. **`SwaggerGeneratorTest`**:
   - Chạy test tự động gọi vào API `/v3/api-docs` của Spring Boot.
   - Ghi nội dung Swagger JSON trực tiếp vào thư mục `docs/api/openapi.json`.
2. **`run-quality-checks.ps1`**:
   - Được móc nối vào `pre-commit` hook của Git.
   - Luôn chạy `mvn test` (để chạy Unit test, Arch test, và sinh file Swagger).
   - Có thêm logic kiểm tra `git status -s docs/api/openapi.json`. Nếu cấu trúc API thay đổi làm file này biến đổi, hook sẽ từ chối commit và báo cho kỹ sư phần mềm biết để chủ động thêm nó vào version control.

---
**Trạng thái**: Giai đoạn 3 đã hoàn tất. Dự án sẵn sàng bước sang Giai đoạn 4 (Tích hợp hạ tầng: Triển khai JPA, Scheduler và HTTP client).
