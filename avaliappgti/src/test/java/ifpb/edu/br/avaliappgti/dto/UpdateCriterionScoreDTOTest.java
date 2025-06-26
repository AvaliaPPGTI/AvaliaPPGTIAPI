package ifpb.edu.br.avaliappgti.dto;

import ifpb.edu.br.avaliappgti.dto.UpdateCriterionScoreDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.*;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateCriterionScoreDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validScore_shouldPassValidation() {
        UpdateCriterionScoreDTO dto = new UpdateCriterionScoreDTO(BigDecimal.valueOf(8.5));
        Set<ConstraintViolation<UpdateCriterionScoreDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "No violations expected for valid score");
    }

    @Test
    void nullScore_shouldFailValidation() {
        UpdateCriterionScoreDTO dto = new UpdateCriterionScoreDTO(null);
        Set<ConstraintViolation<UpdateCriterionScoreDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Violations expected for null score");

        boolean hasNotNullMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Score obtained is required"));
        assertTrue(hasNotNullMessage, "Should contain 'Score obtained is required' message");
    }

    @Test
    void negativeScore_shouldFailValidation() {
        UpdateCriterionScoreDTO dto = new UpdateCriterionScoreDTO(BigDecimal.valueOf(-1));
        Set<ConstraintViolation<UpdateCriterionScoreDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Violations expected for negative score");

        boolean hasDecimalMinMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Score must be non-negative"));
        assertTrue(hasDecimalMinMessage, "Should contain 'Score must be non-negative' message");
    }

    @Test
    void testGettersAndSetters() {
        UpdateCriterionScoreDTO dto = new UpdateCriterionScoreDTO();
        dto.setScoreObtained(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, dto.getScoreObtained());
    }
}
