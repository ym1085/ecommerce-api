package com.ecommerce.restcontroller;

import com.ecommerce.dto.req.MemberRequestDto;
import com.ecommerce.dto.res.MemberResponseDto;
import com.ecommerce.jwt.JwtProvider;
import com.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthRestController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<MemberResponseDto.Login> login(
            @RequestBody @Valid MemberRequestDto.Login request) {
        log.info("로그인 요청 - email = {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }

    @PostMapping("/reissue")
    public ResponseEntity<MemberResponseDto.Reissue> reissueAccessToken(
            @RequestBody @Valid MemberRequestDto.Reissue request) {
        log.info("Access Token 재발급 요청");
        return ResponseEntity.status(HttpStatus.OK).body(authService.reissueAccessToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<MemberResponseDto.Logout> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody @Valid MemberRequestDto.Logout request) {
        log.info("로그아웃 시도");
        String accessToken = jwtProvider.extractAccessTokenByAuthentication(authorization);
        return ResponseEntity.status(HttpStatus.OK).body(authService.logout(accessToken, request));
    }
}
