package com.farmmarket.config;

import com.farmmarket.config.properties.ImageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 로컬과 개발 환경에서 상품 이미지 요청을 외부 파일 저장소에 연결한다
 */
@Profile({"local", "dev"})
@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final ImageProperties imageProperties;

    /**
     * 이미지 요청 주소를 실제 파일 저장 폴더에 연결한다.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(imageProperties.getStorageLocation());
    }
}
