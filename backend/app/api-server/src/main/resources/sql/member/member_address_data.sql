USE `sandbox-ecommerce`;

-- TB_MEMBER_ADDRESS
INSERT INTO TB_MEMBER_ADDRESS (member_address_id, member_id, address_nickname, is_default, zip_code, address, address_detail, created_at, updated_at)
VALUES (1, 1, '집',   'Y', '06236', '서울특별시 강남구 테헤란로 123',   '101동 202호', NOW(), NOW()),
       (2, 1, '회사', 'N', '06164', '서울특별시 강남구 삼성동 456',     '3층',         NOW(), NOW()),
       (3, 2, '집',   'Y', '14057', '경기도 안양시 동안구 관양동 789',  NULL,          NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
