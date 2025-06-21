package ifpb.edu.br.avaliappgti.dto;

import ifpb.edu.br.avaliappgti.model.CriterionScore;
import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CriterionScoreResponseDTOTest {

    @Test
    void constructor_withValidCriterionScore_shouldMapFieldsCorrectly() {
        // Setup
        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setId(10);
        criterion.setCriterionDescription("Clareza e coerência");
        criterion.setMaximumScore(BigDecimal.valueOf(5));

        StageEvaluation evaluation = new StageEvaluation();
        evaluation.setId(20);

        CriterionScore criterionScore = new CriterionScore();
        criterionScore.setId(1);
        criterionScore.setStageEvaluation(evaluation);
        criterionScore.setEvaluationCriterion(criterion);
        criterionScore.setScoreObtained(BigDecimal.valueOf(4.5));

        // Act
        CriterionScoreResponseDTO dto = new CriterionScoreResponseDTO(criterionScore);

        // Assert
        assertEquals(1, dto.getId());
        assertEquals(20, dto.getStageEvaluationId());
        assertEquals(10, dto.getEvaluationCriterionId());
        assertEquals("Clareza e coerência", dto.getEvaluationCriterionDescription());
        assertEquals(BigDecimal.valueOf(5), dto.getEvaluationCriterionMaximumScore());
        assertEquals(BigDecimal.valueOf(4.5), dto.getScoreObtained());
    }

    @Test
    void constructor_withNullNestedObjects_shouldHandleGracefully() {
        // Setup
        CriterionScore criterionScore = new CriterionScore();
        criterionScore.setId(1);
        criterionScore.setStageEvaluation(null);
        criterionScore.setEvaluationCriterion(null);
        criterionScore.setScoreObtained(BigDecimal.valueOf(2.0));

        // Act
        CriterionScoreResponseDTO dto = new CriterionScoreResponseDTO(criterionScore);

        // Assert
        assertEquals(1, dto.getId());
        assertNull(dto.getStageEvaluationId());
        assertNull(dto.getEvaluationCriterionId());
        assertNull(dto.getEvaluationCriterionDescription());
        assertNull(dto.getEvaluationCriterionMaximumScore());
        assertEquals(BigDecimal.valueOf(2.0), dto.getScoreObtained());
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        CriterionScoreResponseDTO dto = new CriterionScoreResponseDTO();

        dto.setStageEvaluationId(100);
        dto.setEvaluationCriterionId(200);
        dto.setEvaluationCriterionDescription("Descrição de teste");
        dto.setEvaluationCriterionMaximumScore(BigDecimal.valueOf(10));
        dto.setScoreObtained(BigDecimal.valueOf(8.5));

        assertEquals(100, dto.getStageEvaluationId());
        assertEquals(200, dto.getEvaluationCriterionId());
        assertEquals("Descrição de teste", dto.getEvaluationCriterionDescription());
        assertEquals(BigDecimal.valueOf(10), dto.getEvaluationCriterionMaximumScore());
        assertEquals(BigDecimal.valueOf(8.5), dto.getScoreObtained());
    }
}
