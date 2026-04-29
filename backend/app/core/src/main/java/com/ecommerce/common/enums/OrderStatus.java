package com.ecommerce.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    ORDERED("주문 생성"),
    PAID("결제 완료"),
    SHIPPING("배송중"),
    DELIVERED("배송 완료"),
    COMPLETED("주문 완료"),
    CANCELED("주문 취소");

    private final String description;
}
