package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import ifpb.edu.br.avaliappgti.model.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
