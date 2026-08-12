package com.farmmarket.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 환경별 상품 이미지 저장 위치와 외부 접근 주소 설정을 관리한다
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "farm-market.image")
public class ImageProperties {

    @NotBlank
    private String storageLocation;

    @NotBlank
    private String baseUrl;
}
