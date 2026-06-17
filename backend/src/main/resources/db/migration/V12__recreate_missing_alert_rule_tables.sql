CREATE TABLE IF NOT EXISTS alert_rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    operator VARCHAR(20) NOT NULL,
    threshold_value DOUBLE PRECISION,
    is_active BOOLEAN DEFAULT true,
    override_default_contacts BOOLEAN DEFAULT false,
    created_by BIGINT,
    workspace_id BIGINT NOT NULL REFERENCES workspaces(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS alert_rule_contact_groups (
    alert_rule_id BIGINT NOT NULL REFERENCES alert_rules(id) ON DELETE CASCADE,
    contact_group_id BIGINT NOT NULL REFERENCES contact_groups(id) ON DELETE CASCADE,
    PRIMARY KEY (alert_rule_id, contact_group_id)
);

CREATE INDEX IF NOT EXISTS idx_alert_rule_workspace ON alert_rules(workspace_id);
