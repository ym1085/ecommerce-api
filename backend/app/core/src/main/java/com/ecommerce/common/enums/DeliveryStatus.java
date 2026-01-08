package com.ecommerce.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryStatus {
    READY("배송 준비중"),
    SHIPPING("배송중"),
    DELIVERED("배송 완료")
    ;

    private String description;
}