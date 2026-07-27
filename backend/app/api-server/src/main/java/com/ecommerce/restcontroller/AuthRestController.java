package com.ecommerce.restcontroller;

import com.ecommerce.dto.req.MemberRequestDto;
import com.ecommerce.dto.res.MemberResponseDto;
import com.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthRestController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<MemberResponseDto.Login> login(
            @RequestBody @Valid MemberRequestDto.Login request) {
        log.info("로그인 요청 - email = {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }

    @PostMapping("/reissue")
    public ResponseEntity<MemberResponseDto.Reissue> reissue(
            @RequestBody @Valid MemberRequestDto.Reissue request) {
        log.info("Access Token 재발급 요청");
        return ResponseEntity.status(HttpStatus.OK).body(authService.reissue(request));
    }
}
