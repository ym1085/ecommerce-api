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

## 개발 프로세스

- CRITICAL: 새 기능 구현 시 테스트를 먼저 작성하고, 테스트가 통과하는 구현을 작성한다 (TDD).
- 커밋 메시지는 Conventional Commits 형식을 따른다 (feat:, fix:, docs:, refactor:).
- 승인 없이 파일을 생성하거나 수정하지 않는다 (Logic-First).

## 진행 중 작업

- Member 인증 (회원가입/로그인/소셜) 구현 계획 및 코드 가이드: `docs/member-auth-plan.md` 참고

## 명령어

```bash
./gradlew build                              # 전체 빌드
./gradlew test                               # 전체 테스트
./gradlew :backend:app:api-server:build      # api-server 빌드
./gradlew :backend:app:api-server:test       # api-server 테스트
./gradlew :backend:app:core:test             # core 테스트
```
