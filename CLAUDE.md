# sandbox-ecommerce-api

## 01. 기술 스택

- Java 17 (Gradle toolchain 고정), Gradle 멀티모듈
- Spring Boot 3.4.2 — Web MVC, Data JPA, Validation, Security
- MySQL 8 + QueryDSL 5.1.0 (jakarta), Redis (refresh token 저장소)
- JWT는 jjwt 0.12.6, Lombok

## 02. 모듈 구조

| 모듈 | 포함 요소 | JAR |
| --- | --- | --- |
| `backend:app:core` | Entity, Repository, DTO, enums·utils, QueryDSL config | 일반 JAR |
| `backend:app:api-server` | RestController, Controller, Service, Handler, config, 메인 클래스 | bootJar |

## 03. 아키텍처 규칙

- CRITICAL: 비즈니스 로직은 `api-server`의 Service에서만 처리한다
- CRITICAL: DB 접근은 Repository 인터페이스로만 한다
- CRITICAL: Entity를 Response로 노출하지 않고 DTO로 변환한다
- Entity·Repository·DTO는 `core`, Service·Controller는 `api-server`에 둔다
- 의존 방향은 `api-server` → `core` 단방향이다
- REST는 `restcontroller`(`@RestController`), SSR 뷰는 `controller`(`@Controller`)에 둔다

## 04. API 규칙

- URL은 `/api/v{n}/리소스`, 복수형 명사, 소문자, 하이픈 구분
- URL에 동사를 쓰지 않고 행위는 HTTP 메서드로 표현한다
- 식별자는 경로에, 필터·정렬·페이징은 쿼리스트링에 둔다
- 인증만 예외로 `auth` 리소스를 쓰고(`/api/v1/auth/login`) `AuthRestController`로 분리한다
- `GET` 파라미터는 `@RequestParam`으로 개별 선언한다
- 바디가 있는 메서드는 `@RequestBody @Valid`로 받고 검증은 요청 DTO의 Bean Validation으로 한다
- 조회는 `GET`, 조건에 List·중첩 구조·민감정보가 있으면 `POST` + `@RequestBody`
- 수정은 `PATCH`만 쓰고 `PUT`은 쓰지 않는다
- 삭제는 `DELETE /리소스/{id}`로 받고 실제 처리는 soft delete로 한다
- 응답은 `ResponseEntity.status(HttpStatus.XXX).body(dto)`로 반환하고 `ok(...)` 축약형은 쓰지 않는다
- 생성은 `201`, 그 외 성공은 `200`
- 반환 타입은 구체 DTO로 명시하고 `ResponseEntity<?>`는 쓰지 않는다
- 예외는 컨트롤러에서 잡지 않고 던진다. 변환은 `ExceptionControllerHandler`가 `ErrorCode.getStatus()`로 처리한다

## 05. 설정 규칙

- 비밀값은 `application-secret.yml`에만 둔다 (gitignore 대상, local 프로필 전용, ADR-005)
- `application-dev.yml`·`application-prod.yml`은 커밋 대상이며 비밀값을 넣지 않고 환경변수·Secret Manager로 주입한다
- 플레이스홀더(`jwt.secret: ${JWT_SECRET}`)는 기동 요건이 아니라 필수 환경변수 문서화 용도로만 쓴다
- 환경변수 주입이 전제인 프로퍼티에 기본값(`${JWT_SECRET:xxx}`)을 두지 않는다

## 06. 코드 스타일

- 짧은 대입문과 메서드 호출은 한 줄로 쓴다
- 메서드 선언은 파라미터가 매우 많을 때만 줄바꿈하고 두 개 정도는 반드시 한 줄로 쓴다
- 공개 클래스·공개 메서드에 무엇·왜·주의점을 담은 한글 Javadoc을 쓴다
- 자명한 위임·게터성 코드에는 주석을 쓰지 않는다
- Javadoc은 한 줄짜리도 여러 줄 형식으로 쓰고 HTML 태그를 쓰지 않는다
- 코드 예시·제안에는 `<p>` 등 HTML 태그 없는 간결한 한글 Javadoc과 필요한 `//` 주석을 함께 보여준다
- `@param`·`@return`은 설명이 필요할 때만 쓴다
- `//` 주석은 복잡한 분기·보안 판단·설계 결정에만 쓴다
- 주석 끝에 마침표를 붙이지 않는다

## 07. 테스트 규칙

- Service·Controller만 작성하고 Repository 파생 쿼리는 생략한다
- 대표 성공 1개 + 주요 실패·분기만 작성한다
- Service는 `@ExtendWith(MockitoExtension.class)` + `@InjectMocks`·`@Mock` 순수 단위 테스트
- Controller는 `@WebMvcTest` + `MockMvc` + `@MockitoBean`(`@MockBean` 금지), 보안 경로는 `@Import(SecurityConfig.class)`
- 대상 메서드 1개당 `@Nested` 1개로 묶는다
- 메서드명은 `should결과_when조건`, `@DisplayName`은 한글로 쓴다
- BDDMockito·AssertJ를 쓰고 given/when/then 주석을 단다
- 테스트 코드와 테스트 제안의 `given`은 요청 조건 → 조회 결과 → 분기 유도값 순으로 배치하고, 각 Mock 동작이 만드는 흐름을 짧은 한글 주석으로 설명한다
- `src/test/java`에 운영 코드와 동일한 패키지 구조로 배치한다

## 08. 코드 성능

- Java 로직은 시간·공간복잡도를 고려해 작성한다. 선형 탐색이 반복되면 Map·Set으로 O(1) 조회로 바꾼다
- 중첩 반복으로 O(n²)이 되는 구간은 자료구조·사전 인덱싱으로 낮춘다
- 같은 데이터를 여러 번 순회하지 않고 한 번의 순회로 처리하며, 반복 계산 결과는 재사용한다
- 대용량은 전체를 메모리에 올리지 않고 페이징·스트림으로 나눠 처리한다
- 반복문 안에서 문자열을 `+`로 잇지 않고 `StringBuilder`를 쓴다
- 컬렉션은 예상 크기로 초기 용량을 지정해 잦은 리사이즈를 피한다
- 알고리즘 선택이 성능을 좌우하는 로직은 제안 설명에 시간·공간복잡도를 한 줄로 밝힌다

## 09. DB 성능

- SQL은 인덱스를 타게 작성한다. 인덱스를 무력화하는 조건(선두 `LIKE '%x'`, 컬럼 가공·함수 적용)을 피한다
- 필요한 컬럼만 조회하고 `SELECT *`를 쓰지 않는다. 조회 결과는 Projection으로 DTO에 바로 담는다
- 목록 조회는 반드시 페이징하고, 무제한 전체 조회를 하지 않는다
- 페이징 count가 부담되면 count 쿼리를 분리하거나 커서(no-offset) 방식을 쓴다
- 반복문 안에서 쿼리를 실행하지 않고 `IN` 절 한 번으로 묶는다
- 대량 삽입·수정은 건별 실행 대신 batch·벌크 연산으로 처리한다
- CRITICAL: 연관 조회에서 N+1을 만들지 않는다. fetch join·`@EntityGraph`·batch fetch로 한 번에 가져온다

## 10. 작업 방식

- CRITICAL: 사용자가 "수정해"·"반영해"·"만들어줘"처럼 명시적으로 편집을 지시하기 전에는 파일을 생성·수정·삭제하지 않는다 (Logic-First)
- "실시간으로 업데이트"·"바로 보여줘"는 편집 허가가 아니라 파일을 다시 읽고 제안하라는 뜻이다
- 코드 제안 전에 대상 파일을 매번 다시 읽고, 이미 반영된 코드는 다시 제안하지 않는다
- 구현 순서는 Domain → Repository → DTO → Service → Controller → Test다
- 제안 형식은 `## 목적` 한 줄 → `[신규]`/`[기존 수정]` 패키지·경로·라인 → 코드 → 역할 순이다
- 로직 설명에는 핵심이 드러나는 60% 수준의 실제 코드와 처리 흐름을 함께 제시한다
- 요청하지 않은 배경 설명·반복 요약을 덧붙이지 않는다
- 커밋 메시지는 Conventional Commits를 따르고 스코프 괄호는 생략한다.
  본문은 한 줄 요약 뒤 빈 줄을 두고 변경 내용을 `-` 불릿으로 나열한다
- 기능 단위가 완결되면 변경 범위와 추천 커밋 메시지를 안내하고, 지시 없이 커밋하지 않는다
- 각 지침 항목은 규칙 하나만 임팩트 있게 담고, 항목을 늘리지 않는다.
  한 항목이 너무 길어지면 쪼개지 말고 들여쓰기 줄바꿈으로 이어 적는다
- 이 파일의 규칙을 바꾸면 `AGENTS.md`도 함께 수정한다

## 11. 빌드 명령어

```bash
./gradlew build                              # 전체 빌드
./gradlew test                               # 전체 테스트
./gradlew :backend:app:api-server:build      # api-server 빌드
./gradlew :backend:app:api-server:test       # api-server 테스트
./gradlew :backend:app:core:test             # core 테스트
```

## 12. 코드·로직 설명 형식

- 코드·로직 분석·설명·검토·제안·수정 보고에 적용하고, 단순 질의응답·한두 문장 답변에는 강제하지 않는다
- 위치는 항상 `파일명:라인`으로 명시하고 "여기"·"이 부분" 같은 표현은 쓰지 않는다
- 기존 수정은 `🔧 **[기존 수정]**`, 신규는 `✅ **[신규]**`로 표시한다
  마커 다음 줄에 저장소 루트 기준 전체 경로를 쓰고, 이후 위치 표기는 `파일명.java:라인`만 쓴다
- 제목은 최상위 `# 01. <주제>`, 각 항목 `# 1.`·`# 2.`로 쓰며 `##` 이하 제목은 쓰지 않는다
  각 항목 제목 위에는 `---` 구분선을 둔다
- 위치를 짚으면 실제 코드를 반드시 함께 제시한다
  변경 비교는 `+`·`-`가 있는 diff 블록, 신규·최종 코드는 java 블록을 사용한다
  발췌에는 메서드 시그니처와 호출 전후의 핵심 준비·분기·결과 코드를 포함해,
  해당 로직만 읽어도 흐름을 이해할 수 있는 범위로 제시한다
  생략은 `...` 한 줄로 표기하되, 이해에 필요한 조건·의존성·반환 처리는 생략하지 않는다
- 코드와 함께 처리 흐름의 순서·분기·의존 관계를 짚는다
  코드 아래 한두 문장으로 핵심을 먼저 말하고, 이어 `- **키워드** — 설명` 불릿으로 근거를 제시한다
  확인이 필요한 사항은 `**확인 필요**`로 분리한다
- 서론·배경·반복 요약·미사여구·요청하지 않은 대안 나열과 HTML 태그를 쓰지 않는다
- 코드 예시는 코드부터 제시하고, 클래스와 public 메서드에는 HTML 태그 없는 한글 Javadoc을 작성한다
  `@param`·`@return`은 설명이 필요할 때만 쓴다
