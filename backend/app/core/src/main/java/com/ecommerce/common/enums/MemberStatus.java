package com.ecommerce.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {
    ACTIVE("활성"), // 정상 회원 -> 모든 서비스 이용 가능
    INACTIVE("비활성"), // 휴먼 계정 -> N일 지난 계정
    DELETED("삭제"), // 사용자 탈퇴 or 관리자 삭제 처리
    BLOCKED("차단") // 문제 있는 유저 -> 서비스 접근만 막음
    ;

    private final String description;
}
