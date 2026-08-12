---
name: querydsl
description: farm-market-platform에서 QueryDSL로 조회 쿼리를 작성·수정할 때 읽는다. Projection DTO 직접 담기, on절 단건 조인, count 분리 페이징, 동적 조건 분리를 프로젝트 실코드 예제로 담는다.
---

# QueryDSL 조회 작성

`repository/*RepositoryCustom` 선언 → `repository/impl/*RepositoryImpl` 구현.
`JPAQueryFactory`를 주입받고 Q타입은 필드로 둔다.

## Projection으로 DTO에 바로 담는다

Entity를 꺼내 변환하지 않고 필요한 컬럼만 응답 DTO로 채운다.

```java
List<ProductResponseDto.Summary> contents = queryFactory.select(
                Projections.fields(ProductResponseDto.Summary.class,
                        product.id.as("productId"),
                        product.productName,
                        product.price,
                        productImage.imageUrl.as("representativeImageUrl")))
        .from(product)
        .leftJoin(productImage)
            .on(productImage.product.eq(product)
                    .and(productImage.representativeYn.eq("Y")))
        .where(product.productStatus.in(VISIBLE_PRODUCT_STATUS))
        .orderBy(product.id.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
```

컬렉션을 fetch join하면 부모 행이 뻥튀기되므로, 대표 1건은 `leftJoin + on`으로 조건 걸어 붙인다.

## count는 분리하고 null을 방어한다

목록에 붙은 join·orderBy를 빼고 조건만 남겨 가볍게 센다.

```java
Long totalCount = queryFactory.select(product.count())
        .from(product)
        .where(product.productStatus.in(VISIBLE_PRODUCT_STATUS))
        .fetchOne();

return new PageImpl<>(contents, pageable, totalCount == null ? 0 : totalCount);
```

## 동적 조건은 BooleanExpression으로 뺀다

null을 `where`에 넘기면 그 조건은 무시된다. 조건별 private 메서드에서 null을 처리한다.

```java
.where(product.productStatus.in(VISIBLE_PRODUCT_STATUS), nameContains(keyword))

private BooleanExpression nameContains(String keyword) {
    return keyword == null ? null : product.productName.contains(keyword);
}
```