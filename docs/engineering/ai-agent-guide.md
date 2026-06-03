# AI Agent Guide

Tài liệu này dành cho AI agent hoặc bất kỳ người nào muốn tiếp tục code trong dự án mà vẫn giữ đúng convention, kiến trúc và cách triển khai hiện tại.

Mục tiêu là làm cho những lần sau bạn nhờ AI code trở nên đơn giản hơn, ít phải nhắc lại, và ít nguy cơ phá vỡ design của dự án.

## 1. Mục tiêu khi agent làm việc trên repo này

Agent phải ưu tiên:

- giữ đúng `Clean Architecture`
- không phá vỡ boundary giữa các layer
- bám theo pattern đang có của repo
- cập nhật docs nếu thay đổi ảnh hưởng kiến trúc, API hoặc trạng thái dự án
- tránh thêm “giải pháp tiện tay” nhưng lệch convention

## 2. Bức tranh ngắn gọn của repo

- backend: Spring Boot, chia `domain`, `application`, `infrastructure`, `delivery`
- frontend: React + Zustand + service API modules
- transaction boundary: ở `application.usecase` qua config AOP
- DTO mapper: ở `application.mapper`
- persistence mapper: ở `infrastructure.persistence.mapper`
- OpenAPI file: `docs/api/openapi.json`

## 3. Quy tắc bắt buộc cho backend

### 3.1. Layer placement

- logic thuần của entity/value object/domain service -> `domain`
- orchestration của use case -> `application.usecase`
- interface dependency ra ngoài -> `application.port.out`
- REST contract -> `delivery.rest`
- JPA/security/scheduler/http/tcp/notification/config -> `infrastructure`

### 3.2. Những điều agent không nên làm

- không cho use case gọi `JpaRepository` trực tiếp
- không cho controller chứa nhiều business logic
- không cho domain phụ thuộc Spring/JPA/HTTP layer
- không thêm mapper “all-in-one” biết cả DTO, domain và JPA entity
- không rải `@Transactional` bừa bãi vào controller hoặc domain

### 3.3. Khi thêm một use case mới

Agent nên đi theo trình tự:

1. xác định input port cần có hay không
2. xác định output port cần có hay không
3. đặt orchestration ở `application.usecase`
4. dùng domain model/domain service nếu logic là business core
5. thêm adapter ở infrastructure nếu cần persistence/integration
6. thêm controller nếu cần REST
7. thêm test cho use case
8. cập nhật OpenAPI nếu API đổi
9. cập nhật docs liên quan

## 4. Quy tắc bắt buộc cho frontend

### 4.1. Data flow hiện tại

Convention frontend đang gần với flow:

- `services/api.ts`: HTTP client chung
- `services/api/*.api.ts`: module API theo resource
- `store/useXStore.ts`: Zustand store theo module
- `features/...`: component/screen dùng store

### 4.2. Khi thêm feature frontend mới

Agent nên đi theo trình tự:

1. tạo hoặc cập nhật type ở `src/types`
2. thêm API module ở `src/services/api`
3. thêm Zustand store nếu là resource có state riêng
4. thêm screen/component trong `src/features`
5. nối route nếu cần
6. giữ cách xử lý token/workspace header theo convention hiện có

### 4.3. Những điều agent không nên làm

- không bypass `api.ts` để gọi fetch lung tung khắp nơi
- không trộn logic network nặng trực tiếp vào component nếu đã có module service/store
- không tạo state management pattern mới nếu chưa cần
- không tự đổi authentication flow hiện có nếu chưa được yêu cầu

## 5. Quy tắc đặt test

### Backend

- domain rule -> test ở `domain/...`
- use case -> test ở `application/usecase/...`
- adapter persistence -> `infrastructure/persistence/adapter/...IT`
- architecture rule -> `architecture/CleanArchitectureTest`
- API spec sync -> `SwaggerGeneratorTest`

### Frontend

Hiện chưa có test automation rõ ràng, nên nếu agent được yêu cầu thêm test frontend thì nên:

- đề xuất framework trước khi thêm ồ ạt
- ưu tiên test cho logic có giá trị cao hơn snapshot UI đơn thuần

## 6. Files agent nên đọc trước khi code

Nếu agent làm backend:

1. [Documentation Index](../README.md)
2. [Clean Architecture Implementation Guide](../architecture/clean-architecture-implementation.md)
3. [Layer Coding Guidelines](../architecture/layer-coding-guidelines.md)
4. [Architecture Decisions](../architecture/architecture-decisions.md)
5. [Project Status](../project/project-status.md)
6. [Progress Checklist](../project/progress-checklist.md)

Nếu agent làm frontend:

1. [Frontend](frontend.md)
2. [Project Status](../project/project-status.md)
3. [Progress Checklist](../project/progress-checklist.md)
4. [Layer Coding Guidelines](../architecture/layer-coding-guidelines.md)

## 7. Prompt template gợi ý cho AI agent

Bạn có thể dùng gần như nguyên văn mẫu này khi nhờ AI code:

```text
Bạn đang làm việc trong dự án API Health Check System.
Hãy tuân thủ Clean Architecture của repo:
- domain cho business rule thuần
- application cho use case orchestration
- infrastructure cho adapter, config, persistence, scheduler, security
- delivery cho REST layer

Quy tắc quan trọng:
- không cho use case gọi JpaRepository trực tiếp
- không thêm @Transactional lung tung; transaction boundary đang ở application.usecase qua config
- mapper DTO nằm ở application.mapper
- mapper persistence nằm ở infrastructure.persistence.mapper
- nếu đổi API thì cập nhật docs/api/openapi.json và docs liên quan
- nếu thêm feature mới thì cập nhật docs/project/project-status.md và docs/project/progress-checklist.md khi phù hợp

Trước khi code, hãy đọc:
- docs/architecture/clean-architecture-implementation.md
- docs/architecture/layer-coding-guidelines.md
- docs/project/project-status.md
- docs/project/progress-checklist.md

Sau khi code, hãy giải thích thay đổi theo đúng boundary kiến trúc.
```

## 8. Checklist cho agent trước khi sửa code

- [ ] mình đang sửa backend hay frontend
- [ ] feature này thuộc tầng nào
- [ ] có use case/port hiện có nào tái sử dụng được không
- [ ] có cần mapper mới không
- [ ] có cần API contract mới không
- [ ] có cần test mới không
- [ ] có cần cập nhật docs không

## 9. Checklist cho agent sau khi sửa code

- [ ] boundary kiến trúc có còn sạch không
- [ ] có test phù hợp cho phần logic mới không
- [ ] nếu đổi controller/DTO thì OpenAPI có cần cập nhật không
- [ ] nếu đổi trạng thái tính năng thì docs progress/status có cần cập nhật không
- [ ] naming và package placement có khớp convention hiện tại không

## 10. Các lỗi AI agent dễ mắc trong repo này

- nhét business logic vào controller cho nhanh
- dùng thẳng JPA repository trong use case
- tạo mapper sai tầng
- viết code mới mà quên `X-Workspace-Id` context
- mô tả tính năng như đã hoàn chỉnh trong docs dù thực tế mới partial
- thêm framework/pattern mới mà không cần thiết

## 11. Nếu agent cần cập nhật docs, nên cập nhật file nào

- thay đổi overall progress -> `project-status.md`, `progress-checklist.md`
- thay đổi kiến trúc -> `architecture.md`, `clean-architecture-implementation.md`, `architecture-decisions.md`, `database-design.md`
- thay đổi API -> `docs/api/openapi.json`, `docs/api/README.md`
- thay đổi test strategy -> `testing-and-quality.md`, `test-case-catalog.md`

## 12. Kết luận

Nếu agent tuân thủ file này, khả năng cao output sẽ:

- đúng convention hơn
- ít phá clean architecture hơn
- dễ review hơn
- đỡ tốn công nhắc lại bối cảnh ở các lần code sau
