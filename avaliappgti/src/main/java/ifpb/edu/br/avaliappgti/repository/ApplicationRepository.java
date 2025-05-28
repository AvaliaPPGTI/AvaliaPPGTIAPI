package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    //finds candidates whose applications are homologated for a specific research topic
    @Query("SELECT DISTINCT a.candidate FROM Application a " +
            "JOIN a.researchTopic rt " +
            "JOIN ApplicationVerification av ON av.application = a " + // Ensure ApplicationVerification is joined correctly
            "WHERE rt.id = :researchTopicId " +
            "AND av.finalStatus = 1")
    List<Candidate> findHomologatedCandidatesByResearchTopicId(@Param("researchTopicId") Integer researchTopicId);


    // Find an Application by its Candidate and ResearchTopic
    // The 'candidate' and 'researchTopic' here refer to the field names in the Application entity.
    Optional<Application> findByCandidateAndResearchTopic(Candidate candidate, ResearchTopic researchTopic);
}