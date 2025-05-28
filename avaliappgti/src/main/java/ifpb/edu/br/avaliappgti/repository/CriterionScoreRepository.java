package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifpb.edu.br.avaliappgti.model.CriterionScore;
import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;

@Repository
public interface CriterionScoreRepository extends JpaRepository<CriterionScore, Integer> {
    // Find all criterion scores for a specific stage evaluation
    List<CriterionScore> findByStageEvaluation(StageEvaluation stageEvaluation);

    // Find a specific criterion score for a given stage evaluation and evaluation criterion
    Optional<CriterionScore> findByStageEvaluationAndEvaluationCriterion(StageEvaluation stageEvaluation, EvaluationCriterion evaluationCriterion);
}