USE `sandbox-ecommerce`;

-- TB_DELIVERY
INSERT INTO TB_DELIVERY (delivery_id, order_id, delivery_status, tracking_number, receiver_name, receiver_phone_number, zip_code, address, address_detail, created_at, updated_at)
VALUES (1, 1, 'DELIVERED', 'CJ-1234567890', '김영민', '010-1111-1111', '06236', '서울특별시 강남구 테헤란로 123', '101동 202호', '2025-01-01 10:05:00', '2025-01-03 15:00:00'),
       (2, 2, 'SHIPPING',  'CJ-0987654321', '김영민', '010-1111-1111', '06236', '서울특별시 강남구 테헤란로 123', '101동 202호', '2025-02-01 14:10:00', '2025-02-02 09:00:00'),
       (3, 3, 'READY',      NULL,           '이지수', '010-2222-2222', '14057', '경기도 안양시 동안구 관양동 789', NULL,          '2025-03-01 09:00:00', '2025-03-01 09:00:00')
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
