package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifpb.edu.br.avaliappgti.model.CommitteeMember;

@Repository
public interface CommitteeMemberRepository  extends JpaRepository<CommitteeMember, Integer> {
    Optional<CommitteeMember> findByEmail(String email);
    Optional<CommitteeMember> findByCpf(String cpf);
    Optional<CommitteeMember> findByIfRegistration(String ifRegistration);
}
