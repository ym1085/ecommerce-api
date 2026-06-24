package com.ecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true) // 통계 = 읽기 전용 - 단일 DataSource라 JPA와 트랜잭션 공유
@Service
public class OrderStatService {

    /* TODO: 통계 or 집계 쿼리 추가 */
}
