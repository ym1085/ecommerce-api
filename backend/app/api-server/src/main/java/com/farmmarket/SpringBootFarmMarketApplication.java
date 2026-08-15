package com.farmmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * @SpringBootApplication
 * SpringBoot 애플리케이션의 시작점임을 선언하는 메타 어노테이션
 * 실제로는 @Configuration, @EnableAutoConfiguration, @ComponentScan 포함
 *
 * 위 어노테이션 하나로 다음이 수행됨
 * - 이 클래스를 Spring 설정 클래스로 명시 (@Configuration)
 * - 필요한 설정과 Bean을 자동으로 구성한다 (@EnableAutoConfiguration)
 * - 현재 패키지 기준으로 컴포넌트 스캔 수행 (@ComponentScan)
 *
 * @ConfigurationPropertiesScan
 * 현재 패키지 하위의 @ConfigurationProperties 클래스(JwtProperties 등)를 찾아 빈으로 등록한다
 * 설정 클래스마다 @EnableConfigurationProperties를 붙이지 않아도 되며, 프로퍼티 클래스가 늘어나도 수정할 필요가 없다
 * 슬라이스 테스트(@WebMvcTest 등)에서는 이 스캔이 동작하지 않으므로, 테스트 컨텍스트에 프로퍼티가 딸려 들어오지 않는다
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringBootFarmMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootFarmMarketApplication.class);
    }
}
