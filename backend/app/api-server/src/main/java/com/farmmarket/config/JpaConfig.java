package com.farmmarket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // JPA Auditing(생성일/수정일 자동 기입) 활성화
public class JpaConfig {
}
