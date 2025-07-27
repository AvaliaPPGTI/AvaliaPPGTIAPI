package ifpb.edu.br.avaliappgti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifpb.edu.br.avaliappgti.dto.LoginRequest;
import ifpb.edu.br.avaliappgti.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@WithMockUser
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLogin_success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIfRegistration("IFPB-1234567");
        loginRequest.setPassword("password");

        when(authService.login(any(LoginRequest.class))).thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful."));
    }

    @Test
    void testLogin_failure() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIfRegistration("IFPB-1234567");
        loginRequest.setPassword("wrong");

        when(authService.login(any(LoginRequest.class))).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials."));
    }

    @Test
    void testLogin_exception() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIfRegistration("IFPB-1234567");
        loginRequest.setPassword("wrong");

        when(authService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials."));
    }

    @Test
    void testLogout_withSession() throws Exception {
        HttpSession session = mock(HttpSession.class);

        mockMvc.perform(post("/api/auth/logout")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .sessionAttr("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext()))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful."));
    }

    @Test
    void testLogout_withoutSession() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful."));
    }
}
