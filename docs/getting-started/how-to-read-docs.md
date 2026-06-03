# How to Read This Documentation

Tài liệu này dành cho thành viên mới của nhóm, người viết báo cáo, hoặc chính bạn trong tương lai khi quay lại dự án sau một thời gian.

## Vì sao cần file này

Bộ docs hiện đã khá đầy đủ và sâu, nên nếu đọc không đúng hướng, người mới rất dễ:

- thấy nhiều file nhưng không biết bắt đầu từ đâu
- đọc kiến trúc trước khi hiểu nghiệp vụ
- đọc API trước khi hiểu bối cảnh
- đọc code quality trước khi hiểu dự án đang làm tới đâu

File này giúp người đọc chọn đúng lộ trình.

## Đọc theo mục tiêu

### 1. Tôi là đồng đội mới, tôi cần hiểu dự án nhanh

1. [Project Overview](../business/project-overview.md)
2. [Business Requirements](../business/business-requirements.md)
3. [Glossary](../business/glossary.md)
4. [Local Development](local-development.md)
5. [Architecture](../architecture/architecture.md)
6. [Clean Architecture Implementation Guide](../architecture/clean-architecture-implementation.md)
7. [Sequence Flows](../business/sequence-flows.md)
8. [Progress Checklist](../project/progress-checklist.md)
9. [Backend](../engineering/backend.md)
10. [Frontend](../engineering/frontend.md)

### 2. Tôi học môn kiến trúc và design pattern, tôi cần phần phân tích sâu

1. [Architecture](../architecture/architecture.md)
2. [Architecture and Design Patterns](../architecture/architecture-and-patterns.md)
3. [Clean Architecture Implementation Guide](../architecture/clean-architecture-implementation.md)
4. [Architecture Decisions](../architecture/architecture-decisions.md)
5. [Layer Coding Guidelines](../architecture/layer-coding-guidelines.md)
6. [Database Design](../architecture/database-design.md)
7. [Backend](../engineering/backend.md)
8. [Code Quality and Delivery Controls](../engineering/code-quality.md)
9. [Testing and Quality](../engineering/testing-and-quality.md)
10. [Project Status](../project/project-status.md)

### 3. Tôi cần viết báo cáo nghiệp vụ hoặc thuyết trình

1. [Project Overview](../business/project-overview.md)
2. [Business Requirements](../business/business-requirements.md)
3. [Glossary](../business/glossary.md)
4. [Sequence Flows](../business/sequence-flows.md)
5. [Architecture and Design Patterns](../architecture/architecture-and-patterns.md)
6. [Database Design](../architecture/database-design.md)
7. [Roadmap](../project/roadmap.md)
8. [Test Case Catalog](../engineering/test-case-catalog.md)

### 4. Tôi cần quay lại code sau một thời gian

1. [Project Status](../project/project-status.md)
2. [Progress Checklist](../project/progress-checklist.md)
3. [Roadmap](../project/roadmap.md)
4. [AI Agent Guide](../engineering/ai-agent-guide.md)

## Cách hiểu từng nhóm tài liệu

### Nhóm 1: Nghiệp vụ

- [Project Overview](../business/project-overview.md)
- [Business Requirements](../business/business-requirements.md)
- [Glossary](../business/glossary.md)
- [Sequence Flows](../business/sequence-flows.md)

### Nhóm 2: Kiến trúc

- [Architecture](../architecture/architecture.md)
- [Architecture and Design Patterns](../architecture/architecture-and-patterns.md)
- [Clean Architecture Implementation Guide](../architecture/clean-architecture-implementation.md)
- [Architecture Decisions](../architecture/architecture-decisions.md)
- [Layer Coding Guidelines](../architecture/layer-coding-guidelines.md)
- [Database Design](../architecture/database-design.md)

### Nhóm 3: Kỹ thuật triển khai và chất lượng

- [Local Development](local-development.md)
- [Backend](../engineering/backend.md)
- [Frontend](../engineering/frontend.md)
- [API](../api/README.md)
- [Code Quality and Delivery Controls](../engineering/code-quality.md)
- [Testing and Quality](../engineering/testing-and-quality.md)
- [Test Case Catalog](../engineering/test-case-catalog.md)
- [AI Agent Guide](../engineering/ai-agent-guide.md)

### Nhóm 4: Trạng thái và định hướng

- [Project Status](../project/project-status.md)
- [Progress Checklist](../project/progress-checklist.md)
- [Roadmap](../project/roadmap.md)

## Quy ước đọc tài liệu trong dự án này

- `implemented`: đã có code chạy được trong repo
- `partial`: đã có một phần nhưng chưa hoàn chỉnh
- `placeholder`: đã có khung hoặc ý tưởng nhưng chưa có luồng thật
- `planned`: định hướng tương lai, chưa làm

## Khi nào nên nghi ngờ tài liệu

Thứ tự ưu tiên sự thật của dự án là:

1. code hiện tại
2. OpenAPI hiện tại
3. tài liệu mô tả

Nếu thấy:

- frontend có route nhưng backend chưa có API
- tài liệu nói đã xong nhưng `progress checklist` vẫn để partial
- migration/schema không còn khớp mô tả cũ

thì nên đối chiếu lại code và cập nhật docs.
