# farm-market-platform

## 프로젝트 개요

- Java 17(Gradle toolchain 고정), Gradle 멀티모듈
- Spring Boot 3.4.2 — Web MVC, Data JPA, Validation, Security
- MySQL 8 + QueryDSL 5.1.0(jakarta), Redis(refresh token 저장소)
- JWT는 jjwt 0.12.6, Lombok

## 모듈 구조

| 모듈 | 포함 요소 | JAR |
| --- | --- | --- |
| `backend:app:core` | Entity, Repository, DTO, enums·utils, QueryDSL config | 일반 JAR |
| `backend:app:api-server` | RestController, Controller, Service, Handler, config, 메인 클래스 | bootJar |

- 의존 방향은 `api-server` → `core` 단방향이다

## 빌드·테스트 명령어

```bash
./gradlew build                              # 전체 빌드
./gradlew test                               # 전체 테스트
./gradlew :backend:app:api-server:build      # api-server 빌드
./gradlew :backend:app:api-server:test       # api-server 테스트
./gradlew :backend:app:core:test             # core 테스트
```

## 레이어 아키텍처

- IMPORTANT: 비즈니스 로직은 `api-server`의 Service에서만 처리하고, DB 접근은 Repository 인터페이스로만 한다
- IMPORTANT: Entity를 Response로 노출하지 않고 DTO로 변환한다
- Entity·Repository·DTO는 `core`, Service·Controller는 `api-server`에 둔다
- REST는 `restcontroller`(`@RestController`), SSR 뷰는 `controller`(`@Controller`)에 둔다

## 쿼리 작성 패턴

- IMPORTANT: 연관 조회에서 N+1을 만들지 않는다. fetch join·`@EntityGraph`·batch fetch로 한 번에 가져온다
- IMPORTANT: N쪽 컬렉션을 join하면 부모 행이 뻥튀기되므로 `distinct`·`Set`으로 중복을 제거한다.
  컬렉션 fetch join과 페이징을 함께 쓰지 않고 `@BatchSize`·`default_batch_fetch_size`로 대체한다
- 반복문 안에서 쿼리를 실행하지 않고 부모 ID를 모아 `IN` 절 한 번으로 묶는다
- 목록 조회는 반드시 페이징한다. count가 부담되면 count 쿼리를 분리하거나 커서(no-offset) 방식을 쓴다
- 조회 결과는 필요한 컬럼만 Projection으로 DTO에 바로 담는다

## API 설계 규약

- URL은 `/api/v{n}/리소스`(복수형 명사·소문자·하이픈), 동사를 쓰지 않고 행위는 HTTP 메서드로 표현한다
- 식별자는 경로에, 필터·정렬·페이징은 쿼리스트링에 둔다
- 인증만 예외로 `auth` 리소스를 쓰고(`/api/v1/auth/login`) `AuthRestController`로 분리한다
- `GET` 파라미터는 `@RequestParam`으로 개별 선언하고, 바디는 `@RequestBody @Valid`로 받아 요청 DTO의 Bean Validation으로 검증한다
- 조회는 `GET`, 조건에 List·중첩 구조·민감정보가 있으면 `POST` + `@RequestBody`
- 수정은 `PATCH`만 쓰고 `PUT`은 쓰지 않는다. 삭제는 `DELETE /리소스/{id}`로 받고 실제 처리는 soft delete로 한다
- 응답은 `ResponseEntity.status(HttpStatus.XXX).body(dto)`로 반환하고 `ok(...)` 축약형은 쓰지 않는다.
  반환 타입은 구체 DTO로 명시하고 `ResponseEntity<?>`는 쓰지 않으며, 생성은 `201` 그 외 성공은 `200`
- 예외는 컨트롤러에서 잡지 않고 던진다. 변환은 `ExceptionControllerHandler`가 `ErrorCode.getStatus()`로 처리한다

## 코드 스타일

- 짧은 대입문·메서드 호출과 파라미터 두 개 정도의 메서드 선언은 반드시 한 줄로 쓴다
- 공개 클래스·공개 메서드에 무엇·왜·주의점을 담은 한글 Javadoc을 쓴다.
  한 줄짜리도 여러 줄 형식으로 쓰고 HTML 태그를 쓰지 않으며 `@param`·`@return`은 설명이 필요할 때만 쓴다
- 자명한 위임·게터성 코드에는 주석을 쓰지 않는다
- `//` 주석은 복잡한 분기·보안 판단·설계 결정에만 쓰고 끝에 마침표를 붙이지 않는다

## 테스트 작성 규약

- Service·Controller만 작성하고 Repository 파생 쿼리는 생략한다. 대표 성공 1개 + 주요 실패·분기만 작성한다
- Service는 `@ExtendWith(MockitoExtension.class)` + `@InjectMocks`·`@Mock` 순수 단위 테스트
- Controller는 `@WebMvcTest` + `MockMvc` + `@MockitoBean`(`@MockBean` 금지), 보안 경로는 `@Import(SecurityConfig.class)`
- 대상 메서드 1개당 `@Nested` 1개로 묶고, 메서드명은 `should결과_when조건`, `@DisplayName`은 한글로 쓴다
- BDDMockito·AssertJ를 쓰고 given/when/then 주석을 단다.
  `given`은 요청 조건 → 조회 결과 → 분기 유도값 순으로 배치하고 각 Mock이 만드는 흐름을 짧은 한글 주석으로 설명한다
- `src/test/java`에 운영 코드와 동일한 패키지 구조로 배치한다

## 환경 설정

- 비밀값은 `application-secret.yml`에만 둔다 (gitignore 대상, local 프로필 전용, ADR-005)
- `application-dev.yml`·`application-prod.yml`은 커밋 대상이며 비밀값을 넣지 않고 환경변수·Secret Manager로 주입한다
- 플레이스홀더(`jwt.secret: ${JWT_SECRET}`)는 기동 요건이 아니라 필수 환경변수 문서화 용도로만 쓴다
- 환경변수 주입이 전제인 프로퍼티에 기본값(`${JWT_SECRET:xxx}`)을 두지 않는다

## 작업 규칙

- IMPORTANT: 사용자가 "수정해"·"반영해"·"만들어줘"처럼 명시적으로 지시하기 전에는 파일을 생성·수정·삭제하지 않는다 (Logic-First).
  "실시간으로 업데이트"·"바로 보여줘"는 편집 허가가 아니라 파일을 다시 읽고 제안하라는 뜻이다
- 코드 제안 전에 대상 파일을 매번 다시 읽고, 이미 반영된 코드는 다시 제안하지 않는다
- 구현 순서는 Domain → Repository → DTO → Service → Controller → Test다
- 커밋 메시지는 Conventional Commits를 따르고 스코프 괄호는 생략한다.
  본문은 한 줄 요약 뒤 빈 줄을 두고 변경 내용을 `-` 불릿으로 나열한다
- 기능 단위가 완결되면 변경 범위와 추천 커밋 메시지를 안내하고, 지시 없이 커밋하지 않는다
- 각 지침 항목은 규칙 하나만 임팩트 있게 담고, 항목을 늘리지 않는다.
  한 항목이 너무 길어지면 쪼개지 말고 들여쓰기 줄바꿈으로 이어 적는다
- 이 파일의 규칙을 바꾸면 `AGENTS.md`도 함께 수정한다

## 설명 형식

- 코드·로직 분석·설명·검토·제안·수정 보고에 적용하고, 단순 질의응답·한두 문장 답변에는 강제하지 않는다
- 맨 앞 `## ` 한 줄로 결론을 두괄식으로 쓴다. 제목만 읽고도 무엇을 어떻게 할지 판단되게 쓴다
- 이어 `### ` 고정 헤더로 나누고 상황별로 이름을 바꾸지 않는다.
  수정·버그는 `무엇이 문제인가` → `어떻게 고치나` → `남은 결정`, 신규 구현은 `설계` → `구현` → `남은 결정`, 분석·리뷰는 `근거`만 둔다
- 인과·판단·근거는 문장으로 서술하고 `-` 불릿은 병렬 나열에만 쓴다. 짧은 불릿을 줄줄이 늘어놓지 않는다
- 수정 지점이 여러 개면 결론과 원인은 한 번만 쓰고 `#### 파일명:라인 — 요약` 아래에 지점별 코드를 반복한다.
  지점이 5개를 넘으면 표로 목록을 먼저 제시하고 코드를 잇는다
- 위치는 항상 `파일명:라인`으로 명시하고 "여기"·"이 부분" 같은 표현은 쓰지 않는다
- 변경은 `+`·`-`가 있는 diff 블록, 신규·최종 코드는 해당 파일 언어의 코드 블록을 쓴다.
  발췌는 메서드 시그니처~닫는 중괄호를 기본 단위로 삼아 준비·분기·결과가 한 눈에 들어오게 한다. 변경 줄만 오려내지 않는다.
  생략은 `...` 한 줄로 표기하되 이해에 필요한 조건·의존성·반환 처리는 생략하지 않는다
- 서브에이전트 결과는 그대로 옮기지 않고 이 형식으로 다시 쓴다. 결과가 충돌하면 채택한 쪽과 근거를 `남은 결정`에 남긴다
- 이모지 마커·번호 제목·구분선(`---`)을 쓰지 않고, 서론·배경·반복 요약·미사여구·HTML 태그를 쓰지 않는다