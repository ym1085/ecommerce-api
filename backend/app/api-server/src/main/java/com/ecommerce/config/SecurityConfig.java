package com.ecommerce.config;

import com.ecommerce.jwt.JwtAuthenticationFilter;
import com.ecommerce.jwt.JwtProvider;
import com.ecommerce.repository.AccessTokenBlacklistRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtProvider jwtProvider,
            AccessTokenBlacklistRepository accessTokenBlacklistRepository
    ) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.POST, "/api/v1/members").permitAll() // 회원가입
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll() // 로그인
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/reissue").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").permitAll()
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    //.requestMatchers(HttpMethod.GET, "/api/v1/members/me").hasAnyRole("USER", "ADMIN")
                    // 그 외 모두 인증 필요
                    .anyRequest().authenticated()
            )
            .formLogin(f -> f.disable()) // Security 제공 기본 로그인 화면 + 인증 API 비활성화
            .httpBasic(b -> b.disable()) // 클라이언트가 요청 헤더에 아이디와 비밀번호를 담아 보내는 인증 방식
            .exceptionHandling(e -> e
                    .authenticationEntryPoint((req, res, ex)
                            -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)) // 인증되지 않은 사용자가 인증이 필요한 API에 접근했을 때 실행 -> 401 Unauthorized
                    .accessDeniedHandler((req, res, ex)
                            -> res.sendError(HttpServletResponse.SC_FORBIDDEN))) // 인증은 됐지만 필요한 권한이 없을 때 실행 -> 403 Forbidden
            .addFilterBefore(
                    new JwtAuthenticationFilter(jwtProvider, accessTokenBlacklistRepository),
                    UsernamePasswordAuthenticationFilter.class
            ); // Spring Security 기본 필터보다 먼저 JWT 검사

        return http.build();
    }
}
