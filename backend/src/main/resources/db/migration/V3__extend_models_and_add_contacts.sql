-- 1. Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create contact_groups and related tables
CREATE TABLE contact_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contact_group_users (
    contact_group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (contact_group_id, user_id),
    CONSTRAINT fk_cg_users_cg FOREIGN KEY (contact_group_id) REFERENCES contact_groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_cg_users_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE contact_group_emails (
    contact_group_id BIGINT NOT NULL,
    email_address VARCHAR(255) NOT NULL,
    CONSTRAINT fk_cg_emails_cg FOREIGN KEY (contact_group_id) REFERENCES contact_groups(id) ON DELETE CASCADE
);

CREATE TABLE contact_group_webhooks (
    contact_group_id BIGINT NOT NULL,
    webhook_url VARCHAR(1024) NOT NULL,
    CONSTRAINT fk_cg_webhooks_cg FOREIGN KEY (contact_group_id) REFERENCES contact_groups(id) ON DELETE CASCADE
);

-- 3. Create alert_rules and mapping tables
CREATE TABLE alert_rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    operator VARCHAR(20) NOT NULL,
    threshold_value DOUBLE PRECISION,
    is_active BOOLEAN DEFAULT true,
    override_default_contacts BOOLEAN DEFAULT false,
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE alert_rule_contact_groups (
    alert_rule_id BIGINT NOT NULL,
    contact_group_id BIGINT NOT NULL,
    PRIMARY KEY (alert_rule_id, contact_group_id),
    CONSTRAINT fk_arcg_rule FOREIGN KEY (alert_rule_id) REFERENCES alert_rules(id) ON DELETE CASCADE,
    CONSTRAINT fk_arcg_cg FOREIGN KEY (contact_group_id) REFERENCES contact_groups(id) ON DELETE CASCADE
);

-- 4. Update existing tables
-- Update monitored_endpoints with mapping tables
CREATE TABLE endpoint_alert_rules (
    endpoint_id BIGINT NOT NULL,
    alert_rule_id BIGINT NOT NULL,
    PRIMARY KEY (endpoint_id, alert_rule_id),
    CONSTRAINT fk_ear_endpoint FOREIGN KEY (endpoint_id) REFERENCES monitored_endpoints(id) ON DELETE CASCADE,
    CONSTRAINT fk_ear_rule FOREIGN KEY (alert_rule_id) REFERENCES alert_rules(id) ON DELETE CASCADE
);

CREATE TABLE endpoint_contact_groups (
    endpoint_id BIGINT NOT NULL,
    contact_group_id BIGINT NOT NULL,
    PRIMARY KEY (endpoint_id, contact_group_id),
    CONSTRAINT fk_ecg_endpoint FOREIGN KEY (endpoint_id) REFERENCES monitored_endpoints(id) ON DELETE CASCADE,
    CONSTRAINT fk_ecg_cg FOREIGN KEY (contact_group_id) REFERENCES contact_groups(id) ON DELETE CASCADE
);

-- Add columns to check_policies
ALTER TABLE check_policies ADD COLUMN expected_response_body TEXT;
ALTER TABLE check_policies ADD COLUMN response_regex VARCHAR(255);
ALTER TABLE check_policies ADD COLUMN created_by BIGINT;

-- Add nodeId to health_check_results
ALTER TABLE health_check_results ADD COLUMN node_id VARCHAR(50);
