package com.ecommerce.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    public static final String TOKEN_TYPE = "Bearer";
    private static final String ROLE_CLAIM = "role";
    private static final String TOKEN_KIND_CLAIM = "tokenKind";
    private static final String ACCESS_TOKEN_KIND = "ACCESS";
    private static final String REFRESH_TOKEN_KIND = "REFRESH";

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
    public String createAccessToken(String email, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)
                .claim(ROLE_CLAIM, role)
                .claim(TOKEN_KIND_CLAIM, ACCESS_TOKEN_KIND)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtProperties.getExpiration()))
                .signWith(key)
                .compact();

    }

    /**
     * Access Token 재발급에 사용할 Refresh Token을 발급한다
     */
    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(memberId.toString())
                .claim(TOKEN_KIND_CLAIM, REFRESH_TOKEN_KIND)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtProperties.getRefreshExpiration()))
                .signWith(key)
                .compact();
    }

    /**
     * 필터가 매 요청마다 호출하는 관문 검사
     * 만료, 위조, 형식오류를 예외로 터뜨리지 않고 false로 흡수 -> 최종 차단은 SecurityConfig의 authenticated()가 담당
     */
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = parseJwtToken(token).getPayload();
            return ACCESS_TOKEN_KIND.equals(claims.get(TOKEN_KIND_CLAIM, String.class))
                    && StringUtils.hasText(claims.getSubject())
                    && StringUtils.hasText(claims.get(ROLE_CLAIM, String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 재발급 요청에 사용된 Refresh Token을 검증한다
     */
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = parseJwtToken(token).getPayload();
            if (!REFRESH_TOKEN_KIND.equals(claims.get(TOKEN_KIND_CLAIM, String.class))) {
                return false;
            }
            Long.parseLong(claims.getSubject());
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Refresh Token에서 회원 ID를 추출한다
     */
    public Long extractMemberId(String refreshToken) {
        return Long.valueOf(parseJwtToken(refreshToken).getPayload().getSubject());
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
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }
}
