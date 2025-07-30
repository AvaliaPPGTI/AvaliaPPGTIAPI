package ifpb.edu.br.avaliappgti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifpb.edu.br.avaliappgti.config.JwtAuthFilter;
import ifpb.edu.br.avaliappgti.dto.LoginRequest;
import ifpb.edu.br.avaliappgti.dto.LoginResponse;
import ifpb.edu.br.avaliappgti.service.AuthService;
import ifpb.edu.br.avaliappgti.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLogin_success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIfRegistration("IFPB-1234567");
        loginRequest.setPassword("61ce46c310554ee912b5aa06da9a0cea8e08cdd4db96006e0cdcab258618bf42");

        when(authService.login(any(LoginRequest.class))).thenReturn("jwt-token-123");

        mockMvc.perform(post("/api/auth/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"));
    }

    @Test
    void testLogin_failure() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIfRegistration("IFPB-1234567");
        loginRequest.setPassword("wrong");

        when(authService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials."));
    }
}
