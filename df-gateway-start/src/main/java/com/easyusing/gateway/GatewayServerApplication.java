package com.easyusing.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author outsider
 * @date 2023/7/27
 */
@SpringBootApplication(scanBasePackages = {"com.easyusing.gateway"})
public class GatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }

}
