package com.smartsure.policy.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdempotencyInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(IdempotencyInterceptor.class);
    private final ConcurrentHashMap<String, Long> processedKeys = new ConcurrentHashMap<>();
    private static final long EXPIRATION_TIME_MS = 60000; // 1 minute expiration

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String idempotencyKey = request.getHeader("Idempotency-Key");
            if (idempotencyKey != null && !idempotencyKey.trim().isEmpty()) {
                cleanUpExpiredKeys();
                Long expirationTime = System.currentTimeMillis() + EXPIRATION_TIME_MS;
                Long previousEntry = processedKeys.putIfAbsent(idempotencyKey, expirationTime);
                
                if (previousEntry != null && previousEntry > System.currentTimeMillis()) {
                    logger.warn("Duplicate request detected for idempotency key: {}", idempotencyKey);
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().write("Duplicate request detected for idempotency key");
                    return false;
                } else if (previousEntry != null) {
                    // Overwrite expired key
                    processedKeys.put(idempotencyKey, expirationTime);
                }
            }
        }
        return true;
    }

    private void cleanUpExpiredKeys() {
        long currentTime = System.currentTimeMillis();
        processedKeys.entrySet().removeIf(entry -> entry.getValue() < currentTime);
    }
}
