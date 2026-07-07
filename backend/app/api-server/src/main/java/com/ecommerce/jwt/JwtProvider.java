package com.ecommerce.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    public static final String TOKEN_TYPE = "Bearer";
    private final JwtProperties jwtProperties;

    // 서명 검증에 쓰는 비밀키, 매 요청마다 만들면 낭비라 최초 1회 생성 후 재사용
    private SecretKey key;

    @PostConstruct
    void init() {
        // secret은 Base64로 인코딩된 256bit 이상 키
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }

    /**
     * 로그인 성공 시 Access Token 1장을 발급한다
     *
     * @param email 토큰 주인 식별자. subject에 담아 두면 이후 필터에서 "누구인지" 복원 가능.
     * @param role 권한(USER/ADMIN) 인가 판단용으로 실어 보내, 매 요청마다 DB 조회를 안 해도 되게 한다.
     * @return 서명까지 끝난 직렬화 토큰 문자열(xxxxx.yyyyy.zzzzz)
     */
    public String createJwtToken(String email, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtProperties.getExpiration())) // 현재 날짜 시점 + 만료 기간을 더해서 셋팅
                .signWith(key)
                .compact();
    }

    /**
     * 필터가 매 요청마다 호출하는 관문 검사
     * 만료, 위조, 형식오류를 예외로 터뜨리지 않고 false로 흡수 -> 최종 차단은 SecurityConfig의 authenticated()가 담당
     */
    public boolean validateJwtToken(String token) {
        try {
            parseJwtToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; // 만료 / 서명불일치 / 형식오류 전부 여기를 탄다
        }
    }

    /**
     * 검증에 성공한 토큰에서 내용물(subject=email, role)을 꺼낸다
     *
     * @param token
     * @return
     */
    public Claims extractClaimsFromJwtToken(String token) {
        return parseJwtToken(token).getPayload();
    }

    /**
     * 서명키로 검증하며 파싱
     */
    private Jws<Claims> parseJwtToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }
}
