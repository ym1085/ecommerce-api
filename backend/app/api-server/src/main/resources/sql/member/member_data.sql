USE `sandbox-ecommerce`;

-- TB_MEMBER
INSERT INTO TB_MEMBER (member_id, name, password, email, phone_number, status, is_agree_marketing, last_login_at, created_at, updated_at)
VALUES (1, '김영민', '$2a$10$dummyhashedpassword1111', 'user1@test.com',  '010-1111-1111', 'ACTIVE', 'Y', NULL, NOW(), NOW()),
       (2, '이지수', '$2a$10$dummyhashedpassword2222', 'user2@test.com',  '010-2222-2222', 'ACTIVE', 'N', NULL, NOW(), NOW()),
       (3, '박민준', '$2a$10$dummyhashedpassword3333', 'admin@test.com',  '010-3333-3333', 'ACTIVE', 'Y', NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE email = VALUES(email);
