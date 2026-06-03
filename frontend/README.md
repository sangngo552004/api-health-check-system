# Frontend

Frontend là dashboard React + Vite cho luồng đăng nhập, chọn workspace, xem dashboard và thao tác CRUD với các tài nguyên giám sát chính.

## Chạy cục bộ

```powershell
cd frontend
npm install
npm run dev
```

Mặc định ứng dụng dev chạy qua Vite và gọi backend tại `http://localhost:8080/api/v1`.

## Thành phần chính

- `context`: auth, workspace, theme.
- `routes`: định nghĩa public/private route.
- `features`: dashboard, endpoint, policy, alert, contact, member, auth.
- `store`: Zustand store theo từng feature.
- `services`: API client và các module gọi REST API.

## Tài liệu chi tiết

- [Thiết lập local](../docs/getting-started/local-development.md)
- [Tài liệu frontend](../docs/engineering/frontend.md)
- [API và OpenAPI](../docs/api/README.md)
