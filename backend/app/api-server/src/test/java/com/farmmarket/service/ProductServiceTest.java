package com.farmmarket.service;

import com.farmmarket.common.enums.ErrorCode;
import com.farmmarket.common.enums.ProductStatus;
import com.farmmarket.common.exception.BusinessException;
import com.farmmarket.domain.Product;
import com.farmmarket.domain.ProductImage;
import com.farmmarket.dto.res.ProductResponseDto;
import com.farmmarket.repository.ProductImageRepository;
import com.farmmarket.repository.ProductRepository;
import com.farmmarket.support.ImageUrlConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Service] 상품 Service 테스트")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductImageRepository productImageRepository;

    @Mock
    ImageUrlConverter imageUrlConverter;

    @InjectMocks
    ProductService productService;

    @Nested
    @DisplayName("getProducts - 상품 목록 조회")
    @Order(1)
    class GetProducts {

        @Test
        @DisplayName("상품이 존재하면 대표 이미지 URL을 변환해 페이지로 반환한다")
        @Order(1)
        void shouldReturnProductsWithConvertedImageUrl_whenProductsExist() {
            // given
            Pageable pageable = PageRequest.of(0, 9);

            ProductResponseDto.Summary storedProduct = ProductResponseDto.Summary.builder()
                    .productId(1L)
                    .productName("햇 감자 3kg")
                    .price(9_900L)
                    .productStatus(ProductStatus.ON_SALE)
                    .representativeImageUrl("/products/1/main.webp")
                    .build();

            // 상품 목록과 대표 이미지의 외부 URL 변환 결과를 준비
            given(productRepository.findProducts(pageable))
                    .willReturn(new PageImpl<>(List.of(storedProduct), pageable, 1));

            given(imageUrlConverter.convertStoredImagePathToImageUrl("/products/1/main.webp"))
                    .willReturn("http://localhost:8080/images/products/1/main.webp");

            // when
            Page<ProductResponseDto.Summary> result = productService.getProducts(pageable);

            // then
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).singleElement().satisfies(product -> {
                assertThat(product.getProductId()).isEqualTo(1L);
                assertThat(product.getProductName()).isEqualTo("햇 감자 3kg");
                assertThat(product.getPrice()).isEqualTo(9_900L);
                assertThat(product.getProductStatus()).isEqualTo(ProductStatus.ON_SALE);
                assertThat(product.getRepresentativeImageUrl())
                        .isEqualTo("http://localhost:8080/images/products/1/main.webp");
            });

            then(productRepository).should().findProducts(pageable);
            then(imageUrlConverter).should().convertStoredImagePathToImageUrl("/products/1/main.webp");
        }
    }

    @Nested
    @DisplayName("getProductById - 상품 상세 조회")
    @Order(2)
    class GetProductById {

        @Test
        @DisplayName("상품이 존재하면 노출 순서대로 이미지 URL을 변환해 상세 정보를 반환한다")
        @Order(1)
        void shouldReturnProductDetailWithOrderedImageUrls_whenProductExists() {
            // given
            Product product = Product.createProduct("햇 감자 3kg", "주말농장에서 직접 수확한 햇감자", 9_900L, 30);
            ReflectionTestUtils.setField(product, "id", 1L);

            ProductImage firstImage = ProductImage.create(
                    "/products/1/detail-1.webp", "detail-1.webp", "image/webp", 100L, "Y", 1, product);
            ProductImage secondImage = ProductImage.create(
                    "/products/1/detail-2.webp", "detail-2.webp", "image/webp", 100L, "N", 2, product);

            // 상품과 displayOrder 오름차순 이미지 목록을 조회
            given(productRepository.findById(1L)).willReturn(Optional.of(product));
            given(productImageRepository.findByProductIdOrderByDisplayOrderAsc(1L))
                    .willReturn(List.of(firstImage, secondImage));

            // 저장 경로를 외부에서 접근 가능한 이미지 URL로 변환
            given(imageUrlConverter.convertStoredImagePathToImageUrl("/products/1/detail-1.webp"))
                    .willReturn("http://localhost:8080/images/products/1/detail-1.webp");

            given(imageUrlConverter.convertStoredImagePathToImageUrl("/products/1/detail-2.webp"))
                    .willReturn("http://localhost:8080/images/products/1/detail-2.webp");

            // when
            ProductResponseDto.Detail result = productService.getProductById(1L);

            // then
            assertThat(result.getProductId()).isEqualTo(1L);
            assertThat(result.getProductName()).isEqualTo("햇 감자 3kg");
            assertThat(result.getDescription()).isEqualTo("주말농장에서 직접 수확한 햇감자");
            assertThat(result.getPrice()).isEqualTo(9_900L);
            assertThat(result.getProductStatus()).isEqualTo(ProductStatus.ON_SALE);
            assertThat(result.getStockQuantity()).isEqualTo(30);
            assertThat(result.getImageUrls()).containsExactly(
                    "http://localhost:8080/images/products/1/detail-1.webp",
                    "http://localhost:8080/images/products/1/detail-2.webp"
            );

            then(productRepository).should().findById(1L);
            then(productImageRepository).should().findByProductIdOrderByDisplayOrderAsc(1L);
        }

        @Test
        @DisplayName("상품이 존재하지 않으면 상품 없음 예외를 던지고 이미지를 조회하지 않는다")
        @Order(2)
        void shouldThrow_whenProductNotFound() {
            // given
            // 상품이 존재하지 않아 상세 이미지 조회로 진행할 수 없는 상태
            given(productRepository.findById(999L)).willReturn(Optional.empty());

            // when
            // then
            assertThatThrownBy(() -> productService.getProductById(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PRODUCT_NOT_FOUND);

            then(productImageRepository).shouldHaveNoInteractions();
            then(imageUrlConverter).shouldHaveNoInteractions();
        }
    }
}
