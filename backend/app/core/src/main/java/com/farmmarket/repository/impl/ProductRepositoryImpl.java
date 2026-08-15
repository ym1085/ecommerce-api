package com.farmmarket.repository.impl;

import com.farmmarket.common.enums.ProductStatus;
import com.farmmarket.domain.QProduct;
import com.farmmarket.domain.QProductImage;
import com.farmmarket.dto.res.ProductResponseDto;
import com.farmmarket.repository.ProductRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QProduct product = QProduct.product;
    private final QProductImage productImage = QProductImage.productImage;

    private static final List<ProductStatus> VISIBLE_PRODUCT_STATUS = List.of(
            ProductStatus.ON_SALE,
            ProductStatus.UPCOMING,
            ProductStatus.OUT_OF_STOCK
    );

    @Override
    public Page<ProductResponseDto.Summary> findProducts(Pageable pageable) {
        List<ProductResponseDto.Summary> contents = queryFactory.select(
                        Projections.fields(
                                ProductResponseDto.Summary.class,
                                product.id.as("productId"),
                                product.productName,
                                product.price,
                                product.productStatus,
                                productImage.imageUrl.as("representativeImageUrl")
                        ))
                .from(product)
                .leftJoin(productImage)
                    .on(productImage.product.eq(product)
                            .and(productImage.representativeYn.eq("Y")))
                .where(product.productStatus.in(VISIBLE_PRODUCT_STATUS))
                .orderBy(product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory.select(product.count())
                .from(product)
                .where(product.productStatus.in(VISIBLE_PRODUCT_STATUS))
                .fetchOne();

        return new PageImpl<>(contents, pageable, totalCount == null ? 0 : totalCount);
    }
}
