package com.farmmarket.service;

import com.farmmarket.common.enums.ErrorCode;
import com.farmmarket.common.exception.BusinessException;
import com.farmmarket.common.utils.CodeUtils;
import com.farmmarket.domain.Member;
import com.farmmarket.domain.Order;
import com.farmmarket.domain.OrderItem;
import com.farmmarket.domain.Product;
import com.farmmarket.dto.req.OrderRequestDto;
import com.farmmarket.dto.res.OrderResponseDto;
import com.farmmarket.repository.MemberRepository;
import com.farmmarket.repository.OrderRepository;
import com.farmmarket.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public Page<OrderResponseDto.Summary> findOrders(Pageable pageable, Long memberId) {
        return orderRepository.findOrders(pageable, memberId);
    }

    public OrderResponseDto.Detail findOrderByOrderId(Long orderId) {
        return orderRepository.findOrderByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    @Transactional
    public Long createOrder(Long memberId, OrderRequestDto.Create request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        List<Long> productIds = request.getItems()
                .stream()
                .map(item -> item.getProductId())
                .collect(Collectors.toList());

        // 중복 상품 요청 차단 (order_id, product_id) 유니크 제약 위반 사전 방지
        int uniqueProductCount = new HashSet<>(productIds).size();
        if (productIds.size() != uniqueProductCount) {
            throw new BusinessException(ErrorCode.DUPLICATE_ORDER_ITEM);
        }

        // 비관적 락을 통해 상품 동시 주문 제어, 쿼리 수행 시 오름차순으로 데드락 방지
        Map<Long, Product> productMap = productRepository.findAllByIdWithLock(productIds)
                .stream()
                .collect(Collectors.toMap(product -> product.getId(), product -> product));

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderRequestDto.Item item : request.getItems()) {
            Product findProduct = productMap.get(item.getProductId()); // o(1)로 상품 조회
            if (findProduct == null) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            // 주문 정보에서 상품 수량을 기반으로 재고 차감
            findProduct.decreaseStock(item.getQuantity());
            OrderItem orderItem = OrderItem.createOrderItem(findProduct, item.getQuantity());
            orderItems.add(orderItem);
        }

        // 16자리 난수 상품 번호 생성 (orderNo)
        String orderNo = CodeUtils.generate("ORD");

        // 주문 정보 생성 (Order)
        Order order = Order.createOrder(orderNo, member, orderItems);
        orderRepository.save(order);
        return order.getId();
    }
}
