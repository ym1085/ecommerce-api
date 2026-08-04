package com.ecommerce.repository;

import java.time.Duration;

/**
 * 로그아웃된 Access Token을 만료 전까지 차단하기 위한 블랙리스트 저장소
 */
public interface AccessTokenBlacklistRepository {

    /**
     * AccessToken의 jti를 남은 유효시간(ttl) 동안 블랙리스트에 등록한다
     */
    void saveAccessTokenJtiToBlacklist(String jti, Duration ttl);

    /**
     * 해당 jti가 블랙리스트에 있는지 확인한다
     */
    boolean existsAccessTokenJtiInBlacklist(String jti);
}
