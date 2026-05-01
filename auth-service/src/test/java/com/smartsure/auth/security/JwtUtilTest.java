package com.smartsure.auth.security;

import com.smartsure.auth.security.UserDetailsImpl;
import com.smartsure.auth.entity.User;
import com.smartsure.auth.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;
    private final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);

        User user = User.builder()
                .email("test@example.com")
                .role(Role.CUSTOMER)
                .build();
        userDetails = new UserDetailsImpl(user);
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtUtil.generateToken(userDetails, "CUSTOMER");
        assertNotNull(token);
        
        String username = jwtUtil.extractUsername(token);
        assertEquals("test@example.com", username);
        
        String role = jwtUtil.extractRole(token);
        assertEquals("CUSTOMER", role);
        
        assertTrue(jwtUtil.isTokenValid(token, userDetails));
        assertDoesNotThrow(() -> jwtUtil.validateToken(token));
    }

    @Test
    void testTokenValidationFailure() {
        String invalidToken = "invalid.token.here";
        assertThrows(Exception.class, () -> jwtUtil.validateToken(invalidToken));
    }
}
