package com.ecommerce.domain;

import com.ecommerce.common.enums.OrderStatus;
import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_ORDER")
@Entity
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "order_no", nullable = false, unique = true)
    private String orderNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Comment("총 주문 금액")
    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    // 회원(1) -> 주문(N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 주문(1) <- 주문 상품(N)
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 양방향 연관관계 매핑 -> Order <-> OrderItem
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.assignOrder(this);
    }

    @Builder
    public Order(String orderNo, Long totalAmount, Member member) {
        this.orderNo = orderNo;
        this.orderStatus = OrderStatus.ORDERED;
        this.totalAmount = totalAmount;
        this.member = member;
    }

    public static Order createOrder(String orderNo, Long totalAmount, Member member) {
        return Order.builder()
                .orderNo(orderNo)
                .totalAmount(totalAmount)
                .member(member)
                .build();
    }

    /**
     * 주문 결제 완료 처리
     */
    public void completePay() {
        this.orderStatus = OrderStatus.PAID;
    }

    /**
     * 주문 취소 처리
     */
    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCELED;
    }
}
