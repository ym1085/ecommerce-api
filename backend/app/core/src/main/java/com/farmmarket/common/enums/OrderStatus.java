package com.farmmarket.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    ORDERED("주문 생성"),
    PAID("결제 완료"),
    CANCELED("주문 취소")
    ;

    private final String description;
}
