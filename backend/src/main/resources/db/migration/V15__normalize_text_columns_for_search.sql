DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'monitored_endpoints'
          AND column_name = 'name' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE monitored_endpoints
            ALTER COLUMN name TYPE VARCHAR(255) USING convert_from(name, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'monitored_endpoints'
          AND column_name = 'url' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE monitored_endpoints
            ALTER COLUMN url TYPE VARCHAR(1024) USING convert_from(url, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'monitored_endpoints'
          AND column_name = 'environment' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE monitored_endpoints
            ALTER COLUMN environment TYPE VARCHAR(50) USING convert_from(environment, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'monitored_endpoints'
          AND column_name = 'status' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE monitored_endpoints
            ALTER COLUMN status TYPE VARCHAR(50) USING convert_from(status, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'contact_groups'
          AND column_name = 'name' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE contact_groups
            ALTER COLUMN name TYPE VARCHAR(255) USING convert_from(name, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'contact_groups'
          AND column_name = 'description' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE contact_groups
            ALTER COLUMN description TYPE TEXT USING convert_from(description, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'contact_group_emails'
          AND column_name = 'email_address' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE contact_group_emails
            ALTER COLUMN email_address TYPE VARCHAR(255) USING convert_from(email_address, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'check_policies'
          AND column_name = 'name' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE check_policies
            ALTER COLUMN name TYPE VARCHAR(255) USING convert_from(name, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'check_policies'
          AND column_name = 'expected_response_body' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE check_policies
            ALTER COLUMN expected_response_body TYPE TEXT
            USING convert_from(expected_response_body, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'check_policies'
          AND column_name = 'response_regex' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE check_policies
            ALTER COLUMN response_regex TYPE VARCHAR(255) USING convert_from(response_regex, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'alert_rules'
          AND column_name = 'name' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE alert_rules
            ALTER COLUMN name TYPE VARCHAR(255) USING convert_from(name, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'users'
          AND column_name = 'username' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE users
            ALTER COLUMN username TYPE VARCHAR(255) USING convert_from(username, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'users'
          AND column_name = 'email' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE users
            ALTER COLUMN email TYPE VARCHAR(255) USING convert_from(email, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'users'
          AND column_name = 'phone_number' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE users
            ALTER COLUMN phone_number TYPE VARCHAR(20) USING convert_from(phone_number, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workspaces'
          AND column_name = 'name' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE workspaces
            ALTER COLUMN name TYPE VARCHAR(255) USING convert_from(name, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workspaces'
          AND column_name = 'slug' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE workspaces
            ALTER COLUMN slug TYPE VARCHAR(255) USING convert_from(slug, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workspaces'
          AND column_name = 'description' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE workspaces
            ALTER COLUMN description TYPE TEXT USING convert_from(description, 'UTF8');
    END IF;
END $$;
