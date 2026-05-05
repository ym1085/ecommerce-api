package com.ecommerce.service;

import com.ecommerce.dto.res.OrderResponseDto;
import com.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public Page<OrderResponseDto.Summary> findOrders(Pageable pageable, Long memberId) {
        return orderRepository.findOrders(pageable, memberId);
    }

    public OrderResponseDto.Detail findOrderByOrderId(Long orderId) {
        return orderRepository.findOrderByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다. orderId=" + orderId));
    }
}
