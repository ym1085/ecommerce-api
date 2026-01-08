package com.ecommerce.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 결제 수단은 우선 2가지 종류로만 제한
 */
@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CARD("카드결제"),
    TRANSFER("계좌이체"),
    ;

    private String description;
}
