package com.farmmarket.restcontroller;

import com.farmmarket.dto.res.OrderResponseDto;
import com.farmmarket.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@RestController
public class OrderRestController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderResponseDto.Summary>> getOrders(Pageable pageable, @RequestParam Long memberId) {
        log.info("Get orders - memberId={}, page={}, size={}", memberId, pageable.getOffset(), pageable.getPageSize());
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrders(pageable, memberId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto.Detail> getOrderById(@PathVariable Long orderId) {
        log.info("Get order detail - orderId={}", orderId);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderById(orderId));
    }

    /*@PostMapping
    public ResponseEntity<?> saveOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid OrderRequestDto.Create request) {
        log.info("Create order - request={}", request);
        return ResponseEntity.ok(orderService.createOrder(userDetails.getMemberId(), request));
    }*/
}
