# Member 인증 구현 계획 (회원가입 / 로그인 / 소셜)

> 다른 PC의 Claude Code에서도 이어서 작업할 수 있도록 진행 상황과 구현 방향을 기록한 문서.
> 작업 브랜치: `feature/member-social`

## 전체 로드맵

| 단계 | 내용 | 상태 |
| ---- | ---- | ---- |
| 1차 | 일반 회원가입 | 진행 중 |
| 2차 | 로그인 + JWT (SecurityConfig, JwtProvider, 인증 필터) | 예정 |
| 3차 | 소셜 로그인 (OAuth2 + Spring Security + JWT) | 예정 |

## 작업 규칙 (이 작업 한정)

- **구현을 먼저 작성하고, 테스트는 마지막에 작성한다.** (CLAUDE.md의 TDD 규칙을 이 작업에 한해 예외 적용)
- 그 외 CLAUDE.md 아키텍처 규칙은 그대로 준수:
  - 비즈니스 로직은 Service(`api-server`)에서만
  - DB 접근은 Repository 인터페이스로만
  - Entity를 Response로 직접 노출 금지 → DTO 변환
  - Entity·Repository·DTO는 `core`, Service·Controller는 `api-server`
  - REST는 `restcontroller` 패키지/@RestController

---

## 1차 회원가입 — 진행 체크리스트

- [ ] 사전: `MemberRepository` 중복검증 메서드 + `ErrorCode` 회원 에러코드 추가
- [ ] ① DTO (core) — `MemberRequestDto.Signup` / `MemberResponseDto.Signup`
- [ ] ② PasswordEncoder Bean (api-server, SecurityConfig)
- [ ] ③ `MemberService.signup()` (api-server)
- [ ] ④ `MemberRestController` POST (api-server, restcontroller)
- [ ] ⑤ 빌드 확인
- [ ] ⑥ (마지막) 테스트 작성

작성 순서: **사전 → ① → ② → ③ → ④ → ⑤** (의존성 막힘 없이 컴파일됨)

---

## 사전: Repository & ErrorCode

**`backend/app/core/.../repository/MemberRepository.java`** — 메서드 추가
```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<Member> findByEmail(String email); // 2차 로그인용으로도 사용
}
```

**`backend/app/core/.../common/enums/ErrorCode.java`** — 회원 섹션에 추가
```java
// 회원 (MEMBER)
MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER-001", "회원 정보를 찾을 수 없습니다."),
DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER-002", "이미 사용 중인 이메일입니다."),
DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "MEMBER-003", "이미 사용 중인 전화번호입니다."),
```

---

## ① DTO (core, `dto/req` · `dto/res`)

기존 `OrderRequestDto`처럼 **중첩 static 클래스** 컨벤션을 따른다.

**`dto/req/MemberRequestDto.java`**
```java
package com.ecommerce.dto.req;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDto {

    @Getter
    @NoArgsConstructor
    public static class Signup {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 8, max = 20)
        private String password;

        @NotBlank
        private String name;

        @NotBlank
        @Pattern(regexp = "^01[0-9]{8,9}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
        private String phoneNumber;

        @NotBlank
        private String isAgreeMarketing; // "Y" / "N"
    }
}
```

**`dto/res/MemberResponseDto.java`** — Entity 직접 노출 금지, `from(Member)` 변환 메서드 포함
```java
package com.ecommerce.dto.res;

import com.ecommerce.domain.Member;
import lombok.Builder;
import lombok.Getter;

public class MemberResponseDto {

    @Getter
    @Builder
    public static class Signup {
        private Long id;
        private String email;
        private String name;

        public static Signup from(Member member) {
            return Signup.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .name(member.getName())
                    .build();
        }
    }
}
```

---

## ② PasswordEncoder Bean (api-server)

`config` 패키지에 **`SecurityConfig`** 신규. 지금은 PasswordEncoder Bean + 회원가입 경로 허용만. (2차에서 JWT 필터/인증 추가)

```java
package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/members").permitAll() // 회원가입 허용
                .anyRequest().permitAll()                       // 2차에서 인증 강화
            );
        return http.build();
    }
}
```

> ⚠️ `spring-boot-starter-security`가 이미 의존성에 있어, SecurityConfig가 없으면 모든 요청이 막히고 기본 로그인 폼이 뜬다. 그래서 이 단계가 필요.

---

## ③ MemberService.signup() (api-server)

```java
package com.ecommerce.service;

import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.domain.Member;
import com.ecommerce.dto.req.MemberRequestDto;
import com.ecommerce.dto.res.MemberResponseDto;
import com.ecommerce.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public MemberResponseDto.Signup signup(MemberRequestDto.Signup request) {
        // 1) 중복 검증
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (memberRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        // 2) 비밀번호 암호화 + 저장
        Member member = Member.createMember(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getPhoneNumber(),
                request.getIsAgreeMarketing()
        );
        Member saved = memberRepository.save(member);

        return MemberResponseDto.Signup.from(saved);
    }
}
```

> 포인트: 클래스는 `readOnly = true`, **쓰기 메서드만 `@Transactional`** 오버라이드 (OrderService와 동일 패턴).

---

## ④ MemberRestController (api-server, restcontroller)

```java
package com.ecommerce.restcontroller;

import com.ecommerce.dto.req.MemberRequestDto;
import com.ecommerce.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@RestController
public class MemberRestController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<?> signup(@RequestBody @Valid MemberRequestDto.Signup request) {
        log.info("Signup - email={}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.signup(request));
    }
}
```

---

## ⑤ 빌드 확인

```bash
./gradlew :backend:app:api-server:build
```

## ⑥ 테스트 (마지막에)

`MemberServiceTest` — 가입 성공 / 중복 이메일 / 중복 전화번호 케이스.

---

## 참고: 도메인 현황

- `Member.createMember(email, password, name, phoneNumber, isAgreeMarketing)` → status=ACTIVE, role=USER 세팅.
  - 메서드: `updatePassword` / `updateStatus` / `updateProfile` / `updateLastLoginAt`
- `MemberSocial` (TB_MEMBER_SOCIAL): `@ManyToOne(LAZY) Member` (단방향), `SocialProvider provider`, `String providerId`. 팩토리 `createMemberSocial(Member, SocialProvider, String)`.
- `SocialProvider` enum: KAKAO / NAVER / GOOGLE (description 보유).
- 의존성 현황: `spring-boot-starter-security`, `jjwt 0.12.6` 존재. **`oauth2-client`는 아직 없음 (3차에서 추가)**.
- `BusinessException(ErrorCode)` 단일 생성자.
