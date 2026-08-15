package com.farmmarket.domain;

import com.farmmarket.common.enums.PaymentMethod;
import com.farmmarket.common.enums.PaymentStatus;
import com.farmmarket.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Comment("주문 결제 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_PAYMENT")
@Entity
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("결제 PK(예: 3000)")
    @Column(name = "payment_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Comment("결제 수단(예: CARD, TRANSFER, VIRTUAL_ACCOUNT)")
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Comment("결제 상태(예: READY, PAID, FAILED, CANCELED)")
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Comment("PG사가 발급한 결제 거래번호(예: toss_20260810_123456)")
    @Column(name = "pg_tx_id", unique = true)
    private String pgTxId;

    @Comment("결제 대상 주문 PK(예: 1000)")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Builder
    public Payment(Order order, PaymentMethod paymentMethod) {
        this.order = order;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = PaymentStatus.READY;
    }

    public static Payment createPayment(Order order, PaymentMethod paymentMethod) {
        return Payment.builder()
                .order(order)
                .paymentMethod(paymentMethod)
                .build();
    }

    /**
     * 결제 성공 시 결제 완료로 상태 변경
     * ex) 주문 생성 요청 -> Payment 생성 -> 결제 창 URL 반환 -> 결제 진행(카드 입력 등) -> WebHook 콜백 (pgTxId) -> 결제 성공 -> updatePaid
     */
    public void updatePaid(String pgTxId) {
        this.paymentStatus = PaymentStatus.PAID;
        this.pgTxId = pgTxId;
    }

    /**
     * 결제 실패 시 결제 실패로 상태 변경
     */
    public void updateFailed() {
        this.paymentStatus = PaymentStatus.FAILED;
    }

    /**
     * 결제 취소 시 결제 취소로 상태 변경
     */
    public void updateCanceled() {
        this.paymentStatus = PaymentStatus.CANCELED;
    }
}
