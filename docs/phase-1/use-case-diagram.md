# Use Case Diagram

```mermaid
flowchart LR
    Admin[Quan tri vien]
    Viewer[Nguoi xem]
    Scheduler[Scheduler he thong]

    UC1((Tao endpoint))
    UC2((Cap nhat endpoint))
    UC3((Xem danh sach endpoint))
    UC4((Xem dashboard))
    UC5((Chay health check theo lich))
    UC6((Ghi nhan incident))
    UC7((Dong incident khi phuc hoi))
    UC8((Gui canh bao))

    Admin --> UC1
    Admin --> UC2
    Admin --> UC3
    Admin --> UC4

    Viewer --> UC3
    Viewer --> UC4

    Scheduler --> UC5
    Scheduler --> UC6
    Scheduler --> UC7
    Scheduler --> UC8

    UC5 --> UC6
    UC5 --> UC7
    UC6 --> UC8
```

## Ghi chú

- Phiên bản đầu ưu tiên `HTTP health check`.
- Incident được tạo sau khi lỗi liên tiếp đạt ngưỡng.
- Cảnh báo có thể được gửi khi mở incident hoặc khi phát hiện độ trễ bất thường.
