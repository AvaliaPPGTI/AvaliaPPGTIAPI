package ifpb.edu.br.avaliappgti.model;

import ifpb.edu.br.avaliappgti.model.CriterionScore;
import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CriterionScoreTest {

    @Test
    void testNoArgsConstructor_shouldInitializeNulls() {
        CriterionScore score = new CriterionScore();

        assertNull(score.getId());
        assertNull(score.getStageEvaluation());
        assertNull(score.getEvaluationCriterion());
        assertNull(score.getScoreObtained());
    }

    @Test
    void testAllArgsConstructor_shouldSetFieldsCorrectly() {
        StageEvaluation evaluation = new StageEvaluation();
        EvaluationCriterion criterion = new EvaluationCriterion();
        BigDecimal value = BigDecimal.valueOf(7.5);

        CriterionScore score = new CriterionScore(evaluation, criterion, value);

        assertEquals(evaluation, score.getStageEvaluation());
        assertEquals(criterion, score.getEvaluationCriterion());
        assertEquals(value, score.getScoreObtained());
    }

    @Test
    void testSettersAndGetters_shouldWorkCorrectly() {
        StageEvaluation evaluation = new StageEvaluation();
        EvaluationCriterion criterion = new EvaluationCriterion();
        BigDecimal value = BigDecimal.valueOf(9.0);

        CriterionScore score = new CriterionScore();
        score.setId(100);
        score.setStageEvaluation(evaluation);
        score.setEvaluationCriterion(criterion);
        score.setScoreObtained(value);

        assertEquals(100, score.getId());
        assertEquals(evaluation, score.getStageEvaluation());
        assertEquals(criterion, score.getEvaluationCriterion());
        assertEquals(value, score.getScoreObtained());
    }
}
