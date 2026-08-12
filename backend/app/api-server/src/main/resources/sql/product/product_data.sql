USE `farm-market`;

-- ------------------------------------------------------------
-- Product domain seed: products
-- - Table: TB_PRODUCT
-- - Rows: 6 visible products and 24 discontinued fixtures
-- ------------------------------------------------------------

INSERT INTO TB_PRODUCT (product_id, product_name, description, price, status, stock_quantity, created_at, updated_at)
VALUES
       (1, '햇 감자 3kg', '주말농장에서 직접 수확한 햇감자', 9900, 'ON_SALE', 30, NOW(), NOW()),
       (2, '청양고추 500g', '알싸한 맛이 살아 있는 제철 고추', 6900, 'UPCOMING', 0, NOW(), NOW()),
       (3, '양배추 1통', '파종부터 수확까지 기록한 양배추', 5900, 'ON_SALE', 20, NOW(), NOW()),
       (4, '공심채 300g', '여름 농장에서 자란 아삭한 공심채', 4500, 'UPCOMING', 0, NOW(), NOW()),
       (5, '참외 2kg', '여름 햇볕을 맞으며 자란 제철 참외', 15900, 'OUT_OF_STOCK', 0, NOW(), NOW()),
       (6, '애플수박 1통', '초보 농부의 도전으로 키운 애플수박', 12900, 'OUT_OF_STOCK', 0, NOW(), NOW()),
       (7, '테스트 상품 007', '테스트 설명 007', 45000, 'DISCONTINUED', 27, NOW(), NOW()),
       (8, '테스트 상품 008', '테스트 설명 008', 50000, 'DISCONTINUED', 28, NOW(), NOW()),
       (9, '테스트 상품 009', '테스트 설명 009', 55000, 'DISCONTINUED', 29, NOW(), NOW()),
       (10, '테스트 상품 010', '테스트 설명 010', 11000, 'DISCONTINUED', 30, NOW(), NOW()),
       (11, '테스트 상품 011', '테스트 설명 011', 16000, 'DISCONTINUED', 31, NOW(), NOW()),
       (12, '테스트 상품 012', '테스트 설명 012', 21000, 'DISCONTINUED', 32, NOW(), NOW()),
       (13, '테스트 상품 013', '테스트 설명 013', 26000, 'DISCONTINUED', 33, NOW(), NOW()),
       (14, '테스트 상품 014', '테스트 설명 014', 31000, 'DISCONTINUED', 34, NOW(), NOW()),
       (15, '테스트 상품 015', '테스트 설명 015', 36000, 'DISCONTINUED', 0, NOW(), NOW()),
       (16, '테스트 상품 016', '테스트 설명 016', 41000, 'DISCONTINUED', 36, NOW(), NOW()),
       (17, '테스트 상품 017', '테스트 설명 017', 46000, 'DISCONTINUED', 37, NOW(), NOW()),
       (18, '테스트 상품 018', '테스트 설명 018', 51000, 'DISCONTINUED', 38, NOW(), NOW()),
       (19, '테스트 상품 019', '테스트 설명 019', 56000, 'DISCONTINUED', 39, NOW(), NOW()),
       (20, '테스트 상품 020', '테스트 설명 020', 12000, 'DISCONTINUED', 40, NOW(), NOW()),
       (21, '테스트 상품 021', '테스트 설명 021', 17000, 'DISCONTINUED', 41, NOW(), NOW()),
       (22, '테스트 상품 022', '테스트 설명 022', 22000, 'DISCONTINUED', 42, NOW(), NOW()),
       (23, '테스트 상품 023', '테스트 설명 023', 27000, 'DISCONTINUED', 43, NOW(), NOW()),
       (24, '테스트 상품 024', '테스트 설명 024', 32000, 'DISCONTINUED', 44, NOW(), NOW()),
       (25, '테스트 상품 025', '테스트 설명 025', 37000, 'DISCONTINUED', 45, NOW(), NOW()),
       (26, '테스트 상품 026', '테스트 설명 026', 42000, 'DISCONTINUED', 46, NOW(), NOW()),
       (27, '테스트 상품 027', '테스트 설명 027', 47000, 'DISCONTINUED', 47, NOW(), NOW()),
       (28, '테스트 상품 028', '테스트 설명 028', 52000, 'DISCONTINUED', 48, NOW(), NOW()),
       (29, '테스트 상품 029', '테스트 설명 029', 57000, 'DISCONTINUED', 49, NOW(), NOW()),
       (30, '테스트 상품 030', '테스트 설명 030', 13000, 'DISCONTINUED', 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE product_name = VALUES(product_name),
                        description = VALUES(description),
                        price = VALUES(price),
                        status = VALUES(status),
                        stock_quantity = VALUES(stock_quantity),
                        updated_at = VALUES(updated_at);
