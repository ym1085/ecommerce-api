package com.ecommerce.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // COMMON
    // MEMBER
    // PRODUCT
    // CART

    // ORDER
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_001", "주문 정보를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
