package com.ecommerce.jwt;


import com.ecommerce.repository.AccessTokenBlacklistRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter  {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_CLAIM = "role";

    // Spring Security의 hasRole("ADMIN")은 내부적으로 "ROLE_ADMIN"을 찾으므로 권한 생성 시 접두사를 붙여준다
    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtProvider jwtProvider;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = extractJwtToken(request); // accessToken 추출

        // 서명이 유효해도 로그아웃(블랙리스트)된 토큰이면 인증을 심지 않는다
        // jti 추출은 hasText / validateAccessToken 통과 뒤에 해야 무효 토큰 파싱 예외를 막는다
        if (StringUtils.hasText(accessToken)
                && jwtProvider.validateAccessToken(accessToken)
                && !accessTokenBlacklistRepository.existsAccessTokenJtiInBlacklist(
                        jwtProvider.extractJtiByAccessToken(accessToken))) {
            SecurityContextHolder.getContext().setAuthentication(createAuthentication(accessToken));
        }

        // 인증 여부와 무관하게 다음 필터로 넘긴다
        filterChain.doFilter(request, response);
    }

    private Authentication createAuthentication(String accessToken) {
        Claims claims = jwtProvider.extractClaimsByAccessToken(accessToken);

        String email = claims.getSubject();
        String role = claims.get(ROLE_CLAIM, String.class);

        // 현재 로그인한 사용장의 권한 목록
        // hasRole("ADMIN")을 쓰면 Spring Security가 실제로는 ROLE_ADMIN을 찾기 때문에 접두사 ROLE_를 붙힌다
        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role));

        // Spring Security가 사용할 로그인 완료 사용자 정보 객체 생성
        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }

    /**
     * Authorization 헤더에서 Bearer 토큰을 추출
     *
     * @return 토큰 문자열 반환, 헤더가 겂거나 Bearer 형식이 아니면 null
     */
    private String extractJwtToken(HttpServletRequest request) {
        // Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
        String bearerToken = request.getHeader(AUTH_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
