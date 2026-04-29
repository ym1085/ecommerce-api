USE `sandbox-ecommerce`;

-- TB_CART
INSERT INTO TB_CART (cart_id, member_id, created_at, updated_at)
VALUES (1, 1, NOW(), NOW()),
       (2, 2, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
