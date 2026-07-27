# Issue #48 Refresh Token 구현 메모

## 목적

Refresh Token을 Redis에 저장하고 이를 이용해 Access Token을 재발급하거나 무효화한다

GitHub Issue: `#48 [Feature] Refresh Token 재발급 및 무효화 기능 구현`

## 완료 조건

- [ ] 로그인 시 Refresh Token을 저장한다
- [ ] 재발급 API를 구현한다
- [ ] 로그아웃·강제 만료 시 Refresh Token을 무효화한다
- [ ] Refresh Token 자동화 테스트를 작성한다

## 확정한 설계

- 회원당 Refresh Token 1개를 유지한다
- Redis Key는 변경 가능한 이메일 대신 `memberId`를 사용한다
- Redis Key 형식은 `auth:refresh:{memberId}`다
- Redis에는 Refresh Token 원문이 아닌 SHA-256 해시를 저장한다
- Refresh Token 원문은 로그인 응답으로 클라이언트에 전달한다
- Access Token에는 `email`, `role`, `tokenKind=ACCESS`를 저장한다
- Refresh Token에는 `memberId`, `tokenKind=REFRESH`를 저장한다
- 인증 필터는 Access Token만 인증에 사용한다
- 재발급 시 현재 Refresh Token은 유지하고 Access Token만 새로 발급한다
- Refresh Token rotation은 Issue #48 범위에 포함하지 않는다
- Redis는 Spring Boot가 자동 등록한 `StringRedisTemplate`을 사용한다

## 현재까지 반영한 내용

### Redis 설정

- `backend/app/api-server/src/main/resources/application-local.yml`
  - `connect-timeout: 1s`
  - `timeout: 3s`
- `backend/app/api-server/src/main/resources/application-dev.yml`
  - `connect-timeout: 1s`
  - `timeout: 2s`
- `backend/app/api-server/src/main/resources/application-prod.yml`
  - `connect-timeout: 500ms`
  - `timeout: 1s`

### Repository

- `backend/app/core/src/main/java/com/ecommerce/repository/RefreshTokenRepository.java`
- `backend/app/core/src/main/java/com/ecommerce/repository/redis/RedisRefreshTokenRepository.java`

제공하는 동작:

```java
void save(Long memberId, String tokenHash, Duration ttl);

Optional<String> findByMemberId(Long memberId);

void deleteByMemberId(Long memberId);
```

### JWT

- `JwtProvider.createAccessToken(String email, String role)`
- `JwtProvider.createRefreshToken(Long memberId)`
- `JwtProvider.validateAccessToken(String token)`
- `JwtProvider.validateRefreshToken(String token)`
- `JwtProvider.extractMemberId(String refreshToken)`
- `JwtAuthenticationFilter`는 `validateAccessToken()`만 호출

### DTO 및 오류 코드

- `MemberRequestDto.Reissue`
- `MemberRequestDto.Logout`
- `MemberResponseDto.Login.refreshToken`
- `MemberResponseDto.Reissue`
- `MemberResponseDto.Logout`
- `ErrorCode.AUTH_INVALID_REFRESH_TOKEN`

### Refresh Token 해시

- `backend/app/api-server/src/main/java/com/ecommerce/jwt/RefreshTokenHasher.java`
- SHA-256 해시 생성
- `MessageDigest.isEqual()`을 이용한 해시 비교

## 다음 시작 지점

다음 작업은 `MemberService`에 로그인 저장, 재발급, 로그아웃, 강제 만료 로직을 연결하는 것이다

작업 전에 반드시 현재 대상 파일을 다시 읽고 이미 반영된 코드를 중복 적용하지 않는다

## 남은 구현 코드

### MemberService 의존성

대상:

`backend/app/api-server/src/main/java/com/ecommerce/service/MemberService.java`

추가 import:

```java
import com.ecommerce.jwt.JwtProperties;
import com.ecommerce.jwt.RefreshTokenHasher;
import com.ecommerce.repository.RefreshTokenRepository;
import org.springframework.util.StringUtils;

import java.time.Duration;
```

추가 필드:

```java
private final RefreshTokenRepository refreshTokenRepository;
private final RefreshTokenHasher refreshTokenHasher;
private final JwtProperties jwtProperties;
```

### 로그인 시 Refresh Token 저장

기존 `login()`의 토큰 생성과 응답 부분을 다음 흐름으로 변경한다

```java
String accessToken = jwtProvider.createAccessToken(
        member.getEmail(),
        member.getRole().name()
);
String refreshToken = jwtProvider.createRefreshToken(member.getId());

refreshTokenRepository.save(
        member.getId(),
        refreshTokenHasher.hash(refreshToken),
        Duration.ofMillis(jwtProperties.getRefreshExpiration())
);

return MemberResponseDto.Login.builder()
        .memberId(member.getId())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType(JwtProvider.TOKEN_TYPE)
        .build();
```

### Access Token 재발급

```java
/**
 * Refresh Token을 검증하고 새로운 Access Token을 발급한다
 */
public MemberResponseDto.Reissue reissue(MemberRequestDto.Reissue request) {
    Long memberId = validateStoredRefreshToken(request.getRefreshToken());

    Member member = memberRepository.findById(memberId)
            .orElseThrow(this::invalidRefreshToken);

    if (member.getMemberStatus() != MemberStatus.ACTIVE) {
        refreshTokenRepository.deleteByMemberId(memberId);
        throw new BusinessException(ErrorCode.MEMBER_NOT_ACTIVE);
    }

    String accessToken = jwtProvider.createAccessToken(
            member.getEmail(),
            member.getRole().name()
    );

    return MemberResponseDto.Reissue.builder()
            .accessToken(accessToken)
            .tokenType(JwtProvider.TOKEN_TYPE)
            .build();
}
```

### 로그아웃과 강제 만료

```java
/**
 * Refresh Token을 검증한 뒤 Redis에서 삭제한다
 */
public MemberResponseDto.Logout logout(MemberRequestDto.Logout request) {
    Long memberId = validateStoredRefreshToken(request.getRefreshToken());

    refreshTokenRepository.deleteByMemberId(memberId);

    return MemberResponseDto.Logout.builder()
            .message("로그아웃되었습니다.")
            .build();
}

/**
 * 계정 정지나 탈퇴 시 Refresh Token을 강제로 삭제한다
 */
public void invalidateRefreshToken(Long memberId) {
    refreshTokenRepository.deleteByMemberId(memberId);
}
```

강제 만료 메서드는 계정 정지, 탈퇴, 비밀번호 변경처럼 기존 로그인 세션을 종료해야 하는 Service 흐름에서 호출한다

### 공통 Refresh Token 검증

```java
private Long validateStoredRefreshToken(String refreshToken) {
    if (!StringUtils.hasText(refreshToken)
            || !jwtProvider.validateRefreshToken(refreshToken)) {
        throw invalidRefreshToken();
    }

    Long memberId = jwtProvider.extractMemberId(refreshToken);

    String savedTokenHash = refreshTokenRepository.findByMemberId(memberId)
            .orElseThrow(this::invalidRefreshToken);

    if (!refreshTokenHasher.matches(refreshToken, savedTokenHash)) {
        throw invalidRefreshToken();
    }

    return memberId;
}

private BusinessException invalidRefreshToken() {
    return new BusinessException(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
}
```

### AuthRestController

대상:

`backend/app/api-server/src/main/java/com/ecommerce/restcontroller/AuthRestController.java`

```java
/**
 * Refresh Token으로 Access Token을 재발급한다
 */
@PostMapping("/refresh")
public ResponseEntity<MemberResponseDto.Reissue> reissue(
        @RequestBody @Valid MemberRequestDto.Reissue request
) {
    return ResponseEntity.status(HttpStatus.OK)
            .body(memberService.reissue(request));
}

/**
 * Refresh Token을 무효화해 로그아웃한다
 */
@PostMapping("/logout")
public ResponseEntity<MemberResponseDto.Logout> logout(
        @RequestBody @Valid MemberRequestDto.Logout request
) {
    return ResponseEntity.status(HttpStatus.OK)
            .body(memberService.logout(request));
}
```

### SecurityConfig

대상:

`backend/app/api-server/src/main/java/com/ecommerce/config/SecurityConfig.java`

로그인, 재발급, 로그아웃은 Refresh Token 자체를 검증하므로 Access Token 인증 없이 접근을 허용한다

```java
.requestMatchers(
        HttpMethod.POST,
        "/api/v1/auth/login",
        "/api/v1/auth/refresh",
        "/api/v1/auth/logout"
).permitAll()
```

## 처리 흐름

### 로그인

```text
회원 인증
→ Access Token 생성
→ Refresh Token 생성
→ Refresh Token SHA-256 해시
→ Redis에 memberId 기준 저장 및 TTL 설정
→ Access Token과 Refresh Token 반환
```

### Access Token 재발급

```text
요청 Refresh Token null·blank 검사
→ JWT 서명·만료·tokenKind 검사
→ memberId 추출
→ Redis 저장 해시 조회
→ 요청 토큰 해시와 상수 시간 비교
→ 회원 활성 상태 확인
→ Access Token 재발급
```

### 로그아웃

```text
Refresh Token 검증
→ Redis 저장 해시 비교
→ memberId 기준 Redis Key 삭제
```

### 강제 만료

```text
계정 정지·탈퇴·비밀번호 변경
→ memberId 기준 Redis Key 삭제
```

## 자동화 테스트

### MemberServiceTest

- 로그인 성공 시 Access Token과 Refresh Token을 생성한다
- 로그인 성공 시 Refresh Token 해시를 TTL과 함께 저장한다
- 유효한 Refresh Token이면 Access Token을 재발급한다
- 만료·위조·종류 오류 Refresh Token이면 `AUTH_INVALID_REFRESH_TOKEN`을 던진다
- Redis에 토큰이 없거나 해시가 다르면 `AUTH_INVALID_REFRESH_TOKEN`을 던진다
- 비활성 회원의 Refresh Token을 삭제하고 `MEMBER_NOT_ACTIVE`를 던진다
- 로그아웃 성공 시 Redis에서 Refresh Token을 삭제한다

### AuthRestControllerTest

- `POST /api/v1/auth/refresh` 성공 시 200과 Access Token을 반환한다
- 재발급 요청의 Refresh Token이 blank면 400을 반환한다
- `POST /api/v1/auth/logout` 성공 시 200을 반환한다
- 로그아웃 요청의 Refresh Token이 blank면 400을 반환한다

## 완료 전 확인

```bash
./gradlew :backend:app:api-server:test
./gradlew :backend:app:api-server:build
```

구현 완료 후 GitHub Issue #48의 완료 조건을 실제 반영 상태에 맞춰 체크한다
