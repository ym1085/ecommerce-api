package com.farmmarket.repository;

import com.farmmarket.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 상품 이미지 메타데이터의 저장과 기본 조회를 담당한다.
 */
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
