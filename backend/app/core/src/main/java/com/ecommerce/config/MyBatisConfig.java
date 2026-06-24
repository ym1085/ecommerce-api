package com.ecommerce.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.ecommerce.mapper") // 해당 패키지의 인터페이스를 Mapper Bean으로 등록
public class MyBatisConfig {
}

