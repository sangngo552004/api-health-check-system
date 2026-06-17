# Documentation Index

Đây là điểm vào chính của toàn bộ tài liệu dự án. Bộ docs đã được chia lại thành các nhóm rõ ràng để người mới dễ đọc hơn, đồng thời tiện dùng cho code hằng ngày, onboarding, AI agent và báo cáo học phần.

## Cấu trúc thư mục

- `getting-started/`: cách đọc docs, setup local, onboarding nhanh
- `getting-started/demo-and-deploy.md`: checklist demo thi và deploy online tối thiểu
- `getting-started/demo-runbook.md`: kịch bản demo 5-7 phút và đường lui local
- `business/`: bối cảnh bài toán, yêu cầu nghiệp vụ, thuật ngữ, sequence flow
- `architecture/`: clean architecture, design pattern, decision log, database design, guideline theo layer
- `engineering/`: backend, frontend, code quality, testcase, AI agent guide
- `project/`: trạng thái dự án, checklist tiến độ, roadmap
- `api/`: OpenAPI và tài liệu contract API

## Mục tiêu của bộ docs

- giúp thành viên mới của nhóm hiểu nhanh dự án
- làm rõ phần hay nhất của dự án là `Clean Architecture` và `design pattern`
- hỗ trợ viết báo cáo, thuyết trình và bảo vệ đồ án
- giúp quay lại code sau một thời gian mà không mất ngữ cảnh
- giúp AI agent code đúng convention của repo

## Cách đọc docs theo vai trò

### Nếu bạn là thành viên mới của nhóm

1. [How to Read This Documentation](getting-started/how-to-read-docs.md)
2. [Project Overview](business/project-overview.md)
3. [Business Requirements](business/business-requirements.md)
4. [Local Development](getting-started/local-development.md)
5. [Demo and Deploy](getting-started/demo-and-deploy.md)
6. [Architecture](architecture/architecture.md)
7. [Clean Architecture Implementation Guide](architecture/clean-architecture-implementation.md)
8. [Sequence Flows](business/sequence-flows.md)
9. [Progress Checklist](project/progress-checklist.md)

### Nếu bạn cần tập trung vào kiến trúc và design pattern

1. [Architecture](architecture/architecture.md)
2. [Architecture and Design Patterns](architecture/architecture-and-patterns.md)
3. [Clean Architecture Implementation Guide](architecture/clean-architecture-implementation.md)
4. [Architecture Decisions](architecture/architecture-decisions.md)
5. [Layer Coding Guidelines](architecture/layer-coding-guidelines.md)
6. [Database Design](architecture/database-design.md)
7. [Backend](engineering/backend.md)
8. [Code Quality and Delivery Controls](engineering/code-quality.md)
9. [Testing and Quality](engineering/testing-and-quality.md)

### Nếu bạn cần viết báo cáo hoặc phần nghiệp vụ

1. [Project Overview](business/project-overview.md)
2. [Business Requirements](business/business-requirements.md)
3. [Glossary](business/glossary.md)
4. [Sequence Flows](business/sequence-flows.md)
5. [Database Design](architecture/database-design.md)
6. [Project Status](project/project-status.md)
7. [UI Screenshot Guide](business/ui-screenshot-guide.md)

### Nếu bạn cần quay lại code hoặc quản lý tiến độ cá nhân

1. [Project Status](project/project-status.md)
2. [Progress Checklist](project/progress-checklist.md)
3. [Roadmap](project/roadmap.md)
4. [Test Case Catalog](engineering/test-case-catalog.md)

### Nếu bạn muốn AI agent code đúng convention của dự án

1. [AI Agent Guide](engineering/ai-agent-guide.md)
2. [Layer Coding Guidelines](architecture/layer-coding-guidelines.md)
3. [Clean Architecture Implementation Guide](architecture/clean-architecture-implementation.md)
4. [Code Quality and Delivery Controls](engineering/code-quality.md)
5. [Project Status](project/project-status.md)
6. [Progress Checklist](project/progress-checklist.md)

## Nên đọc theo thứ tự này nếu muốn đi từ đầu tới cuối

1. [Project Overview](business/project-overview.md)
2. [How to Read This Documentation](getting-started/how-to-read-docs.md)
3. [Business Requirements](business/business-requirements.md)
4. [Glossary](business/glossary.md)
5. [Local Development](getting-started/local-development.md)
6. [Demo and Deploy](getting-started/demo-and-deploy.md)
7. [Architecture](architecture/architecture.md)
8. [Architecture and Design Patterns](architecture/architecture-and-patterns.md)
9. [Clean Architecture Implementation Guide](architecture/clean-architecture-implementation.md)
10. [Architecture Decisions](architecture/architecture-decisions.md)
11. [Layer Coding Guidelines](architecture/layer-coding-guidelines.md)
12. [Database Design](architecture/database-design.md)
13. [Backend](engineering/backend.md)
14. [Frontend](engineering/frontend.md)
15. [Sequence Flows](business/sequence-flows.md)
16. [API](api/README.md)
17. [Code Quality and Delivery Controls](engineering/code-quality.md)
18. [Testing and Quality](engineering/testing-and-quality.md)
19. [Test Case Catalog](engineering/test-case-catalog.md)
20. [AI Agent Guide](engineering/ai-agent-guide.md)
21. [Project Status](project/project-status.md)
22. [Progress Checklist](project/progress-checklist.md)
23. [Roadmap](project/roadmap.md)
24. [UI Screenshot Guide](business/ui-screenshot-guide.md)

## Nguyên tắc tài liệu

- Tài liệu phải phản ánh đúng code đang có trong repo.
- Tính năng chưa hoàn chỉnh phải được ghi rõ là `partial`, `placeholder` hoặc `planned`.
- OpenAPI trong [api/openapi.json](api/openapi.json) là artifact sinh tự động, không chỉnh tay.
- Khi thêm tính năng mới, ưu tiên cập nhật file gần nhất với thay đổi thay vì tạo thêm tài liệu rời.
- Tài liệu hướng kiến trúc, BA và quality engineering có thể rất chi tiết, nhưng không được mâu thuẫn với code hiện tại.
