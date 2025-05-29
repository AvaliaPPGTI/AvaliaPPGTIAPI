package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import ifpb.edu.br.avaliappgti.model.Application;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.CommitteeMember;

import ifpb.edu.br.avaliappgti.model.StageEvaluation;

@Repository
public interface StageEvaluationRepository extends JpaRepository<StageEvaluation, Integer> {
    // Find a stage evaluation for a specific application and process stage
    Optional<StageEvaluation> findByApplicationAndProcessStage(Application application, ProcessStage processStage);

    // Find all stage evaluations for a specific application
    List<StageEvaluation> findByApplication(Application application);

    // Find all stage evaluations for a specific process stage
    List<StageEvaluation> findByProcessStage(ProcessStage processStage);

    // Find all stage evaluations conducted by a specific faculty member
    List<StageEvaluation> findByCommitteeMember(CommitteeMember committeeMember);

    // Find all evaluations where the candidate was eliminated in that stage
    List<StageEvaluation> findByIsEliminatedInStageTrue();

    @Override
    @EntityGraph(attributePaths = {"application.candidate", "processStage", "committeeMember"})
    StageEvaluation save(StageEvaluation entity);

    @Override
    @EntityGraph(attributePaths = {"application", "application.candidate", "application.selectionProcess", "processStage", "committeeMember"})
    Optional<StageEvaluation> findById(Integer id);

}
