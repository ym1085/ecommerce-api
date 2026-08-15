USE `farm-market`;

-- ------------------------------------------------------------
-- Member domain seed: members
-- - Table: TB_MEMBER
-- - Rows: 10
-- ------------------------------------------------------------

INSERT INTO TB_MEMBER (member_id, name, password, email, phone_number, status, role, is_agree_marketing, last_login_at, created_at, updated_at)
VALUES
       (1, '테스트회원001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user001@test.com', '010-1000-2000', 'ACTIVE', 'USER', 'N', NULL, NOW(), NOW()),
       (2, '테스트회원002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user002@test.com', '010-1001-2001', 'ACTIVE', 'USER', 'Y', NOW(), NOW(), NOW()),
       (3, '테스트회원003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user003@test.com', '010-1002-2002', 'ACTIVE', 'USER', 'N', NULL, NOW(), NOW()),
       (4, '테스트회원004', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user004@test.com', '010-1003-2003', 'ACTIVE', 'USER', 'Y', NOW(), NOW(), NOW()),
       (5, '테스트회원005', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user005@test.com', '010-1004-2004', 'ACTIVE', 'USER', 'N', NULL, NOW(), NOW()),
       (6, '테스트회원006', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user006@test.com', '010-1005-2005', 'ACTIVE', 'USER', 'Y', NOW(), NOW(), NOW()),
       (7, '테스트회원007', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user007@test.com', '010-1006-2006', 'ACTIVE', 'USER', 'N', NULL, NOW(), NOW()),
       (8, '테스트회원008', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user008@test.com', '010-1007-2007', 'ACTIVE', 'USER', 'Y', NOW(), NOW(), NOW()),
       (9, '테스트회원009', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user009@test.com', '010-1008-2008', 'ACTIVE', 'USER', 'N', NULL, NOW(), NOW()),
       (10, '테스트회원010', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user010@test.com', '010-1009-2009', 'ACTIVE', 'USER', 'Y', NOW(), NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
