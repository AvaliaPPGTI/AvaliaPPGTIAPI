package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import ifpb.edu.br.avaliappgti.model.Candidate;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
    Optional<Candidate> findByEmail(String email);
    Optional<Candidate> findByCpf(String cpf);

    // @EntityGraph to fetch related entities eagerly
    // 'candidateDocument' is the field name in the Candidate entity that points to CandidateDocument
    // 'quota' is the field name in the Candidate entity that points to Quota
    @EntityGraph(attributePaths = {"candidateDocument", "quota"})
    Optional<Candidate> findById(Integer id);
}
