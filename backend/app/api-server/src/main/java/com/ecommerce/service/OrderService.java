package com.ecommerce.service;

import com.ecommerce.common.utils.CodeUtils;
import com.ecommerce.domain.Member;
import com.ecommerce.domain.Order;
import com.ecommerce.domain.OrderItem;
import com.ecommerce.domain.Product;
import com.ecommerce.dto.req.OrderRequestDto;
import com.ecommerce.dto.res.OrderResponseDto;
import com.ecommerce.repository.MemberRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
                .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다. orderId=" + orderId));
    }

    @Transactional
    public Long createOrder(Long memberId, OrderRequestDto.Create request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다. memberId=" + memberId));

        // 상품 ID 목록 추출
        List<Long> productIds = request.getItems()
                .stream()
                .map(item -> item.getProductId())
                .collect(Collectors.toList());

        // 상품 ID 기반 상품 정보 조회
        // 비관적 락을 통해 상품 동시 주문 제어
        Map<Long, Product> productMap = productRepository.findAllByIdWithLock(productIds)
                .stream()
                .collect(Collectors.toMap(product -> product.getId(), product -> product));

        long totalAmount = 0L;
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderRequestDto.Item item : request.getItems()) {
            Product findProduct = productMap.get(item.getProductId()); // o(1)로 상품 조회
            if (findProduct == null) {
                throw new IllegalArgumentException("상품 정보를 찾을 수 없습니다. productId=" + item.getProductId());
            }
            // 주문 정보에서 상품 수량을 기반으로 재고 차감
            findProduct.decreaseStock(item.getQuantity());

            // 주문 상품 생성
            OrderItem orderItem = OrderItem.create(findProduct, item.getQuantity());
            totalAmount += orderItem.calculateTotalAmount();
            orderItems.add(orderItem);
        }

        // 16자리 난수 상품 번호 생성 (orderNo)
        String orderNo = CodeUtils.generate("ORD");

        // 주문 정보 생성 (Order)
        Order order = Order.createOrder(orderNo, totalAmount, member);
        orderItems.forEach(orderItem -> order.addOrderItem(orderItem)); // 양방향 연관관계 셋팅

        orderRepository.save(order);
        return order.getId();
    }
}
