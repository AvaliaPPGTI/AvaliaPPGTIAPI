package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.model.CommitteeMember;
import ifpb.edu.br.avaliappgti.repository.CommitteeMemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CommitteeMemberRepository committeeMemberRepository;

    public CustomUserDetailsService(CommitteeMemberRepository committeeMemberRepository) {
        this.committeeMemberRepository = committeeMemberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String ifRegistration) throws UsernameNotFoundException {
        CommitteeMember member = committeeMemberRepository.findByIfRegistration(ifRegistration)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with registration: " + ifRegistration));

        // WARNING: Storing plain text passwords. This is a major security risk.
        // You should migrate to hashed passwords.
        return new User(
                member.getIfRegistration(),
                member.getPassword(), // Spring will compare this with the provided password
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_COMMITTEE"))
        );
    }
}
