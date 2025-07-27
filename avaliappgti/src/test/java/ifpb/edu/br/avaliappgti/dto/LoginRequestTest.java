package ifpb.edu.br.avaliappgti.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testSettersAndGetters() {
        LoginRequest dto = new LoginRequest();
        dto.setIfRegistration("IFPB-1234567");
        dto.setPassword("secret");

        assertEquals("IFPB-1234567", dto.getIfRegistration());
        assertEquals("secret", dto.getPassword());
    }

    @Test
    void testDefaultValues() {
        LoginRequest dto = new LoginRequest();
        assertNull(dto.getIfRegistration());
        assertNull(dto.getPassword());
    }
}
