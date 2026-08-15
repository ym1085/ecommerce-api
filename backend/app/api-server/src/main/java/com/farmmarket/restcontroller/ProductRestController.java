package com.farmmarket.restcontroller;

import com.farmmarket.dto.res.ProductResponseDto;
import com.farmmarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@RestController
public class ProductRestController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto.Summary>> getProducts(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProducts(pageable));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto.Detail> getProductById(@PathVariable Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductById(productId));
    }
}