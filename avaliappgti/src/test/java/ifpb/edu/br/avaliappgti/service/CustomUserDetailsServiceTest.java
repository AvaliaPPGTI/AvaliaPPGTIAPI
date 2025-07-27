package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.model.CommitteeMember;
import ifpb.edu.br.avaliappgti.repository.CommitteeMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private CommitteeMemberRepository committeeMemberRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customUserDetailsService = new CustomUserDetailsService(committeeMemberRepository);
    }

    @Test
    void testLoadUserByUsername_userFound() {
        CommitteeMember member = new CommitteeMember();
        member.setIfRegistration("IFPB-1234567");
        member.setPassword("plainpassword");

        when(committeeMemberRepository.findByIfRegistration("IFPB-1234567"))
                .thenReturn(Optional.of(member));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("IFPB-1234567");

        assertEquals("IFPB-1234567", userDetails.getUsername());
        assertEquals("plainpassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COMMITTEE")));
    }

    @Test
    void testLoadUserByUsername_userNotFound() {
        when(committeeMemberRepository.findByIfRegistration("NOT_FOUND"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("NOT_FOUND"));
    }
}
