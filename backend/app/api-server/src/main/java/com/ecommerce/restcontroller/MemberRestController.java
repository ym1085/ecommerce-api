package com.ecommerce.restcontroller;

import com.ecommerce.dto.req.MemberRequestDto;
import com.ecommerce.dto.res.MemberResponseDto;
import com.ecommerce.service.MemberService;
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
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@RestController
public class MemberRestController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponseDto.SignUp> signUp(
            @RequestBody @Valid MemberRequestDto.SignUp request) {
        log.info("회원 가입 요청 - email = {}", request.getEmail());
        MemberResponseDto.SignUp savedMember = memberService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMember);
    }
}
