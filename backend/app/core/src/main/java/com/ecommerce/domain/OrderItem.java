package com.ecommerce.domain;

import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_ORDER_ITEM",
        uniqueConstraints = @UniqueConstraint(columnNames = {"order_id", "product_id"})
)
@Entity
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @Comment("상품명 스냅샷")
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Comment("상품별 가격 스냅샷")
    @Column(name = "unit_price", nullable = false)
    private Long unitPrice;

    @Comment("주문 수량 스냅샷")
    @Column(nullable = false)
    private Integer quantity;

    // 주문 상품(N) -> 주문(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 상품(N) -> 주문 상품(N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 양방향 연관관계 편의 메서드 -> Order <-> OrderItem
    // protected: 같은 domain 패키지의 Order.addOrderItem()에서만 호출하도록 외부(Service) 차단
    protected void assignOrder(Order order) {
        this.order = order;
    }

    @Builder
    private OrderItem(Product product, Integer quantity) {
        this.productName = product.getProductName();
        this.unitPrice = product.getPrice();
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * 주문 상품 생성
     * 주문 시점의 상품명과 단가를 스냅샷으로 복사해 이후 상품 정보가 바뀌어도 주문 내역은 유지된다
     */
    public static OrderItem createOrderItem(Product product, Integer quantity) {
        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .build();
    }

    /**
     * 상품 단가 * 상품 수량 => 총 주문 금액 계산
     */
    public Long calculateTotalAmount() {
        return this.unitPrice * this.quantity;
    }
}
