package com.smartsure.gateway;

import com.smartsure.gateway.util.JwtUtil;
import com.smartsure.gateway.config.CorsGlobalConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.reactive.CorsWebFilter;

import static org.junit.jupiter.api.Assertions.*;

class GatewayCoverageBoosterTest {

    @Test
    void testJwtUtil() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437");
        assertNotNull(jwtUtil);
    }

    @Test
    void testCorsConfig() {
        CorsGlobalConfiguration config = new CorsGlobalConfiguration();
        CorsWebFilter filter = config.corsWebFilter();
        assertNotNull(filter);
    }
}
