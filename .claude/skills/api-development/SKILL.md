---
name: api-development
description: farm-market-platform에서 새 REST 엔드포인트를 추가할 때 읽는다. Controller·Service 골격, ResponseEntity 반환 형식, BusinessException 예외 흐름을 프로젝트 실코드 예제로 담는다.
---

# REST 엔드포인트 추가

`Domain → Repository → DTO → Service → Controller` 순으로 만든다.
규약 자체는 CLAUDE.md `API 설계 규약`을 따르고, 여기서는 프로젝트 코드가 실제로 어떤 모양인지만 본다.

## Controller

`@RestController`에 두고, 응답은 구체 DTO 타입의 `ResponseEntity.status(...).body(...)`로 반환한다.

```java
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@RestController
public class ProductRestController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto.Summary>> getProducts(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProducts(pageable));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto.Detail> getProductById(@PathVariable Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductById(productId));
    }
}
```

## Service

조회 전용은 클래스에 `@Transactional(readOnly = true)`.
없는 리소스는 `BusinessException(ErrorCode.XXX)`로 던지고, Entity는 DTO 빌더로 변환해 반환한다.

```java
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponseDto.Detail getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        return ProductResponseDto.Detail.builder()
                .productId(product.getId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .build();
    }
}
```

예외는 컨트롤러에서 잡지 않는다. `ExceptionControllerHandler`가 `ErrorCode.getStatus()`로 상태코드를 변환한다.

## 조회 쿼리

Repository 조회를 QueryDSL로 짤 때는 `querydsl` skill을 함께 읽는다.