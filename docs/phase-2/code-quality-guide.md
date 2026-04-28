# Hướng dẫn kiểm tra chất lượng code local

## 1. Mục tiêu

Repo này ưu tiên bắt lỗi ngay trên máy local trước khi code được commit. Cách làm này phù hợp với giai đoạn đầu của đồ án vì:

- setup nhanh hơn CI
- phản hồi sớm cho người viết code
- giảm xác suất đẩy code hỏng lên remote

## 2. Kiểm tra backend

Backend hiện dùng hai lớp kiểm tra chính:

### Chạy test

Lệnh:

```powershell
cd backend
mvn test
```

Ý nghĩa:

- xác nhận ứng dụng khởi động được
- xác nhận cấu hình test đang dùng được

### Kiểm tra ranh giới kiến trúc

Backend dùng `ArchUnit` để kiểm tra ranh giới package của `Clean Architecture`.

Mục tiêu của rule hiện tại:

- `delivery` không bị lớp khác gọi ngược vào
- `application` chỉ là lớp trung gian để delivery và infrastructure đi qua
- `domain` không kéo phụ thuộc sai hướng
- `infrastructure` không bị các lớp khác phụ thuộc ngược vào như một lõi nghiệp vụ

Lưu ý:

- Rule package chỉ là hàng rào đầu tiên.
- Review code vẫn cần kiểm tra ngữ nghĩa nghiệp vụ.

## 3. Kiểm tra frontend

Frontend hiện dùng hai lớp kiểm tra chính:

### Chạy lint

Lệnh:

```powershell
cd frontend
npm run lint
```

Ý nghĩa:

- bắt lỗi biến không dùng
- bắt một số lỗi phổ biến trong React hook
- giữ code nhất quán hơn trong team

### Chạy build

Lệnh:

```powershell
cd frontend
npm run build
```

Ý nghĩa:

- kiểm tra TypeScript
- kiểm tra config Vite
- xác nhận mã hiện tại build production được

## 4. Git hook local

Repo có hỗ trợ chạy kiểm tra tự động theo sự kiện Git.

### Cài hook

Chạy một lần:

```powershell
pwsh -File scripts/install-git-hooks.ps1
```

### pre-commit

Khi commit, hook sẽ chạy:

- `frontend`: `npm run lint`
- `backend`: `mvn test`

Hiện tại repo chỉ giữ `pre-commit` để tránh chạy lặp lại cùng một bộ kiểm tra ở cả commit và push.

## 5. Khi nào nên thêm rule mới

Nên thêm rule khi:

- team lặp lại cùng một lỗi nhiều lần
- có một quyết định kiến trúc cần được bảo vệ tự động
- review thủ công đang tốn thời gian cho lỗi máy móc

Không nên thêm rule chỉ để “siết cho chặt” nếu nó không giúp chất lượng thật hoặc làm team quá chậm.

## 6. Nguyên tắc giữ code sạch

### Backend

- Một lớp chỉ nên có một trách nhiệm chính.
- Business rule không đặt ở controller hay adapter.
- Interface port phải nói rõ ý nghĩa nghiệp vụ.
- Tên use case nên dùng động từ rõ ràng như `CreateEndpointUseCase`, `RunHealthCheckUseCase`.

### Frontend

- Không để page vừa fetch data, vừa format phức tạp, vừa ôm quá nhiều UI con.
- Tách component dùng lại được khỏi feature đặc thù.
- Tránh utility “thập cẩm” không rõ mục đích.
- Ưu tiên type rõ ràng thay vì `any`.

## 7. Mức kiểm tra hiện tại và hướng mở rộng

Hiện tại bộ kiểm tra local đủ tốt cho phase 2 và đầu phase 3. Sau này có thể mở rộng thêm:

- backend: naming rule, package rule chi tiết hơn, mutation test hoặc integration test
- frontend: formatter, test component, accessibility lint, import order
