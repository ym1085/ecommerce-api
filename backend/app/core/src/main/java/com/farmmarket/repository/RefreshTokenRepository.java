package com.farmmarket.repository;

import java.time.Duration;
import java.util.Optional;

public interface RefreshTokenRepository {

    /**
     * Refresh Token 해시를 저장한다
     */
    void save(Long memberId, String tokenHash, Duration ttl);

    /**
     * 회원의 Refresh Token 해시를 조회한다
     */
    Optional<String> findByMemberId(Long memberId);

    /**
     * 회원의 Refresh Token을 삭제한다
     */
    void deleteByMemberId(Long memberId);
}
