# 주말농장 제철과일 판매 플랫폼

## Overview

주말농장에서 직접 수확한 제철 과일을 온라인으로 판매하기 위해 개발하는 쇼핑몰 프로젝트입니다.

React 기반 프론트엔드와 Spring Boot 기반 백엔드를 하나의 저장소에서 관리합니다.

## Project Structure

```text
ecommerce-api/
├── backend/
│   └── app/
│       ├── api-server/    # API 및 비즈니스 로직
│       └── core/          # Entity, Repository, DTO
├── frontend/              # 사용자 화면
├── build.gradle
└── settings.gradle
```

백엔드는 `api-server`가 `core`를 참조하는 단방향 멀티모듈 구조입니다.

## Tech Stack

| 구분 | 기술 |
| --- | --- |
| Frontend | React 18, TypeScript 5.5, Vite 5 |
| Styling | Tailwind CSS 4 |
| State | TanStack Query, Zustand |
| Backend | Java 17, Spring Boot 3.4.2 |
| Database | MySQL 8, Redis |
| Data Access | Spring Data JPA, QueryDSL 5.1.0, MyBatis |
| Authentication | Spring Security, JWT |
| Build | Gradle Multi-Module |

## Getting Started

### Backend

MySQL과 Redis를 실행한 후 로컬 설정 파일을 생성합니다.

```bash
cp backend/app/api-server/src/main/resources/application-secret.example.yml \
   backend/app/api-server/src/main/resources/application-secret.yml
```

`application-secret.yml`의 데이터베이스와 JWT 설정을 로컬 환경에 맞게 수정한 후 실행합니다.

```bash
./gradlew :backend:app:api-server:bootRun
```

백엔드는 `http://localhost:8080`에서 실행됩니다.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

프론트엔드는 `http://localhost:5173`에서 실행됩니다.
