package com.farmmarket.domain;

import com.farmmarket.common.enums.OrderStatus;
import com.farmmarket.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Comment("회원 주문 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_ORDER")
@Entity
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("주문 PK(예: 1000)")
    @Column(name = "order_id")
    private Long id;

    @Comment("고객에게 노출하는 주문번호(예: ORD-20260810-0001)")
    @Column(name = "order_no", nullable = false, unique = true)
    private String orderNo;

    @Enumerated(EnumType.STRING)
    @Comment("주문 상태(예: ORDERED, PAID, CANCELED)")
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Comment("주문 상품의 총 결제 금액(원, 예: 59800)")
    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    // 회원(1) -> 주문(N)
    @Comment("주문한 회원 PK(예: 1)")
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
    public Order(String orderNo, Member member, List<OrderItem> orderItems) {
        this.orderNo = orderNo;
        this.member = member;
        this.orderStatus = OrderStatus.ORDERED;
        orderItems.forEach(item -> this.addOrderItem(item)); // 양방향 연관관계 매핑
        this.totalAmount = calculateTotalAmount();
    }

    /**
     * 주문 생성 진입점
     * 양방향 연관관계 설정과 총 주문 금액 계산을 이 메서드 안에서 모두 끝낸다
     * 총 금액은 외부에서 주입받지 않고 주문 상품 목록에서 직접 계산한다
     */
    public static Order createOrder(String orderNo, Member member, List<OrderItem> orderItems) {
        return Order.builder()
                .orderNo(orderNo)
                .member(member)
                .orderItems(orderItems)
                .build();
    }

    /**
     * 총 주문금액 계산
     * @return
     */
    private Long calculateTotalAmount() {
        return this.orderItems.stream()
                .mapToLong(OrderItem::calculateTotalAmount)
                .sum();
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
