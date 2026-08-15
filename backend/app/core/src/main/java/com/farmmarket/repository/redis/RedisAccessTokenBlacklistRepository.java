package com.farmmarket.repository.redis;

import com.farmmarket.repository.AccessTokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisAccessTokenBlacklistRepository implements AccessTokenBlacklistRepository {

    private static final String KEY_PREFIX = "auth:blacklist:";
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveAccessTokenJtiToBlacklist(String jti, Duration ttl) {
        stringRedisTemplate.opsForValue().set(KEY_PREFIX + jti, "1", ttl);
    }

    @Override
    public boolean existsAccessTokenJtiInBlacklist(String jti) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(KEY_PREFIX + jti));
    }
}
