package com.farmmarket.service;

import com.farmmarket.common.enums.ErrorCode;
import com.farmmarket.common.exception.BusinessException;
import com.farmmarket.domain.Member;
import com.farmmarket.dto.req.MemberRequestDto;
import com.farmmarket.dto.res.MemberResponseDto;
import com.farmmarket.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@DisplayName("[Service] 회원 관련 Service 테스트")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    MemberService memberService;

    @Nested
    @DisplayName("signUp - 회원가입")
    class SingUp {

        @Test
        @DisplayName("정상 요청이면 회원을 저장하고 응답을 반환한다")
        @Order(1)
        void shouldReturnResponse_whenSignUpSuccess() throws Exception {
            // given
            MemberRequestDto.SignUp request = MemberRequestDto.SignUp.builder()
                    .email("test@gmail.com")
                    .password("password")
                    .name("test")
                    .phoneNumber("01012345678")
                    .isAgreeMarketing("Y")
                    .build();

            // 실제 회원가입 후 생성된 회원 정보
            Member savedMember = Member.createMember(
                    "test@gmail.com",
                    "encodedPassword",
                    "test",
                    "01012345678",
                    "Y"
            );

            given(memberRepository.existsByEmail("test@gmail.com")).willReturn(false);
            given(memberRepository.existsByPhoneNumber("01012345678")).willReturn(false);
            given(passwordEncoder.encode("password")).willReturn("encodedPassword");
            given(memberRepository.save(any(Member.class))).willReturn(savedMember);

            // when
            MemberResponseDto.SignUp result = memberService.signUp(request);

            // then
            assertThat(result.getEmail()).isEqualTo("test@gmail.com");
            assertThat(result.getName()).isEqualTo("test");

            then(passwordEncoder).should().encode("password");
            then(memberRepository).should().save(any(Member.class));
        }

        @Test
        @DisplayName("이미 가입된 이메일이면 예외를 던지고 저장하지 않는다")
        @Order(2)
        void shouldThrow_whenEmailDuplicated() throws Exception {
            // given
            MemberRequestDto.SignUp request = MemberRequestDto.SignUp.builder()
                    .email("test@gmail.com")
                    .password("password")
                    .name("test")
                    .phoneNumber("01012345678")
                    .isAgreeMarketing("Y")
                    .build();

            given(memberRepository.existsByEmail("test@gmail.com")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> memberService.signUp(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_DUPLICATE_EMAIL);

            then(memberRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("이미 가입된 휴대폰 번호면 예외를 던지고 저장하지 않는다")
        @Order(3)
        void shouldThrow_whenPhoneNumberDuplicated() throws Exception {
            // given
            MemberRequestDto.SignUp request = MemberRequestDto.SignUp.builder()
                    .email("test@gmail.com")
                    .password("password")
                    .name("test")
                    .phoneNumber("01012345678")
                    .isAgreeMarketing("Y")
                    .build();

            given(memberRepository.existsByEmail("test@gmail.com")).willReturn(false);
            given(memberRepository.existsByPhoneNumber("01012345678")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> memberService.signUp(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_DUPLICATE_PHONE_NUMBER);

            then(memberRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("저장 중 무결성 제약에 걸리면 중복 이메일 예외로 변환한다")
        @Order(4)
        void shouldThrow_whenDataIntegrityViolation() throws Exception {
            // given
            MemberRequestDto.SignUp request = MemberRequestDto.SignUp.builder()
                    .email("test@gmail.com")
                    .password("password")
                    .name("test")
                    .phoneNumber("01012345678")
                    .isAgreeMarketing("Y")
                    .build();

            given(memberRepository.existsByEmail("test@gmail.com")).willReturn(false);
            given(memberRepository.existsByPhoneNumber("01012345678")).willReturn(false);
            given(passwordEncoder.encode("password")).willReturn("encodedPassword");
            given(memberRepository.save(any(Member.class)))
                    .willThrow(new DataIntegrityViolationException("unique 제약 위반"));

            // when & then
            assertThatThrownBy(() -> memberService.signUp(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_DUPLICATE_EMAIL);
        }
    }
}
