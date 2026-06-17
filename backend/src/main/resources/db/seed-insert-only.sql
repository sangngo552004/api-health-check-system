BEGIN;

-- Demo password for all accounts: password123
INSERT INTO users (
    id,
    username,
    email,
    phone_number,
    password_hash,
    role,
    is_active,
    requires_password_change,
    created_at,
    updated_at
) VALUES
    (1, 'admin', 'admin@healthcheck.com', '0912345678', '$2a$10$ZCXXhbBA0TwLjSEg7fbK1ugs5CbNy2C2PvUCurCQ6rZY6ZbGyhKri', 'SUPER_ADMIN', true, false, NOW(), NOW()),
    (2, 'viewer', 'viewer@healthcheck.com', '0987654321', '$2a$10$ZCXXhbBA0TwLjSEg7fbK1ugs5CbNy2C2PvUCurCQ6rZY6ZbGyhKri', 'USER', true, false, NOW(), NOW()),
    (3, 'opslead', 'opslead@healthcheck.com', '0900000001', '$2a$10$ZCXXhbBA0TwLjSEg7fbK1ugs5CbNy2C2PvUCurCQ6rZY6ZbGyhKri', 'USER', true, false, NOW(), NOW());

INSERT INTO workspaces (
    id,
    name,
    description,
    slug,
    owner_id,
    is_active,
    created_at,
    updated_at
) VALUES
    (1, 'Default Workspace', 'Workspace demo cho local development', 'default-workspace', 1, true, NOW(), NOW()),
    (2, 'Payment Platform', 'Workspace theo doi he thong thanh toan va callback', 'payment-platform', 1, true, NOW(), NOW());

INSERT INTO workspace_members (
    workspace_id,
    user_id,
    joined_at
) VALUES
    (1, 1, NOW()),
    (1, 2, NOW()),
    (1, 3, NOW()),
    (2, 1, NOW()),
    (2, 3, NOW());

INSERT INTO contact_groups (
    id,
    name,
    description,
    is_active,
    created_by,
    workspace_id,
    created_at,
    updated_at
) VALUES
    (1, 'DevOps Team', 'Nhan canh bao van hanh cho workspace mac dinh', true, 1, 1, NOW(), NOW()),
    (2, 'Backend Team', 'Nhan canh bao API va auth', true, 1, 1, NOW(), NOW()),
    (3, 'Payment On-call', 'Nhan su co lien quan thanh toan', true, 1, 2, NOW(), NOW());

INSERT INTO contact_group_users (contact_group_id, user_id) VALUES
    (1, 1),
    (1, 3),
    (2, 2),
    (3, 1),
    (3, 3);

INSERT INTO contact_group_emails (contact_group_id, email_address) VALUES
    (1, 'devops@healthcheck.local'),
    (2, 'backend@healthcheck.local'),
    (3, 'payments-oncall@healthcheck.local');

INSERT INTO contact_group_webhooks (contact_group_id, webhook_url) VALUES
    (1, 'https://example.com/webhooks/devops'),
    (3, 'https://example.com/webhooks/payment');

INSERT INTO check_policies (
    id,
    name,
    interval_seconds,
    timeout_millis,
    retry_count,
    failure_threshold,
    latency_threshold_millis,
    expected_status_code,
    expected_response_body,
    response_regex,
    created_by,
    workspace_id,
    created_at,
    updated_at
) VALUES
    (1, 'Standard HTTP', 60, 5000, 2, 3, 2000, 200, NULL, NULL, 1, 1, NOW(), NOW()),
    (2, 'Critical Auth API', 30, 3000, 2, 2, 1000, 200, 'UP', NULL, 1, 1, NOW(), NOW()),
    (3, 'Payment Callback', 20, 2500, 1, 2, 800, 200, 'OK', NULL, 1, 2, NOW(), NOW()),
    (4, 'TCP Port Check', 60, 2000, 1, 3, 500, NULL, NULL, NULL, 1, 2, NOW(), NOW());

INSERT INTO alert_rules (
    id,
    name,
    rule_type,
    operator,
    threshold_value,
    is_active,
    override_default_contacts,
    created_by,
    workspace_id,
    created_at,
    updated_at
) VALUES
    (1, 'Latency Warning', 'RESPONSE_TIME_EXCEEDED', 'GT', 1500, true, false, 1, 1, NOW(), NOW()),
    (2, 'Consecutive Failures', 'CONSECUTIVE_FAILURE', 'GT', 2, true, false, 1, 1, NOW(), NOW()),
    (3, 'Status Code Mismatch', 'STATUS_CODE_MISMATCH', 'NE', 200, true, true, 1, 2, NOW(), NOW());

INSERT INTO alert_rule_contact_groups (alert_rule_id, contact_group_id) VALUES
    (1, 1),
    (2, 2),
    (3, 3);

INSERT INTO monitored_endpoints (
    id,
    name,
    url,
    method,
    environment,
    check_type,
    workspace_id,
    is_active,
    status,
    policy_id,
    request_body,
    created_by,
    last_checked_at,
    created_at,
    updated_at
) VALUES
    (1, 'Demo Service Health', 'http://localhost:8086/actuator/health', 'GET', 'Development', 'HTTP', 1, true, 'UP', 1, NULL, 1, NOW() - INTERVAL '2 minutes', NOW(), NOW()),
    (2, 'Auth API', 'http://localhost:8080/api/v1/health', 'GET', 'Local', 'HTTP', 1, true, 'DEGRADED', 2, NULL, 1, NOW() - INTERVAL '1 minute', NOW(), NOW()),
    (3, 'Payment Callback API', 'https://payment.example.com/callback/health', 'POST', 'Production', 'HTTP', 2, true, 'DOWN', 3, '{\"source\":\"seed\"}', 1, NOW() - INTERVAL '30 seconds', NOW(), NOW()),
    (4, 'Redis TCP', 'tcp://redis.internal:6379', 'GET', 'Production', 'TCP', 2, true, 'UP', 4, NULL, 1, NOW() - INTERVAL '3 minutes', NOW(), NOW());

INSERT INTO endpoint_headers (endpoint_id, header_key, header_value) VALUES
    (3, 'Content-Type', 'application/json'),
    (3, 'X-Demo-Source', 'seed');

INSERT INTO endpoint_tags (endpoint_id, tag) VALUES
    (1, 'demo'),
    (1, 'actuator'),
    (2, 'auth'),
    (2, 'backend'),
    (3, 'payment'),
    (3, 'critical'),
    (4, 'tcp');

INSERT INTO endpoint_alert_rules (endpoint_id, alert_rule_id) VALUES
    (1, 1),
    (2, 1),
    (2, 2),
    (3, 3);

INSERT INTO endpoint_contact_groups (endpoint_id, contact_group_id) VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 3);

INSERT INTO health_check_results (
    id,
    endpoint_id,
    workspace_id,
    checked_at,
    status,
    http_status_code,
    response_time_millis,
    error_message,
    response_payload,
    node_id,
    success,
    created_at,
    updated_at
) VALUES
    (1, 1, 1, NOW() - INTERVAL '12 minutes', 'UP', 200, 180, NULL, '{"status":"UP"}', 'local', true, NOW(), NOW()),
    (2, 1, 1, NOW() - INTERVAL '6 minutes', 'UP', 200, 205, NULL, '{"status":"UP"}', 'local', true, NOW(), NOW()),
    (3, 2, 1, NOW() - INTERVAL '10 minutes', 'UP', 200, 650, NULL, '{"status":"UP"}', 'local', true, NOW(), NOW()),
    (4, 2, 1, NOW() - INTERVAL '4 minutes', 'DEGRADED', 200, 1800, 'High latency: 1800ms', '{"status":"UP"}', 'local', true, NOW(), NOW()),
    (5, 3, 2, NOW() - INTERVAL '8 minutes', 'DOWN', 500, 320, 'Status code mismatch. Expected: 200, Actual: 500', '{"status":"ERROR"}', 'local', false, NOW(), NOW()),
    (6, 3, 2, NOW() - INTERVAL '4 minutes', 'DOWN', 502, 410, 'Status code mismatch. Expected: 200, Actual: 502', '{"status":"BAD_GATEWAY"}', 'local', false, NOW(), NOW()),
    (7, 3, 2, NOW() - INTERVAL '1 minute', 'DOWN', 503, 430, 'Status code mismatch. Expected: 200, Actual: 503', '{"status":"UNAVAILABLE"}', 'local', false, NOW(), NOW()),
    (8, 4, 2, NOW() - INTERVAL '5 minutes', 'UP', NULL, 45, NULL, NULL, 'local', true, NOW(), NOW());

INSERT INTO incidents (
    id,
    endpoint_id,
    workspace_id,
    started_at,
    resolved_at,
    status,
    reason,
    failure_count,
    severity,
    root_cause,
    created_at,
    updated_at
) VALUES
    (1, 3, 2, NOW() - INTERVAL '5 minutes', NULL, 'OPEN', 'Endpoint that bai lien tiep 3 lan (nguong: 2).', 3, 'WARNING', 'Demo incident do seed tao san de test dashboard.', NOW(), NOW());

INSERT INTO incident_failing_results (incident_id, result_id) VALUES
    (1, 5),
    (1, 6),
    (1, 7);

SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE((SELECT MAX(id) FROM users), 1), true);
SELECT setval(pg_get_serial_sequence('workspaces', 'id'), COALESCE((SELECT MAX(id) FROM workspaces), 1), true);
SELECT setval(pg_get_serial_sequence('contact_groups', 'id'), COALESCE((SELECT MAX(id) FROM contact_groups), 1), true);
SELECT setval(pg_get_serial_sequence('check_policies', 'id'), COALESCE((SELECT MAX(id) FROM check_policies), 1), true);
SELECT setval(pg_get_serial_sequence('alert_rules', 'id'), COALESCE((SELECT MAX(id) FROM alert_rules), 1), true);
SELECT setval(pg_get_serial_sequence('monitored_endpoints', 'id'), COALESCE((SELECT MAX(id) FROM monitored_endpoints), 1), true);
SELECT setval(pg_get_serial_sequence('health_check_results', 'id'), COALESCE((SELECT MAX(id) FROM health_check_results), 1), true);
SELECT setval(pg_get_serial_sequence('incidents', 'id'), COALESCE((SELECT MAX(id) FROM incidents), 1), true);
SELECT setval(pg_get_serial_sequence('notifications', 'id'), 1, false);
SELECT setval(pg_get_serial_sequence('refresh_tokens', 'id'), 1, false);

COMMIT;
