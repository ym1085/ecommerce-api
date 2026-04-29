package com.ecommerce.domain;

import com.ecommerce.common.enums.OrderStatus;
import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Order(String orderNo, Integer totalAmount, Member member) {
        this.orderNo = orderNo;
        this.orderStatus = OrderStatus.ORDERED;
        this.totalAmount = totalAmount;
        this.member = member;
    }

    public static Order createOrder(String orderNo, Integer totalAmount, Member member) {
        return Order.builder()
                .orderNo(orderNo)
                .totalAmount(totalAmount)
                .member(member)
                .build();
    }

    /**
     * 주문 결제 완료 처리
     */
    public void paid() {
        this.orderStatus = OrderStatus.PAID;
    }

    /**
     * 주문 취소 처리
     */
    public void cancel() {
        this.orderStatus = OrderStatus.CANCELED;
    }
}
