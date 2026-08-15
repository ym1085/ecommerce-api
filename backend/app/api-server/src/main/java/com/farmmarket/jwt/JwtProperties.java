package com.farmmarket.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    @NotBlank
    private String secret; // JWT를 만들 때 사용하는 서명용 비밀키 (Base64 인코딩된 256bit 이상 키)

    @Positive
    private long expiration; // JWT Access Token이 얼마 동안 유효한지 나타내는 속성(ms)

    @Positive
    private long refreshExpiration; // JWT Refresh Token이 얼마 동안 유효한지 나타내는 속성(ms)
}
