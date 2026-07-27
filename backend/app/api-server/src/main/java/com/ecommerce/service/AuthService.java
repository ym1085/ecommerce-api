package com.ecommerce.service;

import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.enums.MemberStatus;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.domain.Member;
import com.ecommerce.dto.req.MemberRequestDto;
import com.ecommerce.dto.res.MemberResponseDto;
import com.ecommerce.jwt.JwtProperties;
import com.ecommerce.jwt.JwtProvider;
import com.ecommerce.jwt.RefreshTokenHasher;
import com.ecommerce.repository.MemberRepository;
import com.ecommerce.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * 로그인과 토큰 발급·검증 등 인증 흐름을 처리한다
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenHasher refreshTokenHasher;
    private final JwtProperties jwtProperties;

    /**
     * 로그인 수행
     * 회원 인증이 완료되면 Access Token과 Refresh Token을 발급한다
     */
    @Transactional
    public MemberResponseDto.Login login(MemberRequestDto.Login request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            log.error("로그인 실패 계정 email = {}", request.getEmail());
            throw new BusinessException(ErrorCode.MEMBER_INVALID_CREDENTIALS);
        }

        // 비밀번호 검증 이후에 확인한다
        // 앞에 두면 비밀번호를 모르는 공격자에게 "이 이메일은 탈퇴/차단된 계정"이라는 정보가 새어나간다
        if (member.getMemberStatus() != MemberStatus.ACTIVE) {
            log.warn("비활성 계정 로그인 시도 email = {}, status = {}", request.getEmail(), member.getMemberStatus());
            throw new BusinessException(ErrorCode.MEMBER_NOT_ACTIVE);
        }

        member.updateLastLoginAt(); // 최근 로그인 일자 업데이트 (Dirty Checking)

        // JWT 신규 Token 생성
        String accessToken = jwtProvider.createAccessToken(member.getEmail(), member.getRole().name());
        String refreshToken = jwtProvider.createRefreshToken(member.getId());
        String refreshTokenHash = refreshTokenHasher.hash(refreshToken);
        Duration refreshTokenTtl = Duration.ofMillis(jwtProperties.getRefreshExpiration());

        // refresh token 저장
        refreshTokenRepository.save(member.getId(), refreshTokenHash, refreshTokenTtl);

        return MemberResponseDto.Login.builder()
                .memberId(member.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(JwtProvider.TOKEN_TYPE)
                .build();
    }

    /**
     * 유효한 Refresh Token으로 새로운 Access Token을 발급한다
     */
    public MemberResponseDto.Reissue reissue(MemberRequestDto.Reissue request) {
        Long memberId = validateStoredRefreshToken(request.getRefreshToken());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_REFRESH_TOKEN));

        if (member.getMemberStatus() != MemberStatus.ACTIVE) {
            refreshTokenRepository.deleteByMemberId(memberId);
            throw new BusinessException(ErrorCode.MEMBER_NOT_ACTIVE);
        }

        String accessToken = jwtProvider.createAccessToken(member.getEmail(), member.getRole().name());

        return MemberResponseDto.Reissue.builder()
                .accessToken(accessToken)
                .tokenType(JwtProvider.TOKEN_TYPE)
                .build();
    }

    /**
     * 전달받은 Refresh Token의 유효성과 Redis 저장 정보 일치 여부를 검증한다
     *
     * @param refreshToken 클라이언트가 전달한 Refresh Token 원문
     * @return 검증된 Refresh Token의 회원 ID
     */
    private Long validateStoredRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken) || !jwtProvider.validateRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
        }

        // Claim Subject memberId 추출
        Long memberId = jwtProvider.extractMemberId(refreshToken);

        String savedTokenHash = refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_REFRESH_TOKEN));

        // 전달 받은 RefreshToken과 Redis에 저장된 해시(refreshTokenHash)가 일치하는지 확인
        if (!refreshTokenHasher.matches(refreshToken, savedTokenHash)) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
        }
        return memberId;
    }
}
