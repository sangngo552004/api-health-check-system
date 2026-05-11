-- V8: Hạ tầng Workspace và Phân quyền (Multi-tenancy)

-- 1. Tạo bảng workspaces
CREATE TABLE IF NOT EXISTS workspaces (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    slug VARCHAR(255) UNIQUE NOT NULL,
    owner_id BIGINT NOT NULL REFERENCES users(id),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tạo bảng workspace_members (N-N giữa User và Workspace)
CREATE TABLE IF NOT EXISTS workspace_members (
    workspace_id BIGINT NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL, -- ADMIN, MEMBER
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (workspace_id, user_id)
);

-- 3. Tạo một Default Workspace cho dữ liệu cũ (Seed data cũ)
-- Giả định admin user có id = 1
INSERT INTO workspaces (name, description, slug, owner_id)
VALUES ('Default Workspace', 'Workspace mặc định cho hệ thống', 'default-workspace', 1);

-- Thêm admin vào làm Workspace Admin của Default Workspace
INSERT INTO workspace_members (workspace_id, user_id, role)
VALUES (1, 1, 'ADMIN');

-- 4. Bổ sung workspace_id vào các bảng hiện có
ALTER TABLE monitored_endpoints ADD COLUMN workspace_id BIGINT REFERENCES workspaces(id);
ALTER TABLE check_policies ADD COLUMN workspace_id BIGINT REFERENCES workspaces(id);
ALTER TABLE alert_rules ADD COLUMN workspace_id BIGINT REFERENCES workspaces(id);
ALTER TABLE contact_groups ADD COLUMN workspace_id BIGINT REFERENCES workspaces(id);
ALTER TABLE health_check_results ADD COLUMN workspace_id BIGINT REFERENCES workspaces(id);
ALTER TABLE incidents ADD COLUMN workspace_id BIGINT REFERENCES workspaces(id);

-- Gán dữ liệu hiện có vào Default Workspace (id = 1)
UPDATE monitored_endpoints SET workspace_id = 1;
UPDATE check_policies SET workspace_id = 1;
UPDATE alert_rules SET workspace_id = 1;
UPDATE contact_groups SET workspace_id = 1;
UPDATE health_check_results SET workspace_id = 1;
UPDATE incidents SET workspace_id = 1;

-- Sau khi update dữ liệu, đặt ràng buộc NOT NULL
ALTER TABLE monitored_endpoints ALTER COLUMN workspace_id SET NOT NULL;
ALTER TABLE check_policies ALTER COLUMN workspace_id SET NOT NULL;
ALTER TABLE alert_rules ALTER COLUMN workspace_id SET NOT NULL;
ALTER TABLE contact_groups ALTER COLUMN workspace_id SET NOT NULL;
ALTER TABLE health_check_results ALTER COLUMN workspace_id SET NOT NULL;
ALTER TABLE incidents ALTER COLUMN workspace_id SET NOT NULL;

-- 5. Đánh Index để tối ưu truy vấn theo Workspace
CREATE INDEX idx_endpoint_workspace ON monitored_endpoints(workspace_id);
CREATE INDEX idx_policy_workspace ON check_policies(workspace_id);
CREATE INDEX idx_rule_workspace ON alert_rules(workspace_id);
CREATE INDEX idx_group_workspace ON contact_groups(workspace_id);
CREATE INDEX idx_result_workspace ON health_check_results(workspace_id);
CREATE INDEX idx_incident_workspace ON incidents(workspace_id);
