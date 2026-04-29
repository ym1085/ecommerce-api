USE `sandbox-ecommerce`;

-- TB_CART_ITEM
INSERT INTO TB_CART_ITEM (cart_item_id, cart_id, product_id, quantity, created_at, updated_at)
VALUES (1, 1, 4, 1, NOW(), NOW()),
       (2, 1, 3, 2, NOW(), NOW()),
       (3, 2, 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
