USE `sandbox-ecommerce`;

-- TB_ORDER
INSERT INTO TB_ORDER (order_id, order_no, order_status, total_amount, member_id, created_at, updated_at)
VALUES (1, 'ORD-20250101-000001', 'PAID',     129000, 1, '2025-01-01 10:00:00', '2025-01-01 10:05:00'),
       (2, 'ORD-20250201-000002', 'PAID',     468000, 1, '2025-02-01 14:00:00', '2025-02-01 14:10:00'),
       (3, 'ORD-20250301-000003', 'ORDERED',   79000, 2, '2025-03-01 09:00:00', '2025-03-01 09:00:00'),
       (4, 'ORD-20250401-000004', 'CANCELED', 189000, 1, '2025-04-01 16:00:00', '2025-04-01 16:30:00')
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
