# 프로젝트: sandbox-ecommerce-api

## 기술 스택

- Java 17 (Gradle toolchain 강제 설정)
- Spring Boot 3.4.2 (Web MVC, Data JPA, Validation)
- Gradle 멀티모듈: `backend:app:core` / `backend:app:api-server` / `frontend`
- MySQL 8 + Spring Data JPA + QueryDSL 5.1.0 (jakarta)
- Lombok

## 모듈 역할

| 모듈                     | 포함 요소                                                                           | JAR 형태                  |
| ------------------------ | ----------------------------------------------------------------------------------- | ------------------------- |
| `backend:app:core`       | Entity(domain), Repository, DTO(req/res), 공통(enums/utils), QueryDSL config        | 일반 JAR (library)        |
| `backend:app:api-server` | RestController, Controller, Service, Handler(ExceptionHandler), config, 메인 클래스 | 실행 가능한 JAR (bootJar) |

## 아키텍처 규칙

- CRITICAL: 비즈니스 로직은 Service 레이어(`api-server`)에서만 처리한다.
- CRITICAL: DB 접근은 반드시 Repository 인터페이스를 통해서만 한다.
- CRITICAL: Entity를 Response로 직접 노출하지 않는다. 반드시 DTO로 변환한다.
- Entity · Repository · DTO는 `core` 모듈에, Service · Controller는 `api-server` 모듈에 위치한다.
- `api-server`는 `core`에 의존할 수 있지만, `core`는 `api-server`에 의존하지 않는다.
- REST API는 `restcontroller` 패키지(@RestController), SSR/MVC 뷰는 `controller` 패키지(@Controller)에 위치한다.

## 요청 규칙

### URL 설계

- URL은 `/api/v{n}/리소스` 형태로 짓는다. 리소스는 **복수형 명사**, 소문자, 단어 구분은 하이픈(`-`)을 쓴다.
- URL에 동사를 쓰지 않는다. 행위는 HTTP 메서드로 표현한다. (`POST /members` O, `POST /members/create` X)
- 리소스 식별자는 경로에(`/members/{memberId}`), 필터·정렬·페이징 조건은 쿼리스트링에 둔다.
- 인증은 예외로 `auth`를 리소스로 둔다(`POST /api/v1/auth/login`). 회원 리소스 조작이 아니라 토큰 발급이므로 `members` 하위에 두지 않으며, 컨트롤러도 `AuthRestController`로 분리한다.

### 요청 바인딩

- `GET` 파라미터는 `@RequestParam`으로 개별 선언한다. 조건이 많아 DTO로 묶는 기준은 실제 검색 API를 만들 때 정한다.
- 바디가 있는 메서드(`POST`/`PATCH`)는 `@RequestBody @Valid`로 받고, 검증은 요청 DTO의 Bean Validation 애노테이션으로 처리한다.
- 조회는 `GET`을 기본으로 한다. 다만 조건에 List·중첩 구조가 들어가거나, 민감정보가 포함되거나, URL 길이 제한을 넘는 경우에는 `POST` + `@RequestBody`로 받는다.
- 수정은 `PATCH`를 기본으로 한다. `PUT`(전체 교체)은 쓰지 않는다.
- 삭제는 `DELETE /리소스/{id}`로 받되, 실제 처리는 논리 삭제(soft delete)로 한다.

## 응답 규칙

- RestController 응답은 `ResponseEntity.status(HttpStatus.XXX).body(응답DTO)` 형태로 상태 코드와 바디를 명시적으로 반환한다. `ResponseEntity.ok(...)` 같은 축약형은 쓰지 않는다.
- 상태 코드 기준: 리소스 생성은 `CREATED(201)`, 조회·로그인 등 그 외 성공은 `OK(200)`.
- 반환 타입은 구체 DTO(`ResponseEntity<MemberResponseDto.Login>`)로 명시한다. `ResponseEntity<?>`는 쓰지 않는다.
- 예외는 컨트롤러에서 잡지 않고 그대로 던진다. 상태 코드 변환은 전역 핸들러(`ExceptionControllerHandler`, `@RestControllerAdvice`)가 `ErrorCode.getStatus()`로 처리한다.

## 설정 규칙

- 실제 비밀값(DB 비밀번호, `jwt.secret` 등)은 `application-secret.yml`에만 둔다. 이 파일은 gitignore 대상이며 local 프로필 전용이다(ADR-005).
- `application-dev.yml` / `application-prod.yml`은 저장소에 커밋된다. 여기에는 비밀값을 절대 넣지 않고, 값은 컨테이너 환경변수나 Secret Manager로 주입한다.
- Spring Boot의 relaxed binding이 환경변수를 프로퍼티에 자동 매핑한다(`JWT_SECRET` → `jwt.secret`). 따라서 dev/prod yml에 `jwt.secret: ${JWT_SECRET}` 같은 플레이스홀더 줄이 없어도 환경변수만 있으면 정상 기동한다.
- 그럼에도 플레이스홀더를 적을 수는 있다. 다만 그건 기동에 필요해서가 아니라 "이 앱이 어떤 환경변수를 요구하는지"를 코드에 남기는 문서화 목적이다.
- 환경변수 주입이 전제인 프로퍼티에는 기본값(`${JWT_SECRET:기본값}`)을 두지 않는다. 운영에서 주입을 누락했을 때 조용히 뜨는 것보다 기동이 실패하는 편이 안전하다.

## 개발 프로세스

- CRITICAL: 기능 구현은 Domain → Repository → DTO → Service → Controller → Test 순서로 진행한다.
  도메인을 먼저 설계하고 상위 레이어를 쌓은 뒤, 테스트는 마지막에 작성한다 (구현 선행, 테스트 후행).
- 커밋 메시지는 Conventional Commits 형식을 따른다 (feat:, fix:, docs:, refactor:).
- 승인 없이 파일을 생성하거나 수정하지 않는다 (Logic-First).
- CRITICAL: 코드 수정의 1차 주체는 사용자다. Claude는 먼저 로직/코드를 제안(설명·스니펫)만 하고, 사용자가 명시적으로 "수정해"라고 지시할 때만 실제 파일을 편집한다. 지시 없이 선제적으로 파일을 고치지 않는다.
- 파일 편집은 사용자가 "수정해/반영해"라고 명시적으로 지시할 때만 한다. "실시간으로 업데이트"·"바로 보여줘" 같은 표현은 파일 편집 허가가 아니라, 현재 파일을 다시 읽어 최신 상태로 제안하라는 뜻으로 해석한다.

## 테스트 규칙

- 테스트는 Service · Controller만 작성한다 (Repository 단순 파생 쿼리는 생략).
- 모든 케이스를 망라하지 않고 핵심 케이스(대표 성공 1개 + 주요 실패/분기)만 작성한다 (기능 개발 속도 우선).
- Service: `@ExtendWith(MockitoExtension.class)` 순수 단위 테스트. `@InjectMocks` 대상 + `@Mock` 의존, 비즈니스 로직·분기·예외 검증.
- Controller: `@WebMvcTest(대상.class)` + `MockMvc` 웹 슬라이스. Service 의존은 `@MockitoBean` 사용(`@MockBean`은 3.4부터 deprecated). URL·HTTP Method·`@Valid`·Status·Response Body 검증.
- 테스트 대상 메서드 1개당 `@Nested` 1개로 묶는다.
- 메서드명은 영문 `should결과_when조건`(예: `shouldReturn201_whenSignUpSuccess`), `@DisplayName`은 한글로 작성한다.
- 스타일: BDDMockito(`given().willReturn()`), AssertJ(`assertThat`, `assertThatThrownBy`), given/when/then 주석.
- 테스트는 `src/test/java`에 운영 코드와 동일한 패키지 구조로 배치한다.

## 로직/코드 설명 방식

- 구현 전 로직을 설명할 때는 코드를 완전히 생략하지 말고, 핵심이 드러나는 60% 수준의 실제 코드를 보여준다.
- 코드와 함께 처리 흐름(순서, 분기, 의존 관계)을 이해하기 쉽게 짚어준다.
- 짧은 대입문과 메서드 호출은 불필요하게 여러 줄로 나누지 않고 한 줄로 작성한다.
- 로직이나 코드를 제안할 때는 대상 클래스의 패키지와 파일 경로를 먼저 밝히고, 기존 파일은 현재 라인 번호를 함께 표기한다. 새 파일은 `[신규]`로 표시한다.
- 코드 제안은 먼저 `## 목적` 한 줄로 기능의 이유를 밝힌다. 이어서 `[신규]` 또는 `[기존 수정]` 패키지·경로·라인, 코드, 역할 설명 순서로 필요한 파일만 제시한다.
- 사용자가 코드·로직 예시를 요청하면 위 형식의 코드부터 바로 제시한다. 요청하지 않은 장황한 배경 설명·반복 요약은 덧붙이지 않는다.
- CRITICAL: 코드를 제안하기 전에 항상 대상 파일의 현재 상태를 실시간으로 읽는다. 사용자가 이미 직접 수정했을 수 있으므로, 요약/기억에 의존하지 말고 매번 파일을 다시 읽어 최신 상태를 기준으로 제안한다.
- 사용자가 이미 반영한 코드를 다시 제안하지 않는다. 읽어서 이미 적용된 것을 확인하면, 반복 제시하지 말고 다음 단계로 넘어간다.

## 주석 규칙

- 공개 클래스·공개 메서드에는 무엇을·왜·주의점을 설명하는 깔끔한 한글 Javadoc을 작성한다. 복잡한 분기·보안 판단·설계 결정에만 `//` 라인 주석을 작성한다.
- 이름만으로 자명한 단순 위임·게터성 코드에는 주석을 작성하지 않는다.
- Javadoc은 한 줄짜리라도 여러 줄 형식(`/**` 줄바꿈 후 `*` 본문, 줄바꿈 `*/`)으로 작성한다. 한 줄 `/** ... */`는 쓰지 않는다.
- 코드 예시에도 클래스·공개 메서드의 핵심 의도가 드러나는 깔끔한 한글 Javadoc을 함께 보여준다. HTML 태그(`</p>` 등)는 Javadoc에 쓰지 않는다. `@param`, `@return`은 설명이 필요할 때만 추가하며, 형식적으로 강제하지 않는다.
- 주석 끝에 마침표(`.`)를 붙이지 않는다.

```bash
./gradlew build                              # 전체 빌드
./gradlew test                               # 전체 테스트
./gradlew :backend:app:api-server:build      # api-server 빌드
./gradlew :backend:app:api-server:test       # api-server 테스트
./gradlew :backend:app:core:test             # core 테스트
```
