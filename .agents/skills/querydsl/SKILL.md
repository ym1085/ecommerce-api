---
name: querydsl
description: farm-market-platform에서 QueryDSL 조회 쿼리를 작성하거나 수정할 때 DTO Projection, 조건부 조인, count 분리 페이징과 동적 조건 패턴을 적용한다.
---

# QueryDSL 조회 작성

`repository/*RepositoryCustom`에 계약을 선언하고 `repository/impl/*RepositoryImpl`에 구현한다.
`JPAQueryFactory`를 주입받고 Q타입은 필드로 둔다.

Entity 전체를 가져와 변환하지 말고 필요한 컬럼을 Projection으로 응답 DTO에 직접 담는다.

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

대표 한 건은 컬렉션 fetch join 대신 `leftJoin`과 `on` 조건으로 붙인다.
목록 쿼리와 count 쿼리를 분리하고 count 결과의 null을 방어한다.

```java
Long totalCount = queryFactory.select(product.count())
        .from(product)
        .where(product.productStatus.in(VISIBLE_PRODUCT_STATUS))
        .fetchOne();

return new PageImpl<>(contents, pageable, totalCount == null ? 0 : totalCount);
```

동적 조건은 조건별 `BooleanExpression` 메서드로 분리하고 null이면 해당 조건을 제외한다.
