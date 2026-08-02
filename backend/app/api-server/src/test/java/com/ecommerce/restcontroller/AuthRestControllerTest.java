package com.ecommerce.restcontroller;

import com.ecommerce.config.SecurityConfig;
import com.ecommerce.dto.req.MemberRequestDto;
import com.ecommerce.dto.res.MemberResponseDto;
import com.ecommerce.jwt.JwtProvider;
import com.ecommerce.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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

@DisplayName("[Controller] 회원 인증 REST Controller 테스트")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthRestController.class)
@Import(SecurityConfig.class) // Spring Security 실제 설정을 읽어서 테스트에 반영
class AuthRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    @Order(1)
    class Login {

        @Test
        @DisplayName("로그인에 성공하면 Access Token과 Refresh Token을 반환한다")
        @Order(1)
        void shouldReturn200AndTokens_whenLoginSuccess() throws Exception {
            // given
            // 요청 객체 생성
            MemberRequestDto.Login request = MemberRequestDto.Login.builder()
                    .email("test@gmail.com")
                    .password("password")
                    .build();

            // Mock 응답 객체 생성
            MemberResponseDto.Login response = MemberResponseDto.Login.builder()
                    .memberId(1L)
                    .accessToken("accessToken")
                    .refreshToken("refreshToken")
                    .tokenType(JwtProvider.TOKEN_TYPE)
                    .build();

            given(authService.login(any(MemberRequestDto.Login.class)))
                    .willReturn(response);

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.memberId").value(1L))
                    .andExpect(jsonPath("$.accessToken").value("accessToken"))
                    .andExpect(jsonPath("$.refreshToken").value("refreshToken"))
                    .andExpect(jsonPath("$.tokenType").value(JwtProvider.TOKEN_TYPE));
        }

        static Stream<Arguments> invalidEmailRequests() {
            return Stream.of(
                    Arguments.of(
                            "이메일 null",
                            MemberRequestDto.Login.builder()
                                    .email(null)
                                    .password("password")
                                    .build()
                    ),
                    Arguments.of(
                            "이메일 빈 문자열",
                            MemberRequestDto.Login.builder()
                                    .email("")
                                    .password("password")
                                    .build()
                    ),
                    Arguments.of(
                            "이메일 공백",
                            MemberRequestDto.Login.builder()
                                    .email(" ")
                                    .password("password")
                                    .build()
                    ),
                    Arguments.of(
                            "이메일 형식 오류",
                            MemberRequestDto.Login.builder()
                                    .email("test@")
                                    .password("password")
                                    .build()
                    )
            );
        }

        static Stream<Arguments> invalidPasswordRequests() {
            return Stream.of(
                    Arguments.of(
                            "비밀번호 null",
                            MemberRequestDto.Login.builder()
                                    .email("test@gmail.com")
                                    .password(null)
                                    .build()
                    ),
                    Arguments.of(
                            "비밀번호 빈 문자열",
                            MemberRequestDto.Login.builder()
                                    .email("test@gmail.com")
                                    .password("")
                                    .build()
                    ),
                    Arguments.of(
                            "비밀번호 공백",
                            MemberRequestDto.Login.builder()
                                    .email("test@gmail.com")
                                    .password(" ")
                                    .build()
                    )
            );
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("invalidEmailRequests")
        @DisplayName("이메일이 유효하지 않으면 400을 반환한다")
        @Order(2)
        void shouldReturn400_whenEmailInvalid(String caseName, MemberRequestDto.Login request) throws Exception {
            // when
            ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("invalidPasswordRequests")
        @DisplayName("비밀번호가 비어 있으면 400을 반환한다")
        @Order(3)
        void shouldReturn400_whenPasswordBlank(String caseName, MemberRequestDto.Login request) throws Exception {
            // when
            ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/reissue")
    @Order(2)
    class ReissueAccessToken {

        @Test
        @DisplayName("재발급에 성공하면 새로운 Access Token을 반환한다")
        @Order(1)
        void shouldReturn200AndAccessToken_whenReissueSuccess() throws Exception {
            //given
            // 요청 객체 생성
            MemberRequestDto.Reissue request = MemberRequestDto.Reissue.builder()
                    .refreshToken("refresh_token")
                    .build();

            // Mock 응답 객체 생성
            MemberResponseDto.Reissue response = MemberResponseDto.Reissue.builder()
                    .accessToken("accessToken")
                    .tokenType(JwtProvider.TOKEN_TYPE)
                    .build();

            given(authService.reissueAccessToken(any(MemberRequestDto.Reissue.class)))
                    .willReturn(response);

            //when
            ResultActions result = mockMvc.perform(post("/api/v1/auth/reissue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("accessToken"))
                    .andExpect(jsonPath("$.tokenType").value(JwtProvider.TOKEN_TYPE));
        }

        static Stream<Arguments> invalidReissueRequests() {
            return Stream.of(
                    Arguments.of(
                            "Refresh Token null",
                            MemberRequestDto.Reissue.builder()
                                    .refreshToken(null)
                                    .build()
                    ),
                    Arguments.of(
                            "Refresh Token 빈 문자열",
                            MemberRequestDto.Reissue.builder()
                                    .refreshToken("")
                                    .build()
                    ),
                    Arguments.of(
                            "Refresh Token 공백",
                            MemberRequestDto.Reissue.builder()
                                    .refreshToken(" ")
                                    .build()
                    )
            );
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("invalidReissueRequests")
        @DisplayName("Refresh Token이 비어 있으면 400을 반환한다")
        @Order(2)
        void shouldReturn400_whenRefreshTokenBlank(String caseName, MemberRequestDto.Reissue request) throws Exception {
            // when
            ResultActions result = mockMvc.perform(post("/api/v1/auth/reissue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/logout")
    @Order(3)
    class Logout {

        @Test
        @DisplayName("로그아웃에 성공하면 성공 메시지를 반환한다")
        @Order(1)
        void shouldReturn200AndMessage_whenLogoutSuccess() throws Exception {
            // given
            MemberRequestDto.Logout request = MemberRequestDto.Logout.builder()
                    .refreshToken("refresh_token")
                    .build();

            MemberResponseDto.Logout response = MemberResponseDto.Logout.builder()
                    .message("로그아웃에 성공하였습니다.")
                    .build();

            given(authService.logout(any(MemberRequestDto.Logout.class)))
                    .willReturn(response);

            // when
            ResultActions result = mockMvc.perform(post("/api/v1/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("로그아웃에 성공하였습니다."));
        }

        static Stream<Arguments> invalidLogoutRequests() {
            return Stream.of(
                    Arguments.of(
                            "Refresh Token null",
                            MemberRequestDto.Logout.builder()
                                    .refreshToken(null)
                                    .build()
                    ),
                    Arguments.of(
                            "Refresh Token 빈 문자열",
                            MemberRequestDto.Logout.builder()
                                    .refreshToken("")
                                    .build()
                    ),
                    Arguments.of(
                            "Refresh Token 공백",
                            MemberRequestDto.Logout.builder()
                                    .refreshToken(" ")
                                    .build()
                    )
            );
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("invalidLogoutRequests")
        @DisplayName("Refresh Token이 비어 있으면 400을 반환한다")
        @Order(2)
        void shouldReturn400_whenRefreshTokenBlank(String caseName, MemberRequestDto.Logout request) throws Exception {
            // when
            ResultActions result = mockMvc.perform(post("/api/v1/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}
