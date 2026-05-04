package com.smartsure.gateway.filter;

import com.smartsure.gateway.util.JwtUtil;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        try {
            jwtUtil.validateToken(token);
            String role = jwtUtil.extractRole(token);

            // Paths that require ADMIN role
            if (path.startsWith("/api/admin/")) {
                if (!"ADMIN".equalsIgnoreCase(role)) {
                    return writeError(exchange, HttpStatus.FORBIDDEN, "Access denied: ADMIN role required");
                }
            }

            // The claim status-update endpoint is an internal operation that must be ADMIN-only.
            // Customers must use the admin-service review flow, not call claims-service directly.
            if (path.matches("/api/claims/\\d+/status") &&
                    "PUT".equalsIgnoreCase(exchange.getRequest().getMethod().name())) {
                if (!"ADMIN".equalsIgnoreCase(role)) {
                    return writeError(exchange, HttpStatus.FORBIDDEN, "Access denied: ADMIN role required");
                }
            }

            return chain.filter(exchange);
        } catch (Exception ex) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/login")
                || path.startsWith("/api/auth/register")
                || path.startsWith("/api/claims/documents/download/")
                || path.startsWith("/fallback/")          // internal circuit-breaker forward
                || path.contains("/swagger-ui")
                || path.contains("/v3/api-docs")
                || path.startsWith("/actuator/health");
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"status\":" + status.value() + ",\"message\":\"" + message + "\"}";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}