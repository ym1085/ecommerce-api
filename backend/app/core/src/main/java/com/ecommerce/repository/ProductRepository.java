package com.ecommerce.repository;

import com.ecommerce.domain.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 상품 ID 기반 상품 정보 조회
     * @param ids 주문 신청 시 들어오는 상품 ID
     * @return 조건에 맞는 상품 목록 반환
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE) // SELECT... FOR UPDATE: 읽기/쓰기 모두 차단
    @Query("SELECT p FROM Product p WHERE p.id IN :ids ORDER BY p.id ASC")
    List<Product> findAllByIdWithLock(@Param("ids") List<Long> ids);
}
