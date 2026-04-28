# Component Diagram

```mermaid
flowchart TB
    FE[Frontend Dashboard]
    API[Delivery REST API]
    APP[Application Layer]
    DOM[Domain Layer]
    DB[(PostgreSQL)]
    SCHED[Scheduler Adapter]
    HTTP[HTTP Client Adapter]
    NOTI[Notification Adapter]
    EVENT[Event Publisher]

    FE --> API
    API --> APP
    APP --> DOM

    SCHED --> APP
    APP --> EVENT
    APP --> HTTP
    APP --> NOTI
    APP --> DB

    HTTP --> APP
    EVENT --> NOTI
```

## Ghi chú

- `Delivery` chỉ gọi `Application`, không truy cập trực tiếp database.
- `Infrastructure` sẽ chứa triển khai cụ thể cho `Scheduler`, `HTTP client`, `Repository`, `Notification`.
- `Domain` giữ business rule và không phụ thuộc framework.
