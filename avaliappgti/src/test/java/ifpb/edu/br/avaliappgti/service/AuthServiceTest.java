package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(authenticationManager);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testLogin_successfulAuthentication() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIfRegistration("IFPB-1234567");
        loginRequest.setPassword("password");

        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(mockAuth.isAuthenticated()).thenReturn(true);

        boolean result = authService.login(loginRequest);

        assertTrue(result);
        assertEquals(mockAuth, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testLogin_failedAuthentication() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIfRegistration("IFPB-1234567");
        loginRequest.setPassword("wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
