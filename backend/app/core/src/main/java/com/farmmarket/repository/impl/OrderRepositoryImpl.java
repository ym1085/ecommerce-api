package com.farmmarket.repository.impl;

import com.farmmarket.domain.QMember;
import com.farmmarket.domain.QOrder;
import com.farmmarket.domain.QOrderItem;
import com.farmmarket.dto.res.OrderResponseDto;
import com.farmmarket.repository.OrderRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QOrder order = QOrder.order;
    private final QMember member = QMember.member;
    private final QOrderItem orderItem = QOrderItem.orderItem;

    /**
     * 전체 주문 목록 조회
     */
    @Override
    public Page<OrderResponseDto.Summary> findOrders(Pageable pageable, Long memberId) {
        List<OrderResponseDto.Summary> orders = queryFactory.select(
                        Projections.fields(OrderResponseDto.Summary.class,
                                order.id.as("orderId"),
                                member.name.as("memberName"),
                                order.orderNo,
                                order.totalAmount,
                                order.orderStatus,
                                order.createdAt
                        ))
                .from(order)
                .join(order.member, member)
                .where(
                        order.member.id.eq(memberId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory.select(order.count())
                .from(order)
                .where(order.member.id.eq(memberId))
                .fetchOne();

        return new PageImpl<>(orders, pageable, totalCount == null ? 0 : totalCount);
    }

    /**
     * 주문 상세 조회
     */
    @Override
    public Optional<OrderResponseDto.Detail> findOrderByOrderId(Long orderId) {
        OrderResponseDto.Detail order = queryFactory.select(
                        Projections.fields(OrderResponseDto.Detail.class,
                                this.order.id.as("orderId"),
                                member.name.as("memberName"),
                                this.order.orderNo,
                                this.order.totalAmount,
                                this.order.orderStatus,
                                this.order.createdAt
                        ))
                .from(this.order)
                .join(this.order.member, member)
                .where(
                        this.order.id.eq(orderId)
                )
                .fetchOne();

        if (order == null) {
            return Optional.empty();
        }

        List<OrderResponseDto.Item> items = queryFactory.select(
                        Projections.fields(OrderResponseDto.Item.class,
                                orderItem.id.as("orderItemId"),
                                orderItem.productName,
                                orderItem.unitPrice,
                                orderItem.quantity,
                                orderItem.createdAt
                        )
                )
                .from(orderItem)
                .where(orderItem.order.id.eq(orderId))
                .fetch();

        order.assignItems(items);
        return Optional.of(order);
    }
}
