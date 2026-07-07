package com.ecommerce.config;

import com.ecommerce.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableConfigurationProperties(JwtProperties.class) // jwt.* 바인딩 + 빈 등록
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/members").permitAll() // 회원가입
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll() // 로그인화면
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        //.requestMatchers(HttpMethod.GET, "/api/v1/members/me").hasAnyRole("USER", "ADMIN")
                        // 그 외 모두 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin(f -> f.disable())
                .httpBasic(b -> b.disable());

        return http.build();
    }
}
