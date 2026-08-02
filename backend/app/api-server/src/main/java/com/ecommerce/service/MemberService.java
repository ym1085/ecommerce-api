package com.ecommerce.service;

import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.domain.Member;
import com.ecommerce.dto.req.MemberRequestDto;
import com.ecommerce.dto.res.MemberResponseDto;
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

}
