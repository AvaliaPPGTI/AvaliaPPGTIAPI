package ifpb.edu.br.avaliappgti.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import ifpb.edu.br.avaliappgti.model.Candidate;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
    Optional<Candidate> findByEmail(String email);
    Optional<Candidate> findByCpf(String cpf);

    // Keep "quota" if it's a direct ManyToOne relationship from Candidate.
    @EntityGraph(attributePaths = {"quota"}) // Only fetch quota directly from Candidate
    Optional<Candidate> findById(Integer id);
}
