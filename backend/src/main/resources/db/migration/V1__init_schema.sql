CREATE TABLE monitored_endpoints (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(1024) NOT NULL,
    method VARCHAR(20) NOT NULL,
    environment VARCHAR(50),
    check_type VARCHAR(50) NOT NULL,
    expected_status_code INTEGER,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE check_policies (
    id BIGSERIAL PRIMARY KEY,
    endpoint_id BIGINT NOT NULL,
    interval_seconds INTEGER NOT NULL,
    timeout_millis INTEGER NOT NULL,
    retry_count INTEGER NOT NULL,
    failure_threshold INTEGER NOT NULL,
    latency_threshold_millis INTEGER,
    CONSTRAINT fk_check_policy_endpoint FOREIGN KEY (endpoint_id) REFERENCES monitored_endpoints(id) ON DELETE CASCADE
);

CREATE TABLE health_check_results (
    id BIGSERIAL PRIMARY KEY,
    endpoint_id BIGINT NOT NULL,
    checked_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    http_status_code INTEGER,
    response_time_millis BIGINT,
    error_message TEXT,
    success BOOLEAN NOT NULL,
    CONSTRAINT fk_health_check_result_endpoint FOREIGN KEY (endpoint_id) REFERENCES monitored_endpoints(id) ON DELETE CASCADE
);

CREATE TABLE incidents (
    id BIGSERIAL PRIMARY KEY,
    endpoint_id BIGINT NOT NULL,
    started_at TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    reason TEXT,
    CONSTRAINT fk_incident_endpoint FOREIGN KEY (endpoint_id) REFERENCES monitored_endpoints(id) ON DELETE CASCADE
);
