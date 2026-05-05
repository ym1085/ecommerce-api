package com.ecommerce.repository;

import com.ecommerce.dto.res.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepositoryCustom {

    Page<OrderResponseDto.Summary> findOrders(Pageable pageable, Long memberId);

    Optional<OrderResponseDto.Detail> findOrderByOrderId(Long orderId);
}
