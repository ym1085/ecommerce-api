---
name: api-development
description: farm-market-platform에서 새 REST 엔드포인트를 추가하거나 수정할 때 Controller, Service, DTO, 예외 흐름과 ResponseEntity 형식을 적용한다. 백엔드 API 구현 및 설계 작업에 사용한다.
---

# REST 엔드포인트 개발

Domain → Repository → DTO → Service → Controller 순서로 구현한다.
규약은 AGENTS.md를 따르고 먼저 동일한 역할의 기존 코드를 읽어 실제 패턴을 맞춘다.

Controller는 `restcontroller`에 두고 구체 DTO 타입을 반환한다.

```java
@GetMapping("/{productId}")
public ResponseEntity<ProductResponseDto.Detail> getProductById(@PathVariable Long productId) {
    return ResponseEntity.status(HttpStatus.OK).body(productService.getProductById(productId));
}
```

조회 전용 Service에는 `@Transactional(readOnly = true)`를 적용한다.
없는 리소스는 `BusinessException(ErrorCode.XXX)`로 던지고 Entity를 DTO로 변환해 반환한다.
Controller에서 예외를 잡지 않으며 `ExceptionControllerHandler`가 `ErrorCode.getStatus()`로 상태 코드를 변환하게 한다.

Repository 조회를 QueryDSL로 작성할 때는 `querydsl` 스킬도 사용한다.
