package com.ecommerce.service;

import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.enums.MemberStatus;
import com.ecommerce.common.enums.Role;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.domain.Member;
import com.ecommerce.dto.req.MemberRequestDto;
import com.ecommerce.dto.res.MemberResponseDto;
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

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Service] 회원 관련 Service 테스트")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private Member member;

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

    @Nested
    @DisplayName("로그인 테스트")
    @Order(1)
    class Login {

        @Test
        @DisplayName("로그인에 성공하면 Access & Refresh Token을 발급한다")
        @Order(1)
        void shouldIssueTokensAndSaveRefreshToken_whenLoginSuccess() throws Exception {
            // given
            MemberRequestDto.Login request = MemberRequestDto.Login.builder()
                    .email("test@gmail.com")
                    .password("password")
                    .build();

            given(member.getId()).willReturn(1L);
            given(member.getEmail()).willReturn("test@gmail.com");
            given(member.getPassword()).willReturn("encodedPassword");
            given(member.getMemberStatus()).willReturn(MemberStatus.ACTIVE);
            given(member.getRole()).willReturn(Role.USER);

            given(memberRepository.findByEmail("test@gmail.com")).willReturn(Optional.of(member));
            given(passwordEncoder.matches("password", "encodedPassword")).willReturn(true);
            given(jwtProvider.createAccessToken("test@gmail.com", "USER")).willReturn("accessToken");
            given(jwtProvider.createRefreshToken(1L)).willReturn("refreshToken");
            given(refreshTokenHasher.hash("refreshToken")).willReturn("refreshTokenHash");
            given(jwtProperties.getRefreshExpiration()).willReturn(1_209_600_000L);

            // when
            MemberResponseDto.Login result = authService.login(request);

            // then
            assertThat(result.getMemberId()).isEqualTo(1L);
            assertThat(result.getAccessToken()).isEqualTo("accessToken");
            assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
            assertThat(result.getTokenType()).isEqualTo(JwtProvider.TOKEN_TYPE);

            then(member).should().updateLastLoginAt();
            then(refreshTokenRepository).should().save(
                    1L,
                    "refreshTokenHash",
                    Duration.ofMillis(1_209_600_000L)
            );
        }

        @Test
        @DisplayName("존재하지 않는 이메일이면 로그인에 실패한다")
        @Order(2)
        void shouldThrow_whenEmailNotFound() throws Exception {
            // given
            MemberRequestDto.Login request = MemberRequestDto.Login.builder()
                    .email("test@gmail.com")
                    .password("password")
                    .build();

            given(memberRepository.findByEmail("test@gmail.com")).willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            ErrorCode.MEMBER_INVALID_CREDENTIALS
                    );

            then(passwordEncoder).shouldHaveNoInteractions();
            then(refreshTokenRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 로그인에 실패한다")
        @Order(3)
        void shouldThrow_whenPasswordMismatch() throws Exception {
            // given
            MemberRequestDto.Login request = MemberRequestDto.Login.builder()
                    .email("test@gmail.com")
                    .password("wrongPassword")
                    .build();

            // 이메일에 해당하는 회원이 존재
            given(memberRepository.findByEmail("test@gmail.com")).willReturn(Optional.of(member));

            // 저장된 비밀번호와 요청 비밀번호가 불일치
            given(member.getPassword()).willReturn("encodedPassword");
            given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

            // when
            // then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            ErrorCode.MEMBER_INVALID_CREDENTIALS
                    );

            then(jwtProvider).shouldHaveNoInteractions();
            then(refreshTokenRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("비활성 회원이면 로그인에 실패한다")
        @Order(4)
        void shouldThrow_whenMemberNotActive() throws Exception {
            // given
            MemberRequestDto.Login request = MemberRequestDto.Login.builder()
                    .email("test@gmail.com")
                    .password("password")
                    .build();

            // 이메일에 해당하는 회원이 존재
            given(memberRepository.findByEmail("test@gmail.com")).willReturn(Optional.of(member));

            // 저장된 비밀번호와 요청 비밀번호는 일치
            given(member.getPassword()).willReturn("encodedPassword");
            given(passwordEncoder.matches("password", "encodedPassword")).willReturn(true);

            // 회원 상태가 비활성화 상태
            given(member.getMemberStatus()).willReturn(MemberStatus.INACTIVE);

            // when
            // then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            ErrorCode.MEMBER_NOT_ACTIVE
                    );

            then(jwtProvider).shouldHaveNoInteractions();
            then(refreshTokenRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("Access Token 재발급 테스트")
    @Order(2)
    class ReissueAccessToken {

        @Test
        @DisplayName("유효한 Refresh Token이면 Access Token을 재발급한다")
        @Order(1)
        void shouldReissueAccessToken_whenRefreshTokenValid() {
            // given
            // 유효한 Refresh Token으로 재발급 요청
            MemberRequestDto.Reissue request = MemberRequestDto.Reissue.builder()
                    .refreshToken("refreshToken")
                    .build();

            // Refresh Token 검증 후 회원 ID 추출
            given(jwtProvider.validateRefreshToken("refreshToken")).willReturn(true);
            given(jwtProvider.extractMemberIdByRefreshToken("refreshToken")).willReturn(1L);

            // Redis에 저장된 Refresh Token 해시와 일치
            given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.of("refreshTokenHash"));
            given(refreshTokenHasher.matches("refreshToken", "refreshTokenHash")).willReturn(true);

            // 활성 회원이 존재
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(member.getMemberStatus()).willReturn(MemberStatus.ACTIVE);
            given(member.getEmail()).willReturn("test@gmail.com");
            given(member.getRole()).willReturn(Role.USER);

            // 새로운 Access Token 발급
            given(jwtProvider.createAccessToken("test@gmail.com", "USER")).willReturn("newAccessToken");

            // when
            MemberResponseDto.Reissue result = authService.reissueAccessToken(request);

            // then
            assertThat(result.getAccessToken()).isEqualTo("newAccessToken");
            assertThat(result.getTokenType()).isEqualTo(JwtProvider.TOKEN_TYPE);
        }

        @Test
        @DisplayName("유효하지 않은 Refresh Token이면 재발급에 실패한다")
        @Order(2)
        void shouldThrow_whenRefreshTokenInvalid() {
            // given
            // 유효하지 않은 Refresh Token으로 재발급 요청
            MemberRequestDto.Reissue request = MemberRequestDto.Reissue.builder()
                    .refreshToken("invalidRefreshToken")
                    .build();

            // Refresh Token 자체 검증 실패
            given(jwtProvider.validateRefreshToken("invalidRefreshToken")).willReturn(false);

            // when
            // then
            assertThatThrownBy(() -> authService.reissueAccessToken(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            ErrorCode.AUTH_INVALID_REFRESH_TOKEN
                    );

            then(refreshTokenRepository).shouldHaveNoInteractions();
            then(memberRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Redis에 Refresh Token이 없으면 재발급에 실패한다")
        @Order(3)
        void shouldThrow_whenRefreshTokenNotStored() {
            // given
            // Refresh Token으로 재발급 요청
            MemberRequestDto.Reissue request = MemberRequestDto.Reissue.builder()
                    .refreshToken("refreshToken")
                    .build();

            // Refresh Token 검증 후 회원 ID 추출
            given(jwtProvider.validateRefreshToken("refreshToken")).willReturn(true);
            given(jwtProvider.extractMemberIdByRefreshToken("refreshToken")).willReturn(1L);

            // Redis에 회원의 Refresh Token이 존재하지 않음
            given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> authService.reissueAccessToken(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            ErrorCode.AUTH_INVALID_REFRESH_TOKEN
                    );

            then(refreshTokenHasher).shouldHaveNoInteractions();
            then(memberRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Redis에 저장된 Refresh Token과 일치하지 않으면 재발급에 실패한다")
        @Order(4)
        void shouldThrow_whenRefreshTokenMismatch() {
            // given
            // Refresh Token으로 재발급 요청
            MemberRequestDto.Reissue request = MemberRequestDto.Reissue.builder()
                    .refreshToken("refreshToken")
                    .build();

            // Refresh Token 검증 후 회원 ID 추출
            given(jwtProvider.validateRefreshToken("refreshToken")).willReturn(true);
            given(jwtProvider.extractMemberIdByRefreshToken("refreshToken")).willReturn(1L);

            // 전달된 토큰과 Redis 저장 해시가 불일치
            given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.of("savedTokenHash"));
            given(refreshTokenHasher.matches("refreshToken", "savedTokenHash")).willReturn(false);

            // when
            // then
            assertThatThrownBy(() -> authService.reissueAccessToken(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            ErrorCode.AUTH_INVALID_REFRESH_TOKEN
                    );

            then(memberRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Refresh Token의 회원이 존재하지 않으면 재발급에 실패한다")
        @Order(5)
        void shouldThrow_whenMemberNotFound() {
            // given
            // Refresh Token으로 재발급 요청
            MemberRequestDto.Reissue request = MemberRequestDto.Reissue.builder()
                    .refreshToken("refreshToken")
                    .build();

            // Refresh Token 검증 후 회원 ID 추출
            given(jwtProvider.validateRefreshToken("refreshToken")).willReturn(true);
            given(jwtProvider.extractMemberIdByRefreshToken("refreshToken")).willReturn(1L);

            // 전달된 토큰과 Redis 저장 해시가 일치
            given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.of("refreshTokenHash"));
            given(refreshTokenHasher.matches("refreshToken", "refreshTokenHash")).willReturn(true);

            // Refresh Token의 회원이 존재하지 않음
            given(memberRepository.findById(1L)).willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> authService.reissueAccessToken(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            ErrorCode.AUTH_INVALID_REFRESH_TOKEN
                    );

            then(member).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("비활성 회원이면 Refresh Token을 삭제하고 재발급에 실패한다")
        @Order(6)
        void shouldDeleteRefreshTokenAndThrow_whenMemberNotActive() {
            // given
            // Refresh Token으로 재발급 요청
            MemberRequestDto.Reissue request = MemberRequestDto.Reissue.builder()
                    .refreshToken("refreshToken")
                    .build();

            // Refresh Token 검증 후 회원 ID 추출
            given(jwtProvider.validateRefreshToken("refreshToken")).willReturn(true);
            given(jwtProvider.extractMemberIdByRefreshToken("refreshToken")).willReturn(1L);

            // 전달된 토큰과 Redis 저장 해시가 일치
            given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.of("refreshTokenHash"));
            given(refreshTokenHasher.matches("refreshToken", "refreshTokenHash")).willReturn(true);

            // 조회된 회원이 비활성 상태
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            given(member.getMemberStatus()).willReturn(MemberStatus.INACTIVE);

            // when
            // then
            assertThatThrownBy(() -> authService.reissueAccessToken(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            ErrorCode.MEMBER_NOT_ACTIVE
                    );

            then(refreshTokenRepository).should().deleteByMemberId(1L);
        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    @Order(3)
    class Logout {

        @Test
        @DisplayName("유효한 Refresh Token이면 저장된 토큰을 삭제하고 로그아웃한다")
        @Order(1)
        void shouldDeleteRefreshToken_whenLogoutSuccess() {
            // given
            // 유효한 Refresh Token으로 로그아웃 요청
            MemberRequestDto.Logout request = MemberRequestDto.Logout.builder()
                    .refreshToken("refreshToken")
                    .build();

            // Refresh Token 검증 후 회원 ID 추출
            given(jwtProvider.validateRefreshToken("refreshToken")).willReturn(true);
            given(jwtProvider.extractMemberIdByRefreshToken("refreshToken")).willReturn(1L);

            // 전달된 토큰과 Redis 저장 해시가 일치
            given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.of("refreshTokenHash"));
            given(refreshTokenHasher.matches("refreshToken", "refreshTokenHash")).willReturn(true);

            // when
            MemberResponseDto.Logout result = authService.logout(request);

            // then
            assertThat(result.getMessage()).isEqualTo("로그아웃에 성공하였습니다.");
            then(refreshTokenRepository).should().deleteByMemberId(1L);
        }

        @Test
        @DisplayName("유효하지 않은 Refresh Token이면 로그아웃에 실패한다")
        @Order(2)
        void shouldThrow_whenRefreshTokenInvalid() {
            // given
            // 유효하지 않은 Refresh Token으로 로그아웃 요청
            MemberRequestDto.Logout request = MemberRequestDto.Logout.builder()
                    .refreshToken("invalidRefreshToken")
                    .build();

            // Refresh Token 자체 검증 실패
            given(jwtProvider.validateRefreshToken("invalidRefreshToken")).willReturn(false);

            // when
            // then
            assertThatThrownBy(() -> authService.logout(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            ErrorCode.AUTH_INVALID_REFRESH_TOKEN
                    );

            then(refreshTokenRepository).shouldHaveNoInteractions();
        }
    }
}
