package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.LoginRequest;
import ifpb.edu.br.avaliappgti.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    public String login(LoginRequest loginRequest) {
        // Use AuthenticationManager to validate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getIfRegistration(), loginRequest.getPassword())
        );

        // If successful, load UserDetails and generate a token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getIfRegistration());
        return jwtUtil.generateToken(userDetails);
    }
}