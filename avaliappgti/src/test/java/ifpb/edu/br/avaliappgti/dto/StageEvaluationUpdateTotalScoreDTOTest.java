package ifpb.edu.br.avaliappgti.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StageEvaluationUpdateTotalScoreDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidDTO() {
        StageEvaluationUpdateTotalScoreDTO dto = new StageEvaluationUpdateTotalScoreDTO(new BigDecimal("85.75"));
        Set<ConstraintViolation<StageEvaluationUpdateTotalScoreDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("85.75"), dto.getTotalStageScore());
    }

    @Test
    void testNullTotalStageScore() {
        StageEvaluationUpdateTotalScoreDTO dto = new StageEvaluationUpdateTotalScoreDTO(null);
        Set<ConstraintViolation<StageEvaluationUpdateTotalScoreDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        ConstraintViolation<StageEvaluationUpdateTotalScoreDTO> violation = violations.iterator().next();
        assertEquals("Total stage score is required", violation.getMessage());
    }

    @Test
    void testNegativeTotalStageScore() {
        StageEvaluationUpdateTotalScoreDTO dto = new StageEvaluationUpdateTotalScoreDTO(new BigDecimal("-10.0"));
        Set<ConstraintViolation<StageEvaluationUpdateTotalScoreDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        boolean hasDecimalMinMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Total score must be non-negative"));

        assertTrue(hasDecimalMinMessage);
    }

    @Test
    void testSetterAndGetter() {
        StageEvaluationUpdateTotalScoreDTO dto = new StageEvaluationUpdateTotalScoreDTO();
        dto.setTotalStageScore(new BigDecimal("90.0"));
        assertEquals(new BigDecimal("90.0"), dto.getTotalStageScore());
    }
}
