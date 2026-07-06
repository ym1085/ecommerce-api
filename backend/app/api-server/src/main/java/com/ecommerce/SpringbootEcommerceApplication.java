package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @SpringBootApplication
 * SpringBoot 애플리케이션의 시작점임을 선언하는 메타 어노테이션
 * 실제로는 @Configuration, @EnableAutoConfiguration, @ComponentScan 포함
 *
 * 위 어노테이션 하나로 다음이 수행됨
 * - 이 클래스를 Spring 설정 클래스로 명시 (@Configuration)
 * - 필요한 설정과 Bean을 자동으로 구성한다 (@EnableAutoConfiguration)
 * - 현재 패키지 기준으로 컴포넌트 스캔 수행 (@ComponentScan)
 */
@SpringBootApplication
public class SpringbootEcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootEcommerceApplication.class);
    }
}
