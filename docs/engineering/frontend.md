# Frontend

## Công nghệ chính

- `React 19`
- `TypeScript`
- `Vite`
- `React Router 7`
- `Zustand`
- `React Hook Form`
- `Zod`
- `i18next`
- `Recharts`

## Cấu trúc ứng dụng

- `App.tsx`: ghép `BrowserRouter`, `AuthProvider`, `WorkspaceProvider`
- `context/`: quản lý auth, workspace, theme
- `routes/`: định nghĩa public/private routes
- `features/`: UI theo domain
- `store/`: Zustand store cho từng module
- `services/`: API client dùng chung và service theo tài nguyên

## Route hiện có

### Public

- `/login`

### Private

- `/` dashboard overview
- `/endpoints`
- `/policies`
- `/alerts`
- `/contacts`
- `/incidents`
- `/members`

## Auth và session

- Access token lưu in-memory để giảm rủi ro XSS.
- Refresh token lưu trong `localStorage`.
- Khi app khởi động, `AuthContext` thử gọi `/auth/refresh`.
- Nếu API trả `401` hoặc `403`, client phát event `auth-logout` để dọn phiên cục bộ.

## Workspace handling

- `WorkspaceContext` gọi `/workspaces/my` sau khi đăng nhập.
- Workspace đang chọn được lưu ở `localStorage` với key `workspace_id`.
- API client tự gắn `X-Workspace-Id` nếu người dùng đã chọn workspace.

## Những màn hình đã kết nối API

- Dashboard overview
- Endpoints
- Policies
- Alerts
- Contacts
- Members

Các module trên đều đã có API service và Zustand store riêng.

## Những phần còn dang dở

- Demo flow hiện dùng account seed/demo; chưa có register API backend để self-service onboarding.
- `Incidents` đã có màn hình thật cho list/filter/detail cơ bản, nhưng chưa có timeline/audit workflow sâu hơn.
- Một số text placeholder trong UI cũ vẫn có thể cần dọn thêm.
- Chưa có chiến lược refresh token chủ động trước khi access token hết hạn; hiện dựa vào refresh khi app khởi tạo hoặc khi user đăng nhập lại.

## Quy ước tích hợp API

- Base URL mặc định: `http://localhost:8080/api/v1`
- API client unwrap `ApiResponse.data`
- Error code từ backend được map qua `i18n` khi có thể

## Hướng cải thiện gần

- Hoàn thiện onboarding user và register flow.
- Bổ sung incidents screen thật.
- Đồng bộ text/UX của placeholder cũ với trạng thái code hiện tại.
- Chuẩn hóa hơn nữa typing giữa DTO frontend và OpenAPI/backend contracts.
