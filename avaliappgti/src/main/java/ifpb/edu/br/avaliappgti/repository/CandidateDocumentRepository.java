package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifpb.edu.br.avaliappgti.model.CandidateDocument;
import ifpb.edu.br.avaliappgti.model.Candidate;


@Repository
public interface CandidateDocumentRepository extends JpaRepository<CandidateDocument, Integer> {
    Optional<CandidateDocument> findByCandidate(Candidate candidate);
    Optional<CandidateDocument> findByCandidateCpf(String cpf);
}
