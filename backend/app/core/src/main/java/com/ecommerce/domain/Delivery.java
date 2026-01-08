package com.ecommerce.domain;

import com.ecommerce.common.enums.DeliveryStatus;
import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 배송 정보 (주문 시점 스냅샷)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_DELIVERY")
@Entity
public class Delivery extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "receiver_name", nullable = false, length = 50)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    @Column(name = "zip_code", nullable = false, length = 10)
    private String zipCode;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "address_detail", length = 200)
    private String addressDetail;

    // 배송 메모
    @Column(name = "request_memo", length = 500)
    private String requestMemo;

    // 배송 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DeliveryStatus status;

    // 택배사
    @Column(name = "courier", length = 50)
    private String courier;

    // 송장 번호
    @Column(name = "tracking_number", length = 50)
    private String trackingNumber;

    // 발송일시
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    // 배송완료일시
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
}