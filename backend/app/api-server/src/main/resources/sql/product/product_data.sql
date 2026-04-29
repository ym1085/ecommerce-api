USE `sandbox-ecommerce`;

-- TB_PRODUCT
INSERT INTO TB_PRODUCT (product_id, product_name, description, price, status, stock_quantity, created_at, updated_at)
VALUES (1, '나이키 에어맥스 90',    '클래식 디자인의 나이키 에어맥스 90',   129000, 'ON_SALE',      50, NOW(), NOW()),
       (2, '아디다스 울트라부스트', '고반발 쿠셔닝 러닝화',                 189000, 'ON_SALE',      30, NOW(), NOW()),
       (3, '뉴발란스 990v5',        '메이드 인 USA 프리미엄 스니커즈',      279000, 'ON_SALE',      20, NOW(), NOW()),
       (4, '나이키 조던 1',         '농구화의 아이콘',                       250000, 'OUT_OF_STOCK',  0, NOW(), NOW()),
       (5, '반스 올드스쿨',         '스케이트보드 슈즈의 클래식',            79000,  'ON_SALE',     100, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
