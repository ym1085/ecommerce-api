USE `sandbox-ecommerce`;

-- TB_PAYMENT
INSERT INTO TB_PAYMENT (payment_id, order_id, payment_method, payment_status, pg_tx_id, created_at, updated_at)
VALUES (1, 1, 'CARD',     'PAID',     'PG-TX-AAA-001', '2025-01-01 10:05:00', '2025-01-01 10:05:00'),
       (2, 2, 'CARD',     'PAID',     'PG-TX-BBB-002', '2025-02-01 14:10:00', '2025-02-01 14:10:00'),
       (3, 3, 'TRANSFER', 'READY',     NULL,            '2025-03-01 09:00:00', '2025-03-01 09:00:00'),
       (4, 4, 'CARD',     'CANCELED', 'PG-TX-DDD-004', '2025-04-01 16:30:00', '2025-04-01 16:30:00')
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
