# Architecture Decision Records

이 문서는 하네스의 모든 Step이 따라야 할 확정된 기술 결정을 기록한다. 결정을 바꾸는 작업은 기존 내용을 조용히 덮어쓰지 않고 ADR을 추가하거나 대체 상태를 명시한다.

## ADR-001: Java 17과 Spring Boot 3.4.2

- **상태**: 채택
- **결정**: Java 17을 Gradle toolchain으로 고정하고 Spring Boot 3.4.2와 `jakarta.*` API를 사용한다.
- **이유**: 실행 환경을 통일하고 Spring Boot 3 생태계와 호환한다.
- **제약**: Java 21 전용 기능과 `javax.*` 기반 라이브러리를 도입하지 않는다.

## ADR-002: core와 api-server 멀티모듈

- **상태**: 채택
- **결정**: `core`는 일반 JAR, `api-server`는 실행 가능한 bootJar로 구성하고 `api-server → core` 단방향 의존만 허용한다.
- **이유**: 영속 모델·저장소·DTO와 실행 애플리케이션의 책임을 분리한다.
- **제약**: Entity·Repository·DTO는 `core`, Service·Controller는 `api-server`에 둔다.

## ADR-003: JPA와 QueryDSL 조회 전략

- **상태**: 채택
- **결정**: 기본 영속성은 Spring Data JPA, 동적 조건과 DTO Projection은 QueryDSL 5.1.0 jakarta Custom Repository로 구현한다.
- **이유**: 단순 조회의 생산성과 복잡 조회의 타입 안전성을 함께 확보한다.
- **제약**: Q클래스는 `src/main/generated`에 생성하고 커밋하지 않는다. 페이징과 컬렉션 fetch join을 함께 사용하지 않는다.

## ADR-004: MySQL 8

- **상태**: 채택
- **결정**: 운영 관계형 데이터베이스로 MySQL 8을 사용한다.
- **이유**: 운영 DB와 동일한 방언과 제약조건을 기준으로 개발한다.
- **제약**: 현재 Service·Controller 테스트는 순수 단위·슬라이스 테스트로 작성한다. DB 통합 테스트를 추가할 때는 H2로 대체하지 않고 Testcontainers 기반 MySQL을 함께 도입한다.

## ADR-005: 프로파일과 비밀값 분리

- **상태**: 채택
- **결정**: 공통, local, dev, prod 설정을 분리하고 local 비밀값은 Git에서 제외된 `application-secret.yml`에만 둔다. dev·prod 비밀값은 환경변수 또는 Secret Manager로 주입한다.
- **이유**: 환경별 설정 차이를 명시하면서 비밀값 커밋을 차단한다.
- **제약**: dev·prod 필수 비밀값 플레이스홀더에 기본값을 두지 않는다. `application-secret.example.yml`에는 실제 비밀값을 넣지 않는다.

## ADR-006: 상품 이미지 경로 저장

- **상태**: 채택
- **결정**: DB에는 이미지 경로만 저장하고 응답 시 `ImageUrlConverter`가 환경별 base URL과 조합한다. 대표 이미지는 `representativeYn='Y'`로 구분한다.
- **이유**: 호스트가 바뀌어도 저장 데이터를 수정하지 않기 위해서다.
- **제약**: 목록은 대표 이미지 한 건만 조회하고, 상세 이미지는 `displayOrder` 오름차순으로 조회하며 N+1을 만들지 않는다.

## ADR-007: JWT와 Spring Security 인증

- **상태**: 채택
- **결정**: Spring Security의 stateless filter chain과 jjwt 0.12.6을 사용한다. Access Token에는 인증 주체와 역할을 담고 보호 API는 JWT 필터에서 인증한다.
- **이유**: 서버 HTTP 세션 없이 REST API 인증을 유지한다.
- **제약**: 공개 경로는 `SecurityConfig`에 명시한다. 비밀번호·JWT secret·토큰 원문을 로그나 응답 오류에 노출하지 않는다.

## ADR-008: Redis 토큰 저장소

- **상태**: 채택
- **결정**: Refresh Token은 해시 후 회원 ID 기준으로 Redis에 TTL과 함께 저장하고, 로그아웃한 Access Token JTI는 남은 수명 동안 blacklist에 저장한다.
- **이유**: Refresh Token 탈취 피해를 줄이고 로그아웃을 즉시 반영한다.
- **제약**: Redis 구현은 `core.repository.redis`, 사용 흐름은 `api-server` Service에 둔다. Redis 장애와 DB 트랜잭션을 원자적으로 가정하지 않는다.

## ADR-009: MyBatis 사용 범위

- **상태**: 채택
- **결정**: MyBatis는 통계·집계처럼 SQL 형태를 직접 제어해야 하는 읽기 쿼리에만 사용한다.
- **이유**: JPA·QueryDSL로 복잡한 집계를 우회 구현하는 비용을 줄인다.
- **제약**: 일반 CRUD와 상태 변경은 JPA Repository로 처리한다. Mapper 인터페이스와 XML은 `core`에 둔다.

## ADR-010: API와 오류 응답

- **상태**: 채택
- **결정**: REST 경로는 `/api/v{n}/resources` 형식을 사용하고 수정은 PATCH, 삭제는 DELETE와 soft delete를 사용한다. 오류는 `BusinessException → ErrorCode → ExceptionControllerHandler` 흐름으로 변환한다.
- **이유**: 클라이언트가 성공·실패 계약을 일관되게 처리하도록 한다.
- **제약**: Controller에서 예외를 잡지 않는다. Entity를 응답하지 않는다. 생성은 201, 그 외 성공은 200을 사용하고 구체 DTO 타입의 `ResponseEntity.status(...).body(...)`를 반환한다.

## ADR-011: Service 트랜잭션 경계

- **상태**: 채택
- **결정**: 비즈니스 로직과 트랜잭션 경계는 Service에 둔다. 기본 조회는 read-only, 상태 변경은 쓰기 트랜잭션으로 실행한다.
- **이유**: 여러 Repository 호출과 도메인 상태 변경을 하나의 유스케이스 단위로 관리한다.
- **제약**: Controller와 Repository 구현체에 비즈니스 분기를 두지 않는다. 외부 시스템 호출을 DB 트랜잭션과 원자적이라고 가정하지 않는다.

## ADR-012: 주문 재고 동시성

- **상태**: 채택
- **결정**: 주문 생성 시 대상 상품을 ID 오름차순으로 비관적 잠금하고 재고 검증·차감과 주문 저장을 같은 트랜잭션에서 처리한다.
- **이유**: 초과 판매를 막고 잠금 순서를 통일해 데드락 가능성을 낮춘다.
- **제약**: 상품별 반복 조회를 하지 않고 한 번의 `IN` 조회로 잠근다. 동일 상품이 중복된 주문 요청은 저장 전에 거부한다.

## 미결정 사항

- 결제 PG와 승인·취소·웹훅 처리 방식
- 결제 멱등키와 실패 보상 정책
- 관리자 상품 관리의 상세 권한과 상태 전이

미결정 사항이 구현 결과를 바꾸면 하네스는 임의의 기술을 선택하지 않고 사용자 결정을 받거나 해당 Step을 `blocked`로 기록한다.
