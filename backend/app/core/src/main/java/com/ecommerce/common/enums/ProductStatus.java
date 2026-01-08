package com.ecommerce.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {
    ON_SALE("판매중"),
    SOLD_OUT("품절"),
    HIDDEN("숨김")
    ;

    private String description;
}
