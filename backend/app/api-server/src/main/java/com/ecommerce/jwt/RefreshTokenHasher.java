package com.ecommerce.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Slf4j
@Component
public class RefreshTokenHasher {

    /**
     * Refresh Token을 SHA-256 해시로 변환한다
     * -> Refresh Token 원문을 Redis에 그대로 저장하지 않기 위해 사용
     */
    public String hash(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            log.error("리프레시 토큰이 없습니다.");
            throw new IllegalArgumentException("Refresh Token은 비어 있을 수 없습니다");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] tokenBytes = refreshToken.getBytes(StandardCharsets.UTF_8);
            return HexFormat.of().formatHex(digest.digest(tokenBytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다", e);
        }
    }

    /**
     * 전달받은 Refresh Token과 저장된 해시가 일치하는지 확인한다
     */
    public boolean matches(String refreshToken, String savedTokenHash) {
        if (!StringUtils.hasText(refreshToken) || !StringUtils.hasText(savedTokenHash)) {
            return false;
        }
        byte[] actualHash = hash(refreshToken).getBytes(StandardCharsets.UTF_8);
        byte[] expectedHash = savedTokenHash.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(actualHash, expectedHash);
    }
}
