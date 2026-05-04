package com.smartsure.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "jwt.secret=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437",
    "spring.cloud.config.enabled=false",
    "eureka.client.enabled=false"
})
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
    }
}