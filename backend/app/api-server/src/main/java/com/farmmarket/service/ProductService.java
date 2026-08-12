package com.farmmarket.service;

import com.farmmarket.common.enums.ErrorCode;
import com.farmmarket.common.exception.BusinessException;
import com.farmmarket.domain.Product;
import com.farmmarket.domain.ProductImage;
import com.farmmarket.dto.res.ProductResponseDto;
import com.farmmarket.repository.ProductImageRepository;
import com.farmmarket.repository.ProductRepository;
import com.farmmarket.support.ImageUrlConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ImageUrlConverter imageUrlConverter;

    /**
     * 홈 화면 상품 목록을 페이징 조회한다
     * 대표 이미지는 Repository에서 조건부 조인으로 함께 담아 반환한다
     */
    public Page<ProductResponseDto.Summary> getProducts(Pageable pageable) {
        Page<ProductResponseDto.Summary> products = productRepository.findProducts(pageable);
        return products.map(product -> ProductResponseDto.Summary.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .productStatus(product.getProductStatus())
                .representativeImageUrl(imageUrlConverter.convertStoredImagePathToImageUrl(product.getRepresentativeImageUrl()))
                .build()
        );
    }

    /**
     * 상품 ID로 상세 정보를 조회한다
     * 이미지 목록은 display_order 오름차순으로 함께 담아 반환한다
     */
    public ProductResponseDto.Detail getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        List<String> imageUrls = productImageRepository
                .findByProductIdOrderByDisplayOrderAsc(productId)
                .stream()
                .map(ProductImage::getImageUrl)
                .map(imageUrlConverter::convertStoredImagePathToImageUrl)
                .filter(Objects::nonNull)
                .toList();

        return ProductResponseDto.Detail.builder()
                .productId(product.getId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .productStatus(product.getProductStatus())
                .stockQuantity(product.getStockQuantity())
                .imageUrls(imageUrls)
                .build();
    }
}