package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifpb.edu.br.avaliappgti.model.Application;
import ifpb.edu.br.avaliappgti.model.Candidate;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.model.ResearchTopic;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    Optional<Application> findByCandidateAndSelectionProcess(Candidate candidate, SelectionProcess selectionProcess);
    List<Application> findBySelectionProcess(SelectionProcess selectionProcess);
    List<Application> findByResearchTopic(ResearchTopic researchTopic);
    List<Application> findByApplicationStatus(String status);
    List<Application> findBySelectionProcessAndIsApproved(SelectionProcess process, Boolean isApproved);
    List<Application> findBySelectionProcessOrderByFinalScoreDesc(SelectionProcess selectionProcess);
}