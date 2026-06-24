# Sandbox E-Commerce API

## Overview

Spring Boot 멀티 모듈 기반의 이커머스 백엔드 API 프로젝트입니다.  
회원, 상품, 주문, 결제, 장바구니 도메인을 직접 설계하고 구현합니다.  
MyBatis, JPA, JWT 인증을 사용합니다.

## Project Structure

```
sandbox-ecommerce-api/
├── backend/
│   └── app/
│       ├── api-server/                  # 실행 모듈
│       │   └── com/ecommerce/
│       │       ├── config/              # WebMvc 설정
│       │       ├── handler/             # 전역 예외 처리
│       │       ├── restcontroller/      # REST API 컨트롤러
│       │       └── service/             # 비즈니스 로직
│       └── core/                        # 공통 라이브러리 모듈
│           └── com/ecommerce/
│               ├── common/
│               │   ├── enums/           # 공통 열거형
│               │   └── utils/           # 공통 유틸
│               ├── config/              # QueryDSL 설정
│               ├── domain/              # JPA Entity
│               ├── dto/
│               │   ├── req/             # 요청 DTO
│               │   └── res/             # 응답 DTO
│               └── repository/          # JPA Repository
├── frontend/
├── build.gradle
└── settings.gradle
```

## Tech Stack

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.4.2 |
| Build | Gradle (멀티 모듈) |
| Database | MySQL 8 |
| ORM | Spring Data JPA + QueryDSL 5.1.0 |
| Auth | JWT |
| Mapper | MyBatis |