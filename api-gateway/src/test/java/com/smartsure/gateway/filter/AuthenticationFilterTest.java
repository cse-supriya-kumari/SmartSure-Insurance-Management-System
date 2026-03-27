package com.smartsure.gateway.filter;

import com.smartsure.gateway.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class AuthenticationFilterTest {

    private JwtUtil jwtUtil;
    private AuthenticationFilter authenticationFilter;
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        authenticationFilter = new AuthenticationFilter(jwtUtil);
        chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void shouldAllowPublicLoginPath() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/auth/login").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(authenticationFilter.filter(exchange, chain))
                .verifyComplete();

        verify(chain, times(1)).filter(exchange);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void shouldRejectWhenAuthorizationHeaderMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/policies/user/1").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(authenticationFilter.filter(exchange, chain))
                .verifyComplete();

        assert exchange.getResponse().getStatusCode() != null;
        assert exchange.getResponse().getStatusCode().value() == 401;
    }

    @Test
    void shouldAllowValidNonAdminRequest() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/policies/user/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        doNothing().when(jwtUtil).validateToken("valid-token");

        StepVerifier.create(authenticationFilter.filter(exchange, chain))
                .verifyComplete();

        verify(jwtUtil, times(1)).validateToken("valid-token");
        verify(chain, times(1)).filter(exchange);
    }

    @Test
    void shouldRejectAdminRouteForNonAdminRole() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/admin/reports")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        doNothing().when(jwtUtil).validateToken("valid-token");
        when(jwtUtil.extractRole("valid-token")).thenReturn("CUSTOMER");

        StepVerifier.create(authenticationFilter.filter(exchange, chain))
                .verifyComplete();

        assert exchange.getResponse().getStatusCode() != null;
        assert exchange.getResponse().getStatusCode().value() == 403;
        verify(chain, never()).filter(exchange);
    }

    @Test
    void shouldAllowAdminRouteForAdminRole() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/admin/reports")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        doNothing().when(jwtUtil).validateToken("valid-token");
        when(jwtUtil.extractRole("valid-token")).thenReturn("ADMIN");

        StepVerifier.create(authenticationFilter.filter(exchange, chain))
                .verifyComplete();

        verify(chain, times(1)).filter(exchange);
    }
}