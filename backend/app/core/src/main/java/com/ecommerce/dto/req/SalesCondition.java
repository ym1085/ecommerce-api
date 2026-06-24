package com.ecommerce.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesCondition {
    private LocalDate startDate; // 조회 시작일 (포함)
    private LocalDate endDate;   // 조회 종료일 (포함)
}
