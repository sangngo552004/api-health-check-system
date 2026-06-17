ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

UPDATE users
SET role = 'SUPER_ADMIN'
WHERE role = 'ADMIN';

UPDATE users
SET role = 'USER'
WHERE role = 'VIEWER';

ALTER TABLE users
ADD CONSTRAINT users_role_check
CHECK (role IN ('SUPER_ADMIN', 'USER'));

ALTER TABLE workspace_members DROP COLUMN IF EXISTS role;
