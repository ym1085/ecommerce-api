USE `sandbox-ecommerce`;

-- TB_ORDER_ITEM
INSERT INTO TB_ORDER_ITEM (order_item_id, order_id, product_id, product_name, unit_price, quantity, created_at, updated_at)
VALUES (1, 1, 1, '나이키 에어맥스 90',    129000, 1, '2025-01-01 10:00:00', '2025-01-01 10:00:00'),
       (2, 2, 2, '아디다스 울트라부스트', 189000, 1, '2025-02-01 14:00:00', '2025-02-01 14:00:00'),
       (3, 2, 3, '뉴발란스 990v5',        279000, 1, '2025-02-01 14:00:00', '2025-02-01 14:00:00'),
       (4, 3, 5, '반스 올드스쿨',          79000, 1, '2025-03-01 09:00:00', '2025-03-01 09:00:00'),
       (5, 4, 2, '아디다스 울트라부스트', 189000, 1, '2025-04-01 16:00:00', '2025-04-01 16:00:00')
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
