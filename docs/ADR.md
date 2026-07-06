# Architecture Decision Records

## 철학

중소 규모 B2C 쇼핑몰 백엔드 REST API.
표준 Spring 생태계를 준수하고 과도한 추상화를 배제한다.
운영 가능한 수준의 코드 품질과 아키텍처 일관성을 목표로 한다.

---

### ADR-001: Java 17 + Spring Boot 3.4.2

**결정**: Java 17 LTS, Spring Boot 3.4.2 사용. Gradle toolchain으로 JDK 버전 고정.

**이유**: Java 17은 LTS 버전으로 안정성이 보장된다.
Spring Boot 3.x는 `jakarta.*` 네임스페이스를 표준으로 채택하여 Jakarta EE 10 기반 생태계와 정렬된다.
toolchain 설정으로 로컬 환경에 관계없이 동일한 JDK 버전을 보장한다.

**트레이드오프**: Java 21의 가상 스레드(Project Loom) 및 레코드 패턴 등 최신 기능을 사용하지 않는다.

---

### ADR-002: Gradle 멀티모듈 — core / api-server 분리

**결정**: `backend:app:core` (library JAR) + `backend:app:api-server` (bootJar) 2개 모듈로 분리.

**이유**: Entity·Repository·DTO를 `core`에 격리하고 `api-server`가 단방향으로 의존하도록 강제한다.
관심사 분리가 명확해지며, 향후 `core`를 다른 실행 모듈에서 재사용하는 구조로 확장이 용이하다.

**트레이드오프**: 단일 모듈 대비 빌드 설정 복잡도가 증가한다. `settings.gradle`, 각 모듈의 `build.gradle` 관리 비용이 발생한다.

---

### ADR-003: QueryDSL 5.1.0 (jakarta)

**결정**: Spring Data JPA와 QueryDSL 5.1.0 jakarta 버전을 병행 사용. 복잡 쿼리는 `{Domain}RepositoryCustom` + `{Domain}RepositoryImpl` 패턴으로 분리.

**이유**: 동적 조건 쿼리를 타입 안전하게 작성할 수 있다.
JPQL·Native 쿼리의 문자열 기반 오류를 컴파일 타임에 방지한다.
jakarta 버전은 Spring Boot 3.x의 jakarta 네임스페이스와 호환된다.

**트레이드오프**: Q클래스 자동 생성을 위한 APT(Annotation Processing) 빌드 단계가 필요하다.
`src/main/generated/`는 gitignore 처리하며 빌드 시마다 재생성된다.

---

### ADR-004: MySQL 8

**결정**: MySQL 8을 메인 데이터베이스로 사용.

**이유**: 팀 표준 RDB. JSON 컬럼, Window Function, CTE 등 8.x 기능을 활용할 수 있다.
운영 환경과 동일한 DB를 개발에서도 사용하여 방언(Dialect) 차이로 인한 버그를 방지한다.

**트레이드오프**: H2 인메모리 DB 기반의 경량 테스트가 불가하다.
테스트 환경에서는 Testcontainers로 MySQL 컨테이너를 띄워 실제 DB와 동일한 환경에서 검증한다.

---

### ADR-005: Spring 프로파일 분리 — local / dev / prod + secret

**결정**: `application.yml` (공통) + `application-{local|dev|prod}.yml` (환경별) + `application-secret.yml` (시크릿, gitignore).

**이유**: 환경별 설정을 명확히 분리한다. DB 접속 정보·API 키 등 민감 정보를 `application-secret.yml`에 격리하고 소스코드 저장소에 노출되지 않도록 한다.

**트레이드오프**: 새 개발자가 로컬 환경 구성 시 `application-secret.yml`을 수동으로 생성해야 한다.
온보딩 문서에 해당 파일의 필수 키 목록을 별도로 관리해야 한다.
