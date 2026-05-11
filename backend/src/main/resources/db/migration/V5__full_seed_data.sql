-- V5: Seed data phong phú cho toàn bộ hệ thống

-- 1. Nạp Users
INSERT INTO users (username, email, phone_number, password_hash, role, is_active)
VALUES 
('admin', 'admin@healthcheck.com', '0912345678', '$2a$10$EblZqNptyYvcLm/VwDCVAuAw5QkP3XFk0k0y/xZ2A.M.zH.pT/R0S', 'ADMIN', true),
('viewer', 'viewer@healthcheck.com', '0987654321', '$2a$10$EblZqNptyYvcLm/VwDCVAuAw5QkP3XFk0k0y/xZ2A.M.zH.pT/R0S', 'VIEWER', true);

-- 2. Nạp Contact Groups
INSERT INTO contact_groups (name, description, is_active, created_by)
VALUES 
('DevOps Team', 'Nhóm kỹ sư vận hành chính', true, 1),
('Security Team', 'Nhóm chuyên gia bảo mật', true, 1),
('Manager Group', 'Nhóm quản lý nhận báo cáo tuần', true, 1);

-- Liên kết User vào Contact Groups
INSERT INTO contact_group_users (contact_group_id, user_id) VALUES (1, 1), (2, 1), (3, 2);

-- Thêm Email/Webhook cho Contact Groups
INSERT INTO contact_group_emails (contact_group_id, email_address) VALUES (1, 'devops@cty.com'), (1, 'oncall@cty.com'), (2, 'sec@cty.com');
INSERT INTO contact_group_webhooks (contact_group_id, webhook_url) VALUES (1, 'https://slack.com/webhook/devops'), (2, 'https://discord.com/webhook/security');

-- 3. Nạp Check Policies (Templates)
INSERT INTO check_policies (name, interval_seconds, timeout_millis, retry_count, failure_threshold, latency_threshold_millis, expected_status_code, created_by)
VALUES 
('Standard HTTP', 60, 5000, 3, 3, 2000, 200, 1),
('Critical API', 15, 2000, 2, 2, 500, 200, 1),
('Slow Resource', 300, 10000, 1, 5, 5000, 200, 1);

-- 4. Nạp Alert Rules (Templates)
INSERT INTO alert_rules (name, rule_type, operator, threshold_value, is_active, override_default_contacts, created_by)
VALUES 
('High Latency Alert', 'LATENCY', 'GT', 2000.0, true, false, 1),
('API Down Alert', 'STATUS', 'NE', 200.0, true, false, 1),
('Custom Body Error', 'BODY', 'EQ', 0.0, true, true, 1);

-- Liên kết Alert Rules với Contact Groups
INSERT INTO alert_rule_contact_groups (alert_rule_id, contact_group_id) VALUES (1, 1), (2, 1), (3, 2);

-- 5. Nạp Monitored Endpoints
INSERT INTO monitored_endpoints (name, url, method, environment, check_type, status, policy_id, is_active, created_by)
VALUES 
('Production Gateway', 'https://api.cty.com/v1/health', 'GET', 'Production', 'HTTP', 'UP', 2, true, 1),
('Auth Service', 'https://auth.cty.com/login', 'POST', 'Production', 'HTTP', 'UP', 2, true, 1),
('Staging Database UI', 'https://staging-db.cty.com', 'GET', 'Staging', 'HTTP', 'DEGRADED', 1, true, 1);

-- Thêm Headers cho Endpoints
INSERT INTO endpoint_headers (endpoint_id, header_key, header_value) VALUES (2, 'Content-Type', 'application/json'), (2, 'User-Agent', 'HealthCheckBot/1.0');

-- Thêm Tags cho Endpoints
INSERT INTO endpoint_tags (endpoint_id, tag) VALUES (1, 'core'), (1, 'public'), (2, 'auth'), (2, 'security');

-- Liên kết Endpoints với Alert Rules và Contact Groups
INSERT INTO endpoint_alert_rules (endpoint_id, alert_rule_id) VALUES (1, 1), (1, 2), (2, 2);
INSERT INTO endpoint_contact_groups (endpoint_id, contact_group_id) VALUES (1, 1), (2, 1), (3, 3);
