package com.ecommerce.domain;

import com.ecommerce.common.enums.DeliveryStatus;
import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Comment("주문 배송 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_DELIVERY")
@Entity
public class Delivery extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("배송 PK(예: 30)")
    @Column(name = "delivery_id")
    private Long id;

    @Comment("배송 대상 주문 PK(예: 1000)")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Comment("배송 상태(예: READY, SHIPPING, DELIVERED)")
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus;

    @Comment("택배 운송장 번호(예: 123456789012)")
    @Column(name = "tracking_number")
    private String trackingNumber;

    @Comment("상품 수령인 이름(예: 홍길동)")
    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Comment("상품 수령인 전화번호(예: 010-1234-5678)")
    @Column(name = "receiver_phone_number", nullable = false)
    private String receiverPhoneNumber;

    @Comment("배송지 우편번호(예: 06236)")
    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Comment("배송지 기본 주소(예: 서울특별시 강남구 테헤란로 123)")
    @Column(name = "address", nullable = false)
    private String address;

    @Comment("배송지 상세 주소(예: 101동 1001호)")
    @Column(name = "address_detail")
    private String addressDetail;

    @Builder
    public Delivery(Order order,
                    String receiverName,
                    String receiverPhoneNumber,
                    String zipCode,
                    String address,
                    String addressDetail) {
        this.order = order;
        this.deliveryStatus = DeliveryStatus.READY;
        this.receiverName = receiverName;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.zipCode = zipCode;
        this.address = address;
        this.addressDetail = addressDetail;
    }

    public static Delivery createDelivery(Order order,
                                          String receiverName,
                                          String receiverPhoneNumber,
                                          String zipCode,
                                          String address,
                                          String addressDetail) {
        return Delivery.builder()
                .order(order)
                .receiverName(receiverName)
                .receiverPhoneNumber(receiverPhoneNumber)
                .zipCode(zipCode)
                .address(address)
                .addressDetail(addressDetail)
                .build();
    }

    /**
     * 배송 시작 시 배송 중으로 상태 변경 (물류 API 콜백)
     */
    public void startShipping(String trackingNumber) {
        this.deliveryStatus = DeliveryStatus.SHIPPING;
        this.trackingNumber = trackingNumber;
    }

    /**
     * 배송 완료 시 상태 변경 (물류 API 콜백)
     */
    public void completeDelivery() {
        this.deliveryStatus = DeliveryStatus.DELIVERED;
    }

    /**
     * 구매 확정 시 상태 변경 (고객 직접 확정 또는 자동 확정)
     */
    public void confirmPurchase() {
        this.deliveryStatus = DeliveryStatus.COMPLETED;
    }
}
