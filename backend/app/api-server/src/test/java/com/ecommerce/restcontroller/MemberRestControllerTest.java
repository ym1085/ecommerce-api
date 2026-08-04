package com.ecommerce.restcontroller;

import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.dto.req.MemberRequestDto;
import com.ecommerce.dto.res.MemberResponseDto;
import com.ecommerce.jwt.JwtProvider;
import com.ecommerce.repository.AccessTokenBlacklistRepository;
import com.ecommerce.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.ecommerce.config.SecurityConfig;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Controller] 회원 관련 REST Controller 테스트")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(MemberRestController.class)
@Import(SecurityConfig.class) // Spring Security 실제 설정을 읽어서 테스트에 반영
class MemberRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MemberService memberService;

    @MockitoBean
    JwtProvider jwtProvider;

    @MockitoBean
    private AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    @Nested
    @DisplayName("POST /api/v1/members - 회원가입")
    @Order(1)
    class SignUp {

        @Test
        @DisplayName("정상 요청이면 201과 회원 정보를 반환한다")
        @Order(1)
        void shouldReturn201_whenSignUpSuccess() throws Exception {
            // given
            MemberResponseDto.SignUp response = MemberResponseDto.SignUp.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .name("test")
                    .build();

            given(memberService.signUp(any()))
                    .willReturn(response);

            MemberRequestDto.SignUp request = MemberRequestDto.SignUp.builder()
                    .email("test@gmail.com")
                    .password("password")
                    .name("test")
                    .phoneNumber("01012345678")
                    .isAgreeMarketing("Y")
                    .build();

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.email").value("test@gmail.com"))
                    .andExpect(jsonPath("$.name").value("test"));
        }

        static Stream<Arguments> invalidSignUpRequests() {
            return Stream.of(
                    // email: 형식 오류(@Email)
                    Arguments.of(MemberRequestDto.SignUp.builder()
                            .email("test@")
                            .password("password")
                            .name("test")
                            .phoneNumber("01012345678")
                            .isAgreeMarketing("Y")
                            .build()),

                    // password: 8자 미만(@Size)
                    Arguments.of(MemberRequestDto.SignUp.builder()
                            .email("test@gmail.com")
                            .password("123")
                            .name("test")
                            .phoneNumber("01012345678")
                            .isAgreeMarketing("Y")
                            .build()),

                    // name: 공백(@NotBlank)
                    Arguments.of(MemberRequestDto.SignUp.builder()
                            .email("test@gmail.com")
                            .password("password")
                            .name(" ")
                            .phoneNumber("01012345678")
                            .isAgreeMarketing("Y")
                            .build()),

                    // phoneNumber: 형식 오류(@Pattern)
                    Arguments.of(MemberRequestDto.SignUp.builder()
                            .email("test@gmail.com")
                            .password("password")
                            .name("test")
                            .phoneNumber("123")
                            .isAgreeMarketing("Y")
                            .build()),

                    // isAgreeMarketing: null(@NotBlank)
                    Arguments.of(MemberRequestDto.SignUp.builder()
                            .email("test@gmail.com")
                            .password("password")
                            .name("test")
                            .phoneNumber("01012345678")
                            .isAgreeMarketing(null)
                            .build())
            );
        }

        @ParameterizedTest
        @MethodSource("invalidSignUpRequests")
        @DisplayName("입력값 검증에 실패하면 400을 반환한다")
        @Order(2)
        void shouldReturn400_whenInvalidInput(MemberRequestDto.SignUp request) throws Exception {
            // when
            ResultActions result = mockMvc.perform(post("/api/v1/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("이미 가입된 이메일이면 409를 반환한다")
        @Order(3)
        void shouldReturn409_whenEmailDuplicated() throws Exception {
            // given
            given(memberService.signUp(any()))
                    .willThrow(new BusinessException(ErrorCode.MEMBER_DUPLICATE_EMAIL));

            MemberRequestDto.SignUp request = MemberRequestDto.SignUp.builder()
                    .email("test@gmail.com")
                    .password("password")
                    .name("test")
                    .phoneNumber("01012345678")
                    .isAgreeMarketing("Y")
                    .build();

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andDo(print())
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("이미 가입된 휴대폰 번호면 409를 반환한다")
        @Order(4)
        void shouldReturn409_whenPhoneNumberDuplicated() throws Exception {
            // given
            given(memberService.signUp(any()))
                    .willThrow(new BusinessException(ErrorCode.MEMBER_DUPLICATE_PHONE_NUMBER));

            MemberRequestDto.SignUp request = MemberRequestDto.SignUp.builder()
                    .email("test@gmail.com")
                    .password("password")
                    .name("test")
                    .phoneNumber("01012345678")
                    .isAgreeMarketing("Y")
                    .build();

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andDo(print())
                    .andExpect(status().isConflict());
        }
    }
}