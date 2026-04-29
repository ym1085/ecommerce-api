package com.ecommerce.domain;

import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "unit_price", nullable = false)
    private Long unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 양방향 연관관계 편의 메서드 -> Order <-> OrderItem
    public void assignOrder(Order order) {
        this.order = order;
    }

    @Builder
    public OrderItem(String productName, Long unitPrice, Integer quantity, Order order, Product product) {
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.order = order;
        this.product = product;
    }

    public static OrderItem create(Order order, Product product, Integer quantity) {
        return OrderItem.builder()
                .productName(product.getProductName())
                .unitPrice(product.getPrice())
                .quantity(quantity)
                .order(order)
                .product(product)
                .build();
    }

    public Long calculateSubtotal() {
        return this.unitPrice * this.quantity;
    }
}
