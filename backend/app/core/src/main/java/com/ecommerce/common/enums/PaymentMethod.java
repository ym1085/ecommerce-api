package com.ecommerce.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CARD("신용/체크카드"),
    TRANSFER("계좌이체"),
    VIRTUAL_ACCOUNT("가상 계좌")
    ;

    private final String description;
}
