USE `farm-market`;

-- ------------------------------------------------------------
-- Member domain seed: members
-- - Table: TB_MEMBER
-- - Rows: 10
-- ------------------------------------------------------------

INSERT INTO TB_MEMBER (member_id, name, password, email, phone_number, status, is_agree_marketing, last_login_at, created_at, updated_at)
VALUES
       (1, '테스트회원001', '$2a$10$dummyhashedpassword001', 'user001@test.com', '010-1000-2000', 'ACTIVE', 'N', NULL, NOW(), NOW()),
       (2, '테스트회원002', '$2a$10$dummyhashedpassword002', 'user002@test.com', '010-1001-2001', 'ACTIVE', 'Y', NOW(), NOW(), NOW()),
       (3, '테스트회원003', '$2a$10$dummyhashedpassword003', 'user003@test.com', '010-1002-2002', 'ACTIVE', 'N', NULL, NOW(), NOW()),
       (4, '테스트회원004', '$2a$10$dummyhashedpassword004', 'user004@test.com', '010-1003-2003', 'ACTIVE', 'Y', NOW(), NOW(), NOW()),
       (5, '테스트회원005', '$2a$10$dummyhashedpassword005', 'user005@test.com', '010-1004-2004', 'ACTIVE', 'N', NULL, NOW(), NOW()),
       (6, '테스트회원006', '$2a$10$dummyhashedpassword006', 'user006@test.com', '010-1005-2005', 'ACTIVE', 'Y', NOW(), NOW(), NOW()),
       (7, '테스트회원007', '$2a$10$dummyhashedpassword007', 'user007@test.com', '010-1006-2006', 'ACTIVE', 'N', NULL, NOW(), NOW()),
       (8, '테스트회원008', '$2a$10$dummyhashedpassword008', 'user008@test.com', '010-1007-2007', 'ACTIVE', 'Y', NOW(), NOW(), NOW()),
       (9, '테스트회원009', '$2a$10$dummyhashedpassword009', 'user009@test.com', '010-1008-2008', 'ACTIVE', 'N', NULL, NOW(), NOW()),
       (10, '테스트회원010', '$2a$10$dummyhashedpassword010', 'user010@test.com', '010-1009-2009', 'ACTIVE', 'Y', NOW(), NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
