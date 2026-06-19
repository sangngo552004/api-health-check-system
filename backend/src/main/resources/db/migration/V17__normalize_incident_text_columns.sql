DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'incidents'
          AND column_name = 'reason' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE incidents
            ALTER COLUMN reason TYPE TEXT USING convert_from(reason, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'incidents'
          AND column_name = 'root_cause' AND data_type = 'bytea'
    ) THEN
        ALTER TABLE incidents
            ALTER COLUMN root_cause TYPE TEXT USING convert_from(root_cause, 'UTF8');
    END IF;
END $$;
