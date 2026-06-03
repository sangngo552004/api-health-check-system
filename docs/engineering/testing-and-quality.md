# Testing and Quality

Nếu bạn cần góc nhìn rộng hơn về `CI`, `git hooks`, `coverage`, `OpenAPI gate` và luồng đảm bảo chất lượng code từ local tới pipeline, xem thêm:

- [Code Quality and Delivery Controls](code-quality.md)

## CI hiện tại

Workflow `.github/workflows/ci.yml` chạy hai job:

### Backend CI

- `mvn spotless:check`
- `mvn checkstyle:check`
- `mvn clean verify`
- kiểm tra `docs/api/openapi.json` có còn đồng bộ với `/v3/api-docs` hay không

### Frontend CI

- `npm ci`
- `npm run format:check`
- `npm run lint`
- `npm run build`

## Chạy kiểm tra cục bộ

### Backend

```powershell
cd backend
mvn spotless:check
mvn checkstyle:check
mvn clean verify
```

### Frontend

```powershell
cd frontend
npm install
npm run format:check
npm run lint
npm run build
```

### Script hỗ trợ

Repo có script:

```powershell
scripts\run-quality-checks.ps1 -Mode pre-commit
```

Script này hiện chạy:

- frontend format check
- frontend lint
- backend spotless
- backend checkstyle
- backend fast tests cho `CleanArchitectureTest` và `SwaggerGeneratorTest`

## Test coverage trong backend

Các nhóm test hiện diện trong repo:

- context load test
- ArchUnit test cho ranh giới kiến trúc
- use case test
- integration test cho persistence adapter
- test cho TCP executor
- Swagger/OpenAPI sync test

JaCoCo được cấu hình với ngưỡng line coverage tối thiểu `80%` ở mức bundle, có loại trừ một số package boilerplate như DTO, entity, mapper, config và delivery.

## OpenAPI generation

Để cập nhật file OpenAPI sau khi đổi controller hoặc DTO:

```powershell
cd backend
mvn test -Dtest=SwaggerGeneratorTest -DgenerateSwagger=true
```

File sinh ra sẽ được ghi vào:

- `docs/api/openapi.json`

Nếu file này lệch với runtime API spec, CI backend sẽ fail.
