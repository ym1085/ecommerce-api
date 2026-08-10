# 아키텍처

## 디렉토리 구조

```
farm-market-platform/
├── backend/
│   └── app/
│       ├── core/                          # library JAR
│       │   └── src/main/
│       │       ├── java/com/farmmarket/
│       │       │   ├── domain/            # JPA Entity
│       │       │   ├── repository/        # Spring Data JPA + QueryDSL Custom
│       │       │   │   └── impl/          # QueryDSL 구현체
│       │       │   ├── dto/
│       │       │   │   ├── req/           # Request DTO
│       │       │   │   └── res/           # Response DTO
│       │       │   ├── common/
│       │       │   │   ├── enums/         # 도메인 Enum
│       │       │   │   └── utils/         # BaseTimeEntity 등 공통 유틸
│       │       │   └── config/            # QueryDSL config
│       │       └── generated/             # QueryDSL Q클래스 (자동생성, gitignore)
│       └── api-server/                    # executable bootJar
│           └── src/main/
│               ├── java/com/farmmarket/
│               │   ├── restcontroller/    # @RestController (REST API)
│               │   ├── controller/        # @Controller (SSR/MVC)
│               │   ├── service/           # 비즈니스 로직
│               │   ├── handler/           # ExceptionControllerHandler
│               │   └── config/            # WebMvcConfig 등
│               └── resources/
│                   ├── application.yml            # 공통 설정
│                   ├── application-{profile}.yml  # 프로파일별 설정 (local/dev/prod)
│                   └── sql/                       # 개발용 테스트 데이터 SQL
│                       ├── member/
│                       ├── order/
│                       ├── cart/
│                       └── item/
└── frontend/                              # (미구현)
```

## 모듈 의존 관계

```
api-server → core (단방향)
core는 api-server에 의존하지 않는다.
```

## 패턴

- **Layered Architecture**: RestController → Service → Repository → Entity
- **QueryDSL Custom Repository**: `{Domain}RepositoryCustom` 인터페이스 + `{Domain}RepositoryImpl` 구현체로 복잡 쿼리 분리
- **DTO 변환**: Entity를 Response로 직접 노출하지 않는다. 반드시 DTO(req/res)로 변환
- **프로파일 분리**: local / dev / prod 환경별 설정 분리, secret은 gitignore

## 데이터 흐름

```
HTTP Request
  → @RestController (restcontroller/)
  → Service (비즈니스 로직, 트랜잭션 경계)
  → Repository (DB 접근)
  → Entity (JPA 매핑)
  → DTO 변환
  → HTTP Response
```

## 상태 관리

- 서버 사이드 stateless REST API
- 트랜잭션 경계는 Service 레이어 (@Transactional)
