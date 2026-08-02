package com.ecommerce.service;

import com.ecommerce.jwt.JwtProperties;
import com.ecommerce.jwt.JwtProvider;
import com.ecommerce.jwt.RefreshTokenHasher;
import com.ecommerce.repository.MemberRepository;
import com.ecommerce.repository.RefreshTokenRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayName("회원 관련 Service 테스트")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RefreshTokenHasher refreshTokenHasher;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthService authService;
}