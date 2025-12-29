# Commerce Core API

This repository is for building an e-commerce backend API system. Built with Spring Boot, MyBatis, and JPA, with JWT-based authentication.

## Overview

e-commerce backend API built with Spring Boot multi-module architecture.

## Project Structure

```markdown
commerce-core-api/
├── backend/
│   └── app/
│       ├── api-server/
│       │   ├── src/main/java/
│       │   │   └── com/ecommerce/
│       │   │       ├── SpringbootEcommerceApplication.java
│       │   │       ├── controller/
│       │   │       ├── restcontroller/
│       │   │       ├── service/
│       │   │       ├── config/
│       │   │       ├── handler/
│       │   │       └── common/
│       │   └── build.gradle
│       │
│       └── core/
│           ├── src/main/java/
│           │   └── com/ecommerce/
│           │       ├── domain/
│           │       ├── dto/
│           │       │   ├── req/
│           │       │   └── res/
│           │       ├── exception/
│           │       └── common/
│           └── build.gradle
│
├── frontend/
├── build.gradle
└── settings.gradle
```

- **api-server**: Spring Boot main application, providing REST API endpoints.
- **core**: Common libraries such as domains, DTOs, and exception handling.
- **frontend**: Frontend resource.

## Prerequisites

- Java 17+
- Spring Boot 3.4.2
- Spring Data JPA