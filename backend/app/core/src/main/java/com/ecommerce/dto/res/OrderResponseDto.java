package com.ecommerce.dto.res;

import com.ecommerce.common.enums.OrderStatus;
import com.ecommerce.domain.Order;
import com.ecommerce.domain.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Summary {
        private Long orderId;
        private String memberName;
        private String orderNo;
        private Long totalAmount;
        private OrderStatus orderStatus;
        private LocalDateTime createdAt;

        // QueryDSL Projection을 사용하지 않을 때 엔티티 -> DTO 변환용
        public static Summary from(Order order) {
            return Summary.builder()
                    .orderId(order.getId())
                    .memberName(order.getMember().getName())
                    .orderNo(order.getOrderNo())
                    .totalAmount(order.getTotalAmount())
                    .orderStatus(order.getOrderStatus())
                    .createdAt(order.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail {
        private Long orderId;
        private String memberName;
        private String orderNo;
        private Long totalAmount;
        private OrderStatus orderStatus;
        private LocalDateTime createdAt;
        private List<Item> items;

        // QueryDSL Projection을 사용하지 않을 때 엔티티 -> DTO 변환용
        public static Detail from(Order order) {
            return Detail.builder()
                    .orderId(order.getId())
                    .memberName(order.getMember().getName())
                    .orderNo(order.getOrderNo())
                    .totalAmount(order.getTotalAmount())
                    .orderStatus(order.getOrderStatus())
                    .createdAt(order.getCreatedAt())
                    .build();
        }

        // OrderItem 상품 N개를 상품 정보에 추가
        public void assignItems(List<Item> items) {
            this.items = items;
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private Long orderItemId;
        private String productName;
        private Long unitPrice;
        private Integer quantity;
        private LocalDateTime createdAt;

        // QueryDSL Projection을 사용하지 않을 때 엔티티 -> DTO 변환용
        public static Item from(OrderItem orderItem) {
            return Item.builder()
                    .orderItemId(orderItem.getId())
                    .productName(orderItem.getProductName())
                    .unitPrice(orderItem.getUnitPrice())
                    .quantity(orderItem.getQuantity())
                    .createdAt(orderItem.getCreatedAt())
                    .build();
        }
    }
}
