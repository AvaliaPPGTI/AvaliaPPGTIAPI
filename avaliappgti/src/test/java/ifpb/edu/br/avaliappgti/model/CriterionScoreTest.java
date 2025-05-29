package ifpb.edu.br.avaliappgti.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CriterionScoreTest {

    @Test
    void testAllArgsConstructor() {
        StageEvaluation stageEvaluation = new StageEvaluation();
        EvaluationCriterion evaluationCriterion = new EvaluationCriterion();
        BigDecimal score = new BigDecimal("9.75");

        CriterionScore criterionScore = new CriterionScore(stageEvaluation, evaluationCriterion, score);

        assertEquals(stageEvaluation, criterionScore.getStageEvaluation());
        assertEquals(evaluationCriterion, criterionScore.getEvaluationCriterion());
        assertEquals(score, criterionScore.getScoreObtained());
    }

    @Test
    void testSettersAndGetters() {
        CriterionScore criterionScore = new CriterionScore();

        StageEvaluation stageEvaluation = new StageEvaluation();
        EvaluationCriterion evaluationCriterion = new EvaluationCriterion();
        BigDecimal score = new BigDecimal("8.50");

        criterionScore.setId(10);
        criterionScore.setStageEvaluation(stageEvaluation);
        criterionScore.setEvaluationCriterion(evaluationCriterion);
        criterionScore.setScoreObtained(score);

        assertEquals(10, criterionScore.getId());
        assertEquals(stageEvaluation, criterionScore.getStageEvaluation());
        assertEquals(evaluationCriterion, criterionScore.getEvaluationCriterion());
        assertEquals(score, criterionScore.getScoreObtained());
    }
}
