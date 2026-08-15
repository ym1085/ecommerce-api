# 아키텍처

## 1. 시스템 경계

현재 시스템은 Spring Boot 기반 REST API 서버다. 영속 데이터는 MySQL, Refresh Token과 Access Token 차단 정보는 Redis에 저장한다. 프론트엔드와 외부 결제 시스템은 현재 시스템 경계 밖에 있다.

```text
Client
  → Spring Security / JWT Filter
  → RestController
  → Service / Transaction
  → Repository
      ├─ MySQL: JPA · QueryDSL · 제한적 MyBatis
      └─ Redis: token repositories
```

## 2. 모듈과 의존 방향

```text
backend:app:api-server → backend:app:core
```

- `core`: Entity, Repository, DTO, enum, 공통 예외·유틸, QueryDSL·MyBatis 설정
- `api-server`: 애플리케이션 시작점, Security/JWT, Controller, Service, 예외 Handler, 응답 보조 기능
- `core`는 `api-server`의 클래스를 참조하지 않는다.

## 3. 패키지 배치

```text
backend/app/core/src/main/java/com/farmmarket/
├── domain/                 # JPA Entity
├── repository/             # 저장소 인터페이스
│   ├── impl/               # QueryDSL 구현체
│   └── redis/              # Redis 저장소 구현체
├── dto/req/                # 요청 DTO
├── dto/res/                # 응답 DTO
├── mapper/                 # 통계·집계 전용 MyBatis Mapper
├── common/enums/           # 상태와 ErrorCode
├── common/exception/       # 공통 비즈니스 예외
├── common/utils/           # 공통 유틸
└── config/                 # QueryDSL·MyBatis 설정

backend/app/api-server/src/main/java/com/farmmarket/
├── restcontroller/         # 사용자 REST API
├── admin/restcontroller/   # 관리자 REST API
├── service/                # 사용자 비즈니스 로직
├── admin/service/          # 관리자 비즈니스 로직
├── jwt/                    # 토큰 생성·검증과 인증 필터
├── config/                 # Security·MVC·JPA 설정과 properties
├── handler/                # 전역 예외 변환
└── support/                # API 서버 전용 변환 보조 기능
```

테스트는 운영 코드와 같은 패키지 구조로 `api-server/src/test/java`에 둔다.

## 4. 레이어 책임

| 레이어 | 책임 | 금지 |
| --- | --- | --- |
| Controller | HTTP 입력·인증 정보 전달·응답 상태 결정 | 비즈니스 분기, Repository 직접 호출 |
| Service | 비즈니스 검증·상태 변경·트랜잭션·DTO 변환 조정 | HTTP 응답 생성 |
| Repository | MySQL·Redis 접근과 조회 최적화 | 비즈니스 정책 결정 |
| Entity | 영속 상태와 자체 불변식 유지 | Controller 응답으로 직접 노출 |

구현 순서는 Domain → Repository → DTO → Service → Controller → Test다.

## 5. 요청과 예외 흐름

```text
HTTP Request
  → SecurityFilterChain
  → JwtAuthenticationFilter
  → RestController
  → Service
  → Repository
  → Response DTO
  → ResponseEntity
```

- 공개 경로는 SecurityConfig에 명시하고 나머지는 인증을 요구한다.
- Service의 `BusinessException`은 `ExceptionControllerHandler`가 `ErrorCode`의 상태·코드·메시지로 변환한다.
- Bean Validation 실패와 읽을 수 없는 JSON은 공통 오류 응답으로 변환한다.
- Controller에서 예외를 잡거나 임의의 오류 응답을 만들지 않는다.

## 6. 트랜잭션과 데이터 정합성

- Service 클래스의 기본 조회 경계는 `@Transactional(readOnly = true)`다.
- 상태 변경 메서드에 `@Transactional`을 선언한다.
- DB 상태 변경과 Redis 변경을 하나의 원자적 트랜잭션으로 간주하지 않는다. 실패 보상이 필요한 흐름은 별도 ADR을 먼저 작성한다.
- 재고 차감은 상품을 ID 오름차순으로 잠근 뒤 검증·차감·주문 저장을 같은 DB 트랜잭션에서 처리한다.
- 삭제 API는 실제 행 삭제 대신 도메인 상태 또는 삭제 시각을 변경한다.

## 7. 조회 전략

- 단순 CRUD는 Spring Data JPA 파생 쿼리를 사용한다.
- 동적 조건·DTO Projection·복잡한 조인은 QueryDSL Custom Repository를 사용한다.
- MyBatis는 통계·집계처럼 SQL 제어가 필요한 조회에만 사용하고 일반 CRUD에 사용하지 않는다.
- 반복문 안에서 조회하지 않고 ID를 모아 `IN` 쿼리 한 번으로 처리한다.
- To-One 연관은 fetch join 또는 EntityGraph로 가져온다.
- To-Many 컬렉션 fetch join과 페이징을 결합하지 않고 batch fetch 또는 분리 조회를 사용한다.
- 컬렉션 조인으로 부모가 중복되면 `distinct` 또는 `Set`으로 제거한다.
- 목록은 필요한 컬럼만 DTO Projection하고 반드시 페이징한다.

## 8. 인증과 상태 저장

- API 서버 세션은 stateless이며 Access Token은 JWT로 전달한다.
- Refresh Token 원문은 저장하지 않고 해시를 Redis에 TTL과 함께 저장한다.
- 로그아웃한 Access Token의 JTI는 남은 만료 시간 동안 Redis blacklist에 저장한다.
- 비밀번호나 토큰 원문을 로그와 오류 응답에 남기지 않는다.

## 9. 환경 설정

- `application.yml`: 공통 설정과 프로파일 그룹
- `application-local.yml`: 로컬 비밀값을 제외한 로컬 설정
- `application-dev.yml`, `application-prod.yml`: 환경변수 주입을 전제로 한 배포 설정
- `application-secret.yml`: local 전용 비밀값이며 Git에서 제외
- `application-secret.example.yml`: 필요한 키의 형식만 문서화하고 실제 비밀값은 포함하지 않음

## 10. Step 검증 기준

- `core`만 변경한 Step: `./gradlew :backend:app:core:test`
- `api-server` 또는 양쪽 모듈을 변경한 Step: `./gradlew :backend:app:api-server:test`
- 기능의 마지막 Step: `./gradlew build`
- 각 Step은 변경 레이어, 선행 산출물, 금지 범위와 실행 가능한 검증 명령을 명시한다.
