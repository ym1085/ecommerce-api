package com.ecommerce.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    CREATED("생성됨"),
    PAID("결제완료"),
    CANCELED("취소")
    ;

    private String description;
}