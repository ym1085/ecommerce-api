package com.ecommerce.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통 (COMMON)
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON-001", "잘못된 입력값입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-999", "서버 오류가 발생했습니다."),

    // 회원 (MEMBER)
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER-001", "회원 정보를 찾을 수 없습니다."),
    MEMBER_DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER-002", "이미 사용 중인 이메일입니다."),
    MEMBER_DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "MEMBER-003", "이미 사용 중인 전화번호입니다."),

    // 상품 (PRODUCT)
    PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST, "PRODUCT-001", "상품 정보를 찾을 수 없습니다."),
    PRODUCT_OUT_OF_STOCK(HttpStatus.CONFLICT, "PRODUCT-002", "상품 재고가 부족합니다."),
    PRODUCT_NOT_ORDERABLE(HttpStatus.CONFLICT, "PRODUCT-003", "주문할 수 없는 상태의 상품입니다."),

    // 주문 (ORDER)
    ORDER_NOT_FOUND(HttpStatus.BAD_REQUEST, "ORDER-001", "주문 정보를 찾을 수 없습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
