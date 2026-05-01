package com.smartsure.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Fallback controller for circuit-breaker responses.
 *
 * Uses @RequestMapping (no specific method) on each handler so that ALL
 * HTTP methods (GET, POST, PUT, DELETE, PATCH…) are handled.  The gateway
 * forwards the original request method to the fallback URI, so a GET-only
 * mapping would return 405 for any non-GET request that trips the breaker.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/auth")
    public Mono<ResponseEntity<Map<String, String>>> authFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", "Authentication service is currently unavailable. Please try again later.")));
    }

    @RequestMapping("/policy")
    public Mono<ResponseEntity<Map<String, String>>> policyFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", "Policy service is currently unavailable. Please try again later.")));
    }

    @RequestMapping("/claims")
    public Mono<ResponseEntity<Map<String, String>>> claimsFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", "Claims service is currently unavailable. Please try again later.")));
    }

    @RequestMapping("/admin")
    public Mono<ResponseEntity<Map<String, String>>> adminFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", "Admin service is currently unavailable. Please try again later.")));
    }
}
