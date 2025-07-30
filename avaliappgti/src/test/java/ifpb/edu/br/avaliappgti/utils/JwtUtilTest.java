package ifpb.edu.br.avaliappgti.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secret;

    @BeforeEach
    void setUp() {
        // Use a base64-encoded 256-bit key for HS256
        byte[] keyBytes = new byte[32];
        for (int i = 0; i < keyBytes.length; i++) keyBytes[i] = (byte) i;
        secret = Base64.getEncoder().encodeToString(keyBytes);
        jwtUtil = new JwtUtil(secret);
    }

    @Test
    void testGenerateAndValidateToken() {
        UserDetails user = new User("user1", "pass", List.of(new SimpleGrantedAuthority("ROLE_COMMITTEE")));
        String token = jwtUtil.generateToken(user);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token, user));
        assertEquals("user1", jwtUtil.extractUsername(token));
    }

    @Test
    void testExtractAuthorities() {
        UserDetails user = new User("user2", "pass", List.of(
                new SimpleGrantedAuthority("ROLE_COMMITTEE"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        ));
        String token = jwtUtil.generateToken(user);

        List<SimpleGrantedAuthority> authorities = jwtUtil.extractAuthorities(token).stream()
                .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
                .toList();

        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_COMMITTEE")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testExtractAllClaims() {
        UserDetails user = new User("user3", "pass", List.of(new SimpleGrantedAuthority("ROLE_COMMITTEE")));
        String token = jwtUtil.generateToken(user);

        Claims claims = jwtUtil.extractAllClaims(token);
        assertEquals("user3", claims.getSubject());
        assertNotNull(claims.get("roles"));
    }

    @Test
    void testIsTokenExpired() throws InterruptedException {
        UserDetails user = new User("user4", "pass", List.of(new SimpleGrantedAuthority("ROLE_COMMITTEE")));
        String token = jwtUtil.generateToken(user);

        // Should not be expired immediately
        assertFalse(jwtUtil.extractAllClaims(token).getExpiration().before(new java.util.Date()));
    }
}
