package ifpb.edu.br.avaliappgti.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    @Test
    void testAllArgsConstructorAndGetter() {
        LoginResponse response = new LoginResponse("jwt-token-123");
        assertEquals("jwt-token-123", response.getToken());
    }

    @Test
    void testSetter() {
        LoginResponse response = new LoginResponse("initial-token");
        response.setToken("new-token");
        assertEquals("new-token", response.getToken());
    }
}
