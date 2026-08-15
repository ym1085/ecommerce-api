package com.farmmarket.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {
    ON_SALE("판매중"),
    UPCOMING("수확 예정"),
    OUT_OF_STOCK("품절"),
    DISCONTINUED("판매종료"),
    DELETED("삭제")
    ;

    private final String description;
}
