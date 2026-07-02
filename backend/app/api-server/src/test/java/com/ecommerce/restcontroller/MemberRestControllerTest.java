package com.ecommerce.restcontroller;

import com.ecommerce.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("회원 관련 REST Controller 테스트")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest
class MemberRestControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean MemberService memberService;

    @Nested
    @DisplayName("POST /api/v1/members - 회원가입")
    class SignUp {
    
        @Test
        @DisplayName("정상 요청이면 201과 회원 정보를 반환한다")
        @Order(1)
        void shouldReturn201_whenSignUpSuccess() throws Exception {
            // given
            // when
            // then
        }

        @Test
        @DisplayName("이메일 형식이 올바르지 않으면 400을 반환한다")
        @Order(2)
        void shouldReturn400_whenEmailInvalid() throws Exception {
            //given
            //when
            //then
        }

        @Test
        @DisplayName("테스트노출명")
        @Order(3)
        void shouldReturn400_whenPhoneNum() throws Exception {
            //given
            //when
            //then
        }
    }
}