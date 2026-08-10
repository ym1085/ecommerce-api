USE `farm-market`;

-- ------------------------------------------------------------
-- Cart domain seed: cart items
-- - Table: TB_CART_ITEM
-- - Rows: 10
-- ------------------------------------------------------------

INSERT INTO TB_CART_ITEM (cart_item_id, cart_id, product_id, quantity, created_at, updated_at)
VALUES
       (1, 1, 1, 1, NOW(), NOW()),
       (2, 2, 2, 2, NOW(), NOW()),
       (3, 3, 3, 1, NOW(), NOW()),
       (4, 4, 4, 3, NOW(), NOW()),
       (5, 5, 5, 2, NOW(), NOW()),
       (6, 6, 6, 1, NOW(), NOW()),
       (7, 7, 7, 2, NOW(), NOW()),
       (8, 8, 8, 1, NOW(), NOW()),
       (9, 9, 9, 4, NOW(), NOW()),
       (10, 10, 10, 2, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
