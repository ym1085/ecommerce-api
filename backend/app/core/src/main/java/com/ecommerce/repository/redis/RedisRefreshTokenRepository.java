package com.ecommerce.repository.redis;

import com.ecommerce.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private static final String KEY_PREFIX = "auth:refresh:";
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(Long memberId, String tokenHash, Duration ttl) {
        stringRedisTemplate.opsForValue()
                .set(createKey(memberId), tokenHash, ttl);
    }

    @Override
    public Optional<String> findByMemberId(Long memberId) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(createKey(memberId)))
                .filter(StringUtils::hasText);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        stringRedisTemplate.delete(createKey(memberId));
    }

    private String createKey(Long memberId) {
        return KEY_PREFIX + memberId;
    }
}
