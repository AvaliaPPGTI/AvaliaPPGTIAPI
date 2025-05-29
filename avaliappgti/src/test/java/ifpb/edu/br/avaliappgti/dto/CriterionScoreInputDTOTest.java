package ifpb.edu.br.avaliappgti.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CriterionScoreInputDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidDTO() {
        CriterionScoreInputDTO dto = new CriterionScoreInputDTO(1, new BigDecimal("75.50"));
        Set<ConstraintViolation<CriterionScoreInputDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        assertEquals(1, dto.getEvaluationCriterionId());
        assertEquals(new BigDecimal("75.50"), dto.getScoreValue());
    }

    @Test
    void testNullEvaluationCriterionId() {
        CriterionScoreInputDTO dto = new CriterionScoreInputDTO(null, new BigDecimal("50.00"));
        Set<ConstraintViolation<CriterionScoreInputDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        boolean hasNotNullMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Evaluation criterion ID is required"));

        assertTrue(hasNotNullMessage);
    }

    @Test
    void testNullScoreValue() {
        CriterionScoreInputDTO dto = new CriterionScoreInputDTO(1, null);
        Set<ConstraintViolation<CriterionScoreInputDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        boolean hasNotNullMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Score value is required"));

        assertTrue(hasNotNullMessage);
    }

    @Test
    void testNegativeScoreValue() {
        CriterionScoreInputDTO dto = new CriterionScoreInputDTO(1, new BigDecimal("-1.0"));
        Set<ConstraintViolation<CriterionScoreInputDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        boolean hasDecimalMinMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Score must be non-negative"));

        assertTrue(hasDecimalMinMessage);
    }

    @Test
    void testSettersAndGetters() {
        CriterionScoreInputDTO dto = new CriterionScoreInputDTO();
        dto.setEvaluationCriterionId(5);
        dto.setScoreValue(new BigDecimal("88.88"));

        assertEquals(5, dto.getEvaluationCriterionId());
        assertEquals(new BigDecimal("88.88"), dto.getScoreValue());
    }
}
