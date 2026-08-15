package com.farmmarket.domain;

import com.farmmarket.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Comment("장바구니에 담긴 상품 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_CART_ITEM",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"})
)
@Entity
public class CartItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("장바구니 상품 PK(예: 20)")
    @Column(name = "cart_item_id")
    private Long id;

    @Comment("상품이 담긴 장바구니 PK(예: 10)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Comment("장바구니에 담긴 상품 PK(예: 100)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Comment("장바구니 상품 수량(예: 2)")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Builder
    public CartItem(Cart cart, Product product, Integer quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public static CartItem createCartItem(Cart cart, Product product, Integer quantity) {
        return CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .build();
    }

    /**
     * 장바구니 수량 업데이트
     */
    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
