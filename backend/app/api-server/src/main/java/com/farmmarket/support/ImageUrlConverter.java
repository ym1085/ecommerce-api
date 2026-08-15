package com.farmmarket.support;

import com.farmmarket.config.properties.ImageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 이미지 상대 경로를 외부에서 접근 가능한 전체 URL로 변환한다
 * baseUrl은 환경별로 다르게 주입되므로 로컬·개발·운영(CDN)까지 이 클래스로 흡수한다
 */
@Component
@RequiredArgsConstructor
public class ImageUrlConverter {

    private final ImageProperties imageProperties;

    /**
     * 이미지 상대 경로를 전체 URL로 조립한다
     * 경로가 비어 있으면 null을 반환해 응답에서 이미지 없음을 표현한다
     *
     * @param storageImagePath DB에 저장되어 있는 이미지 경로
     * @return 실제 이미지 풀 경로
     */
    public String convertStoredImagePathToImageUrl(String storageImagePath) {
        if (!StringUtils.hasText(storageImagePath)) {
            return null;
        }

        String baseUrl = imageProperties.getBaseUrl().replaceAll("/+$", "");
        String normalizedPath = storageImagePath.replaceFirst("^/+", "");

        return baseUrl + "/" + normalizedPath;
    }
}