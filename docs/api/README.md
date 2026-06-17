# API Documentation

## Nguồn tài liệu API

Nguồn chuẩn cho REST API là:

- runtime spec tại `/v3/api-docs`
- file commit kèm repo tại [openapi.json](openapi.json)

Swagger UI có tại:

- `http://localhost:8080/swagger-ui/index.html`

## Quy ước xác thực và header

- API bảo vệ bằng `Authorization: Bearer <access-token>`
- Phần lớn API nghiệp vụ yêu cầu thêm `X-Workspace-Id: <workspace-id>`
- `auth`, `actuator` và `swagger` hiện là public endpoint

## Nhóm endpoint

- `auth`
- `workspaces`
- `endpoints`
- `check-policies`
- `alert-rules`
- `contact-groups`
- `dashboard`
- `health`

## Response envelope

Backend dùng envelope thống nhất:

```json
{
  "success": true,
  "code": "20000",
  "message": "Thành công",
  "data": {},
  "timestamp": "2026-06-03T10:00:00"
}
```

Khi lỗi validation:

```json
{
  "success": false,
  "code": "40001",
  "message": "Validation error",
  "errors": {
    "fieldName": "must not be blank"
  },
  "timestamp": "2026-06-03T10:00:00"
}
```

## Cập nhật OpenAPI file

Khi thay đổi controller, DTO hoặc contract API:

```powershell
cd backend
mvn test -Dtest=SwaggerGeneratorTest -DgenerateSwagger=true
```

Sau đó commit lại `docs/api/openapi.json`.

## Lưu ý hiện tại

- Frontend demo hiện không mở route `/register`; backend vẫn chưa có endpoint register trong OpenAPI hiện tại.
- Ngoài dashboard stats/latency, backend đã có incident API riêng cho list/detail/filter cơ bản.
