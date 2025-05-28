package ifpb.edu.br.avaliappgti.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifpb.edu.br.avaliappgti.model.CommitteeMember;

@Repository
public interface CommitteeMemberRepository  extends JpaRepository<CommitteeMember, Integer> {
    Optional<CommitteeMember> findById(Integer id); // JpaRepository provides this by default
    Optional<CommitteeMember> findByEmail(String email);
    Optional<CommitteeMember> findByCpf(String cpf);
    Optional<CommitteeMember> findByIfRegistration(String ifRegistration);
}
