package com.ecommerce.domain;

import com.ecommerce.common.enums.ProductStatus;
import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_PRODUCT")
@Entity
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_name", nullable = false, unique = true)
    private String productName;

    @Column(name = "description", nullable = false, unique = true)
    private String description;

    /* precision: 숫자 전체 자리수(소수점 포함), scale: 소수점 아래 자리수를 DB에 정의 */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;

    @Column(name = "stock_quantity", nullable = false)
    private Long stockQuantity;
}
