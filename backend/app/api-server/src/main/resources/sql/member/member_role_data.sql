USE `sandbox-ecommerce`;

-- TB_MEMBER_ROLE
INSERT INTO TB_MEMBER_ROLE (member_role_id, member_id, role_id, created_at, updated_at)
VALUES (1, 1, 1, NOW(), NOW()),
       (2, 2, 1, NOW(), NOW()),
       (3, 3, 1, NOW(), NOW()),
       (4, 3, 2, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
