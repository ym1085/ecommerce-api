package com.farmmarket.repository;

import com.farmmarket.dto.res.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    /**
     * 홈 화면 상품 목록 조회
     * 대표 이미지(representativeYn=Y)를 조건부 LEFT JOIN해 Summary로 담는다
     */
    Page<ProductResponseDto.Summary> findProducts(Pageable pageable);
}
