USE `sandbox-ecommerce`;

-- TB_ROLE
INSERT INTO TB_ROLE (role_id, role_name, created_at, updated_at)
VALUES (1, 'ROLE_USER',  NOW(), NOW()),
       (2, 'ROLE_ADMIN', NOW(), NOW())
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);
