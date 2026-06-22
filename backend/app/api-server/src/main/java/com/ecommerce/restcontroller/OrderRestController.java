package com.ecommerce.restcontroller;

import com.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@RestController
public class OrderRestController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<?> findOrders(Pageable pageable, @RequestParam Long memberId) {
        log.info("Find orders - memberId={}, page={}, size={}", memberId, pageable.getOffset(), pageable.getPageSize());
        return ResponseEntity.ok(orderService.findOrders(pageable, memberId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> findOrderByOrderId(@PathVariable Long orderId) {
        log.info("Find order detail - orderId={}", orderId);
        return ResponseEntity.ok(orderService.findOrderByOrderId(orderId));
    }

    /*@PostMapping
    public ResponseEntity<?> saveOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid OrderRequestDto.Create request) {
        log.info("Create order - request={}", request);
        return ResponseEntity.ok(orderService.saveOrder(userDetails.getMemberId(), request));
    }*/
}
