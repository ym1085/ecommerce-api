package com.ecommerce.dto.res;

import com.ecommerce.common.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Summary {
        private Long productId;
        private String productName;
        private Long price;
        private ProductStatus productStatus;
        private String representativeImageUrl;
    }
}
