USE `sandbox-ecommerce`;

-- ------------------------------------------------------------
-- Order domain seed: payments
-- - Table: TB_PAYMENT
-- - Rows: 10
-- ------------------------------------------------------------

INSERT INTO TB_PAYMENT (payment_id, order_id, payment_method, payment_status, pg_tx_id, created_at, updated_at)
VALUES
       (1, 1, 'CARD', 'PAID', 'PG-TX-000001', '2026-01-10 10:05:00', '2026-01-10 10:05:00'),
       (2, 2, 'KAKAO_PAY', 'PAID', 'PG-TX-000002', '2026-01-11 10:05:00', '2026-01-11 10:05:00'),
       (3, 3, 'NAVER_PAY', 'PAID', 'PG-TX-000003', '2026-01-12 10:05:00', '2026-01-12 10:05:00'),
       (4, 4, 'TRANSFER', 'PAID', 'PG-TX-000004', '2026-01-13 10:05:00', '2026-01-13 10:05:00'),
       (5, 5, 'CARD', 'PAID', 'PG-TX-000005', '2026-01-14 10:05:00', '2026-01-14 10:05:00'),
       (6, 6, 'KAKAO_PAY', 'READY', NULL, '2026-01-15 10:00:00', '2026-01-15 10:00:00'),
       (7, 7, 'NAVER_PAY', 'READY', NULL, '2026-01-16 10:00:00', '2026-01-16 10:00:00'),
       (8, 8, 'TRANSFER', 'READY', NULL, '2026-01-17 10:00:00', '2026-01-17 10:00:00'),
       (9, 9, 'CARD', 'CANCELED', 'PG-TX-000009', '2026-01-18 10:20:00', '2026-01-18 10:20:00'),
       (10, 10, 'KAKAO_PAY', 'CANCELED', 'PG-TX-000010', '2026-01-19 10:20:00', '2026-01-19 10:20:00')
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
