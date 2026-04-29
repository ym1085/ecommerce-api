package com.ecommerce.domain;

import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_CART")
@Entity
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItems = new ArrayList<>();

    // 양방향 연관관계 편의 메서드 -> Cart <-> CartItem
    public void addCartItem(CartItem cartItem) {
        this.cartItems.add(cartItem);
        cartItem.assignCart(this);
    }

    @Builder
    public Cart(Member member) {
        this.member = member;
    }

    public static Cart createCart(Member member) {
        return Cart.builder()
                .member(member)
                .build();
    }
}
