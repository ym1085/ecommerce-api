package com.farmmarket.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {
    READY("배송 준비"),
    SHIPPING("배송중"),
    DELIVERED("배송 완료"),
    COMPLETED("구매 확정")
    ;

    private final String description;
}
