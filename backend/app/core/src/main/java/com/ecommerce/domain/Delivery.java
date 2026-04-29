package com.ecommerce.domain;

import com.ecommerce.common.enums.DeliveryStatus;
import com.ecommerce.common.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "receiver_phone_number", nullable = false)
    private String receiverPhoneNumber;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(name = "address", nullable = false)
    private String address;

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
