USE `farm-market`;

-- ------------------------------------------------------------
-- Member domain seed: member-role mapping
-- - Table: TB_MEMBER_ROLE
-- - Rows: 10
-- ------------------------------------------------------------

INSERT INTO TB_MEMBER_ROLE (member_role_id, member_id, role_id, created_at, updated_at)
VALUES
       (1, 1, 1, NOW(), NOW()),
       (2, 2, 1, NOW(), NOW()),
       (3, 3, 1, NOW(), NOW()),
       (4, 4, 1, NOW(), NOW()),
       (5, 5, 1, NOW(), NOW()),
       (6, 6, 1, NOW(), NOW()),
       (7, 7, 1, NOW(), NOW()),
       (8, 8, 1, NOW(), NOW()),
       (9, 9, 1, NOW(), NOW()),
       (10, 10, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
