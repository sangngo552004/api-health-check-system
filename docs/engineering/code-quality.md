# Code Quality and Delivery Controls

Tài liệu này mô tả cách dự án đảm bảo chất lượng code từ local workflow đến CI pipeline, bao gồm format, lint, test, coverage, git hooks và kiểm soát contract API.

## Mục tiêu

- giữ code style nhất quán
- phát hiện lỗi sớm trước khi merge
- bảo vệ `Clean Architecture`
- giữ `OpenAPI` đồng bộ với code
- giảm commit hoặc PR fail vì lỗi cơ bản

## Các lớp bảo vệ chất lượng hiện có

- `pre-commit` git hook
- script local `scripts/run-quality-checks.ps1`
- backend CI với Spotless, Checkstyle, Maven verify
- frontend CI với Prettier, ESLint, build
- `ArchUnit` test cho layer dependency
- `SwaggerGeneratorTest` cho OpenAPI sync
- `JaCoCo` coverage threshold

## Git hooks

### Cài đặt

```powershell
scripts\install-git-hooks.ps1
```

Script này đặt:

```powershell
git config core.hooksPath .githooks
```

### Pre-commit hook

Hook file:

- `.githooks/pre-commit`

Hook này gọi:

```sh
pwsh -NoProfile -ExecutionPolicy Bypass -File scripts/run-quality-checks.ps1 -Mode pre-commit
```

### Hook đang chạy gì

`run-quality-checks.ps1 -Mode pre-commit` hiện chạy:

- frontend format check
- frontend lint
- backend Spotless check
- backend Checkstyle
- backend fast tests cho `CleanArchitectureTest` và `SwaggerGeneratorTest`

## CI pipeline

Workflow:

- `.github/workflows/ci.yml`

### Backend CI

1. setup JDK 17
2. `mvn spotless:check`
3. `mvn checkstyle:check`
4. `mvn clean verify`
5. kiểm tra `docs/api/openapi.json` có đồng bộ với runtime spec hay không

### Frontend CI

1. setup Node.js 20
2. `npm ci`
3. `npm run format:check`
4. `npm run lint`
5. `npm run build`

## Coverage

Backend dùng:

- `JaCoCo`

Ngưỡng hiện tại:

- tối thiểu `80%` line coverage ở mức bundle

Một số package được exclude khỏi rule coverage để tập trung vào phần logic và orchestration hơn boilerplate:

- `dto`
- `entity`
- `mapper`
- `exception`
- `config`
- `delivery`
- `infrastructure/persistence/repository`
- `domain/model`
- `domain/valueobject`
- `domain/event`
- `application/port`
- `ApiHealthCheckSystemApplication`

## Architecture quality

Repo không chỉ kiểm tra style và test, mà còn kiểm soát kiến trúc.

### ArchUnit

`CleanArchitectureTest` dùng để kiểm tra dependency direction giữa:

- `delivery`
- `application`
- `domain`
- `infrastructure`

### Documentation-as-quality

Ngoài automated checks, repo còn có:

- [Clean Architecture Implementation Guide](../architecture/clean-architecture-implementation.md)
- [Layer Coding Guidelines](../architecture/layer-coding-guidelines.md)
- [AI Agent Guide](ai-agent-guide.md)

## OpenAPI quality gate

Khi đổi controller hoặc DTO API, cần cập nhật file OpenAPI bằng:

```powershell
cd backend
mvn test -Dtest=SwaggerGeneratorTest -DgenerateSwagger=true
```

Nếu `docs/api/openapi.json` lệch với runtime spec, CI backend sẽ fail.

## Quy trình đề xuất cho developer local

Trước khi commit:

1. chạy feature local
2. chạy test liên quan tới phần vừa sửa
3. chạy:

```powershell
scripts\run-quality-checks.ps1 -Mode pre-commit
```

4. nếu đổi API, cập nhật OpenAPI
5. nếu đổi kiến trúc hoặc trạng thái dự án, cập nhật docs liên quan

## Những gì đã tốt

- có pre-commit hook
- có CI cho backend và frontend
- có architecture test
- có coverage gate
- có OpenAPI sync gate

## Những gì còn có thể cải tiến

- thêm frontend automated tests
- thêm nhiều rule ArchUnit chi tiết hơn
- thêm PR checklist/template
- thêm test cho `MonitorEndpointService`, `IncidentEventListener`, `HttpHealthCheckExecutor`
