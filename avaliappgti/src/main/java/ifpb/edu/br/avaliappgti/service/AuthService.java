package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public boolean login(LoginRequest loginRequest) {
        // Create an authentication token with the user's credentials
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getIfRegistration(),
                loginRequest.getPassword()
        );

        // Let Spring Security's AuthenticationManager handle the password check
        Authentication authentication = authenticationManager.authenticate(authToken);

        // If authentication is successful, set the user in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication.isAuthenticated();
    }
}