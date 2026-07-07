package com.ecommerce.service;

import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.domain.Member;
import com.ecommerce.dto.req.MemberRequestDto;
import com.ecommerce.dto.res.MemberResponseDto;
import com.ecommerce.jwt.JwtProvider;
import com.ecommerce.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public MemberResponseDto.SignUp signUp(MemberRequestDto.SignUp request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            log.error("이미 회원가입이 수행된 email 입니다. email = {}", request.getEmail());
            throw new BusinessException(ErrorCode.MEMBER_DUPLICATE_EMAIL);
        }
        if (memberRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            log.error("이미 회원가입이 수행된 전화번호 입니다. phoneNumber = {}", request.getPhoneNumber());
            throw new BusinessException(ErrorCode.MEMBER_DUPLICATE_PHONE_NUMBER);
        }

        Member member = Member.createMember(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getPhoneNumber(),
                request.getIsAgreeMarketing()
        );

        try {
            Member savedMember = memberRepository.save(member);
            return MemberResponseDto.SignUp.from(savedMember);
        } catch (DataIntegrityViolationException e) {
            log.warn("회원가입 동시성 발생. email = {}", request.getEmail(), e);
            throw new BusinessException(ErrorCode.MEMBER_DUPLICATE_EMAIL);
        }
    }

    @Transactional
    public MemberResponseDto.Login login(MemberRequestDto.Login request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            log.error("로그인 실패 계정 email = {}", request.getEmail());
            throw new BusinessException(ErrorCode.MEMBER_INVALID_CREDENTIALS);
        }

        member.updateLastLoginAt(); // 최근 로그인 일자 업데이트 (Dirty Checking)

        // JWT 신규 Token 생성
        String jwtToken = jwtProvider.createJwtToken(member.getEmail(), member.getRole().name());

        return MemberResponseDto.Login.builder()
                .memberId(member.getId())
                .accessToken(jwtToken)
                .tokenType(JwtProvider.TOKEN_TYPE)
                .build();
    }
}
