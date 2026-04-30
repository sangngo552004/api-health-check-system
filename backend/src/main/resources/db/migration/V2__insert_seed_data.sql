-- Nạp dữ liệu mẫu cho Google Search
INSERT INTO monitored_endpoints (name, url, method, environment, check_type, expected_status_code, is_active)
VALUES ('Google Search', 'https://www.google.com', 'GET', 'Production', 'HTTP', 200, true);

INSERT INTO check_policies (endpoint_id, interval_seconds, timeout_millis, retry_count, failure_threshold, latency_threshold_millis)
VALUES ((SELECT MAX(id) FROM monitored_endpoints), 30, 5000, 3, 3, 2000);

-- Nạp dữ liệu mẫu cho Local Actuator Health
INSERT INTO monitored_endpoints (name, url, method, environment, check_type, expected_status_code, is_active)
VALUES ('Local Actuator Health', 'http://localhost:8080/actuator/health', 'GET', 'Development', 'HTTP', 200, true);

INSERT INTO check_policies (endpoint_id, interval_seconds, timeout_millis, retry_count, failure_threshold, latency_threshold_millis)
VALUES ((SELECT MAX(id) FROM monitored_endpoints), 15, 2000, 2, 2, 1000);
