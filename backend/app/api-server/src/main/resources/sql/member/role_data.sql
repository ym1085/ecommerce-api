USE `farm-market`;

-- ------------------------------------------------------------
-- Member domain seed: roles
-- - Table: TB_ROLE
-- - Rows: 2
-- - Note: master data, intentionally kept minimal
-- ------------------------------------------------------------

-- TB_ROLE
INSERT INTO TB_ROLE (role_id, role_name, created_at, updated_at)
VALUES
       (1, 'ROLE_USER', NOW(), NOW()),
       (2, 'ROLE_ADMIN', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
