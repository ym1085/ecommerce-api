package com.farmmarket.restcontroller;

import com.farmmarket.common.enums.ErrorCode;
import com.farmmarket.common.enums.ProductStatus;
import com.farmmarket.common.exception.BusinessException;
import com.farmmarket.config.SecurityConfig;
import com.farmmarket.dto.res.ProductResponseDto;
import com.farmmarket.jwt.JwtProvider;
import com.farmmarket.repository.AccessTokenBlacklistRepository;
import com.farmmarket.service.ProductService;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Controller] 상품 REST Controller 테스트")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductRestController.class)
@Import(SecurityConfig.class)
class ProductRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ProductService productService;

    @MockitoBean
    JwtProvider jwtProvider;

    @MockitoBean
    AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    @Nested
    @DisplayName("GET /api/v1/products - 상품 목록 조회")
    @Order(1)
    class GetProducts {

        @Test
        @DisplayName("상품 목록 조회에 성공하면 200과 페이징된 상품 정보를 반환한다")
        @Order(1)
        void shouldReturn200AndProducts_whenGetProductsSuccess() throws Exception {
            // given
            Pageable pageable = PageRequest.of(0, 9);

            ProductResponseDto.Summary response = ProductResponseDto.Summary.builder()
                    .productId(1L)
                    .productName("햇 감자 3kg")
                    .price(9_900L)
                    .productStatus(ProductStatus.ON_SALE)
                    .representativeImageUrl("http://localhost:8080/images/products/1/main.webp")
                    .build();

            // 요청한 페이지 조건에 해당하는 상품 목록 반환
            given(productService.getProducts(any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(response), pageable, 1));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/products")
                    .param("page", "0")
                    .param("size", "9")
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].productId").value(1L))
                    .andExpect(jsonPath("$.content[0].productName").value("햇 감자 3kg"))
                    .andExpect(jsonPath("$.content[0].price").value(9_900L))
                    .andExpect(jsonPath("$.content[0].productStatus").value("ON_SALE"))
                    .andExpect(jsonPath("$.content[0].representativeImageUrl")
                            .value("http://localhost:8080/images/products/1/main.webp"))
                    .andExpect(jsonPath("$.number").value(0))
                    .andExpect(jsonPath("$.size").value(9))
                    .andExpect(jsonPath("$.totalElements").value(1));

            then(productService).should().getProducts(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/products/{productId} - 상품 상세 조회")
    @Order(2)
    class GetProductById {

        @Test
        @DisplayName("상품 상세 조회에 성공하면 200과 상품 정보를 반환한다")
        @Order(1)
        void shouldReturn200AndProductDetail_whenProductExists() throws Exception {
            // given
            ProductResponseDto.Detail response = ProductResponseDto.Detail.builder()
                    .productId(1L)
                    .productName("햇 감자 3kg")
                    .description("주말농장에서 직접 수확한 햇감자")
                    .price(9_900L)
                    .productStatus(ProductStatus.ON_SALE)
                    .stockQuantity(30)
                    .imageUrls(List.of(
                            "http://localhost:8080/images/products/1/detail-1.webp",
                            "http://localhost:8080/images/products/1/detail-2.webp"
                    ))
                    .build();

            // 상품 ID에 해당하는 상세 정보 반환
            given(productService.getProductById(1L)).willReturn(response);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/products/{productId}", 1L)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.productId").value(1L))
                    .andExpect(jsonPath("$.productName").value("햇 감자 3kg"))
                    .andExpect(jsonPath("$.description").value("주말농장에서 직접 수확한 햇감자"))
                    .andExpect(jsonPath("$.price").value(9_900L))
                    .andExpect(jsonPath("$.productStatus").value("ON_SALE"))
                    .andExpect(jsonPath("$.stockQuantity").value(30))
                    .andExpect(jsonPath("$.imageUrls[0]")
                            .value("http://localhost:8080/images/products/1/detail-1.webp"))
                    .andExpect(jsonPath("$.imageUrls[1]")
                            .value("http://localhost:8080/images/products/1/detail-2.webp"));

            then(productService).should().getProductById(1L);
        }

        @Test
        @DisplayName("상품이 존재하지 않으면 400과 상품 없음 오류를 반환한다")
        @Order(2)
        void shouldReturn400_whenProductNotFound() throws Exception {
            // given
            // 상품 ID에 해당하는 정보가 없어 Service에서 상품 없음 예외 발생
            given(productService.getProductById(999L))
                    .willThrow(new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/products/{productId}", 999L)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.code").value("PRODUCT-001"))
                    .andExpect(jsonPath("$.message").value("상품 정보를 찾을 수 없습니다."));

            then(productService).should().getProductById(999L);
        }
    }
}
