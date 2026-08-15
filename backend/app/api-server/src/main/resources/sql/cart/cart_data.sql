USE `farm-market`;

-- ------------------------------------------------------------
-- Cart domain seed: carts
-- - Table: TB_CART
-- - Rows: 10
-- ------------------------------------------------------------

INSERT INTO TB_CART (cart_id, member_id, created_at, updated_at)
VALUES
       (1, 1, NOW(), NOW()),
       (2, 2, NOW(), NOW()),
       (3, 3, NOW(), NOW()),
       (4, 4, NOW(), NOW()),
       (5, 5, NOW(), NOW()),
       (6, 6, NOW(), NOW()),
       (7, 7, NOW(), NOW()),
       (8, 8, NOW(), NOW()),
       (9, 9, NOW(), NOW()),
       (10, 10, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
