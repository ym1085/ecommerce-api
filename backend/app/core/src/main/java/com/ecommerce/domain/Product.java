package com.ecommerce.domain;

import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.enums.ProductStatus;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Comment("쇼핑몰 판매 상품 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_PRODUCT")
@Entity
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("상품 PK(예: 100)")
    @Column(name = "product_id")
    private Long id;

    @Comment("고객에게 노출하는 상품명(예: 베이직 반팔 티셔츠)")
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Comment("상품 상세 설명(예: 면 100% 오버핏 반팔 티셔츠)")
    @Column(name = "description")
    private String description;

    @Comment("상품 판매 가격(원, 예: 29900)")
    @Column(name = "price", nullable = false)
    private Long price; // Integer -> 21억 -> Long으로 해서 가격 범위를 더 크게 설정

    @Enumerated(EnumType.STRING)
    @Comment("상품 판매 상태(예: ON_SALE, OUT_OF_STOCK, DISCONTINUED)")
    @Column(name = "status", nullable = false)
    private ProductStatus productStatus;

    @Comment("주문 가능한 상품 재고 수량(예: 10)")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Builder
    public Product(String productName,
                   String description,
                   Long price,
                   ProductStatus productStatus,
                   Integer stockQuantity) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.productStatus = productStatus;
        this.stockQuantity = stockQuantity;
    }

    public static Product createProduct(String productName,
                                        String description,
                                        Long price,
                                        Integer stockQuantity) {
        return Product.builder()
                .productName(productName)
                .description(description)
                .price(price)
                .productStatus(ProductStatus.ON_SALE) // 판매중으로 고정
                .stockQuantity(stockQuantity)
                .build();
    }

    /**
     * 관리자: 상품 기본 정보 변경
     * @param productName
     * @param description
     * @param price
     * @param stockQuantity
     */
    public void updateProductInfo(String productName, String description, Long price, Integer stockQuantity) {
        // TODO: productName, descriptio, price, stockQuantity는 DTO 수준에서 NPE 체크 해야함
        if (stockQuantity == null || stockQuantity < 0) {
            throw new IllegalArgumentException("stockQuantity는 0 이상이어야 합니다.");
        }
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;

        // 재고 0 -> 상품 상태 -> 품절 자동 전환 (단, DISCONTINUED/DELETED는 건드리지 않음)
        if (this.stockQuantity == 0 && this.productStatus == ProductStatus.ON_SALE) {
            this.productStatus = ProductStatus.OUT_OF_STOCK; // 품절 전환
        }

        // 재고 복구 -> 품절 -> 판매중 자동 전환
        if (this.stockQuantity > 0 && this.productStatus == ProductStatus.OUT_OF_STOCK) {
            this.productStatus = ProductStatus.ON_SALE; // 판매중 전환
        }
    }

    /**
     * 관리자: 상품 상태 강제 변경
     * @param status
     */
    public void updateProductStatus(ProductStatus status) {
        this.productStatus = status;
    }

    /**
     * 입고 이벤트: 재고 상대값 증가
     * @param quantity 재고 수량
     */
    public void increaseStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("입고 수량은 1 이상이어야 합니다.");
        }
        this.stockQuantity += quantity;
        if (this.productStatus == ProductStatus.OUT_OF_STOCK) {
            this.productStatus = ProductStatus.ON_SALE; // 품절 -> 판매중 전환
        }
    }

    /**
     * 주문 이벤트: 재고 상대값 감소
     * @param quantity 재고 수량
     */
    public void decreaseStock(Integer quantity) {
        if (this.productStatus != ProductStatus.ON_SALE) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_ORDERABLE);
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("차감 수량은 1 이상이어야 합니다.");
        }
        if (this.stockQuantity - quantity < 0) {
            throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }
        this.stockQuantity -= quantity; // 특정 삼품의 재고 수량에서 상품 수량 차감
        if (this.stockQuantity == 0) {
            this.productStatus = ProductStatus.OUT_OF_STOCK; // 판매중 -> 품절 전환
        }
    }
}
