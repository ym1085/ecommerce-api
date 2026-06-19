package com.ecommerce.common.utils;

import java.util.UUID;

public final class CodeUtils {

    private CodeUtils() {}

    /**
     * 16자리 랜덤 문자열 생성
     * @param prefix 'ORD' 와 같은 prefix 문자열을 넣어서 16자리 문자열 완성
     * @return 16자리 랜덤 문자열 반환
     */
    public static String generate(String prefix) {
        return prefix + "-" + UUID.randomUUID()
                                  .toString()
                                  .replace("-", "")
                                  .substring(0, 16)
                                  .toUpperCase();
    }
}
