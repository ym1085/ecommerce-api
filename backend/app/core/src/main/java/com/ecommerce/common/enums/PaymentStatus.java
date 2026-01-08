package com.ecommerce.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    READY("결제대기"),
    PAID("결제완료"),
    FAILED("결제실패"),
    CANCELED("결제취소")
    ;

    private String description;
}
