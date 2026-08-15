package com.farmmarket.domain;

import com.farmmarket.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Comment("주문에 포함된 상품 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_ORDER_ITEM",
        uniqueConstraints = @UniqueConstraint(columnNames = {"order_id", "product_id"})
)
@Entity
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("주문 상품 PK(예: 2000)")
    @Column(name = "order_item_id")
    private Long id;

    @Comment("주문 당시 상품명(예: 베이직 반팔 티셔츠)")
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Comment("주문 당시 상품 1개 가격(원, 예: 29900)")
    @Column(name = "unit_price", nullable = false)
    private Long unitPrice;

    @Comment("주문한 상품 수량(예: 2)")
    @Column(nullable = false)
    private Integer quantity;

    // 주문 상품(N) -> 주문(1)
    @Comment("주문 상품이 포함된 주문 PK(예: 1000)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 상품(N) -> 주문 상품(N)
    @Comment("주문한 상품 PK(예: 100)")
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
