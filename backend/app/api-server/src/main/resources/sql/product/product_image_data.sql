USE `farm-market`;

-- ------------------------------------------------------------
-- Product domain seed: representative product images
-- - Table: TB_PRODUCT_IMAGE
-- - Rows: 30 (one representative image per product)
-- - image_url stores an environment-independent object key
-- ------------------------------------------------------------

INSERT INTO TB_PRODUCT_IMAGE (
    product_image_id,
    image_url,
    original_file_name,
    content_type,
    file_size,
    is_representative,
    display_order,
    product_id,
    created_at,
    updated_at
)
VALUES
       (1, 'products/1/550e8400-e29b-41d4-a716-446655440001.webp', 'potato-main.webp', 'image/webp', 84320, 'Y', 1, 1, NOW(), NOW()),
       (2, 'products/2/550e8400-e29b-41d4-a716-446655440002.webp', 'chili-main.webp', 'image/webp', 62894, 'Y', 1, 2, NOW(), NOW()),
       (3, 'products/3/550e8400-e29b-41d4-a716-446655440003.webp', 'cabbage-main.webp', 'image/webp', 152780, 'Y', 1, 3, NOW(), NOW()),
       (4, 'products/4/550e8400-e29b-41d4-a716-446655440004.webp', 'water-spinach-main.webp', 'image/webp', 167648, 'Y', 1, 4, NOW(), NOW()),
       (5, 'products/5/550e8400-e29b-41d4-a716-446655440005.webp', 'korean-melon-main.webp', 'image/webp', 47712, 'Y', 1, 5, NOW(), NOW()),
       (6, 'products/6/550e8400-e29b-41d4-a716-446655440006.webp', 'watermelon-main.webp', 'image/webp', 89938, 'Y', 1, 6, NOW(), NOW()),
       (7, 'products/7/550e8400-e29b-41d4-a716-446655440007.webp', 'tomato-main.webp', 'image/webp', 24674, 'Y', 1, 7, NOW(), NOW()),
       (8, 'products/8/550e8400-e29b-41d4-a716-446655440008.webp', 'carrot-main.webp', 'image/webp', 220758, 'Y', 1, 8, NOW(), NOW()),
       (9, 'products/9/550e8400-e29b-41d4-a716-446655440009.webp', 'corn-main.webp', 'image/webp', 153042, 'Y', 1, 9, NOW(), NOW()),
       (10, 'products/10/550e8400-e29b-41d4-a716-446655440010.webp', 'product-010-main.webp', 'image/webp', 204800, 'Y', 1, 10, NOW(), NOW()),
       (11, 'products/11/550e8400-e29b-41d4-a716-446655440011.webp', 'product-011-main.webp', 'image/webp', 204800, 'Y', 1, 11, NOW(), NOW()),
       (12, 'products/12/550e8400-e29b-41d4-a716-446655440012.webp', 'product-012-main.webp', 'image/webp', 204800, 'Y', 1, 12, NOW(), NOW()),
       (13, 'products/13/550e8400-e29b-41d4-a716-446655440013.webp', 'product-013-main.webp', 'image/webp', 204800, 'Y', 1, 13, NOW(), NOW()),
       (14, 'products/14/550e8400-e29b-41d4-a716-446655440014.webp', 'product-014-main.webp', 'image/webp', 204800, 'Y', 1, 14, NOW(), NOW()),
       (15, 'products/15/550e8400-e29b-41d4-a716-446655440015.webp', 'product-015-main.webp', 'image/webp', 204800, 'Y', 1, 15, NOW(), NOW()),
       (16, 'products/16/550e8400-e29b-41d4-a716-446655440016.webp', 'product-016-main.webp', 'image/webp', 204800, 'Y', 1, 16, NOW(), NOW()),
       (17, 'products/17/550e8400-e29b-41d4-a716-446655440017.webp', 'product-017-main.webp', 'image/webp', 204800, 'Y', 1, 17, NOW(), NOW()),
       (18, 'products/18/550e8400-e29b-41d4-a716-446655440018.webp', 'product-018-main.webp', 'image/webp', 204800, 'Y', 1, 18, NOW(), NOW()),
       (19, 'products/19/550e8400-e29b-41d4-a716-446655440019.webp', 'product-019-main.webp', 'image/webp', 204800, 'Y', 1, 19, NOW(), NOW()),
       (20, 'products/20/550e8400-e29b-41d4-a716-446655440020.webp', 'product-020-main.webp', 'image/webp', 204800, 'Y', 1, 20, NOW(), NOW()),
       (21, 'products/21/550e8400-e29b-41d4-a716-446655440021.webp', 'product-021-main.webp', 'image/webp', 204800, 'Y', 1, 21, NOW(), NOW()),
       (22, 'products/22/550e8400-e29b-41d4-a716-446655440022.webp', 'product-022-main.webp', 'image/webp', 204800, 'Y', 1, 22, NOW(), NOW()),
       (23, 'products/23/550e8400-e29b-41d4-a716-446655440023.webp', 'product-023-main.webp', 'image/webp', 204800, 'Y', 1, 23, NOW(), NOW()),
       (24, 'products/24/550e8400-e29b-41d4-a716-446655440024.webp', 'product-024-main.webp', 'image/webp', 204800, 'Y', 1, 24, NOW(), NOW()),
       (25, 'products/25/550e8400-e29b-41d4-a716-446655440025.webp', 'product-025-main.webp', 'image/webp', 204800, 'Y', 1, 25, NOW(), NOW()),
       (26, 'products/26/550e8400-e29b-41d4-a716-446655440026.webp', 'product-026-main.webp', 'image/webp', 204800, 'Y', 1, 26, NOW(), NOW()),
       (27, 'products/27/550e8400-e29b-41d4-a716-446655440027.webp', 'product-027-main.webp', 'image/webp', 204800, 'Y', 1, 27, NOW(), NOW()),
       (28, 'products/28/550e8400-e29b-41d4-a716-446655440028.webp', 'product-028-main.webp', 'image/webp', 204800, 'Y', 1, 28, NOW(), NOW()),
       (29, 'products/29/550e8400-e29b-41d4-a716-446655440029.webp', 'product-029-main.webp', 'image/webp', 204800, 'Y', 1, 29, NOW(), NOW()),
       (30, 'products/30/550e8400-e29b-41d4-a716-446655440030.webp', 'product-030-main.webp', 'image/webp', 204800, 'Y', 1, 30, NOW(), NOW())
ON DUPLICATE KEY UPDATE image_url = VALUES(image_url),
                        original_file_name = VALUES(original_file_name),
                        content_type = VALUES(content_type),
                        file_size = VALUES(file_size),
                        is_representative = VALUES(is_representative),
                        display_order = VALUES(display_order),
                        product_id = VALUES(product_id),
                        updated_at = VALUES(updated_at);
