package ifpb.edu.br.avaliappgti.dto;

import ifpb.edu.br.avaliappgti.dto.UpdateEvaluationCriterionRequestDTO;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateEvaluationCriterionRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void allFieldsValid_shouldPassValidation() {
        UpdateEvaluationCriterionRequestDTO dto = new UpdateEvaluationCriterionRequestDTO();
        dto.setDescription("Updated Description");
        dto.setMaximumScore(BigDecimal.valueOf(10));
        dto.setWeight(BigDecimal.valueOf(1.5));

        Set<ConstraintViolation<UpdateEvaluationCriterionRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void nullFields_shouldPassValidation_becauseTheyAreOptional() {
        UpdateEvaluationCriterionRequestDTO dto = new UpdateEvaluationCriterionRequestDTO();
        dto.setDescription(null);
        dto.setMaximumScore(null);
        dto.setWeight(null);

        Set<ConstraintViolation<UpdateEvaluationCriterionRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation errors when all fields are null (PATCH case)");
    }

    @Test
    void negativeMaximumScore_shouldFailValidation() {
        UpdateEvaluationCriterionRequestDTO dto = new UpdateEvaluationCriterionRequestDTO();
        dto.setMaximumScore(BigDecimal.valueOf(-5));

        Set<ConstraintViolation<UpdateEvaluationCriterionRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("maximumScore")));
    }

    @Test
    void negativeWeight_shouldFailValidation() {
        UpdateEvaluationCriterionRequestDTO dto = new UpdateEvaluationCriterionRequestDTO();
        dto.setWeight(BigDecimal.valueOf(-0.5));

        Set<ConstraintViolation<UpdateEvaluationCriterionRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("weight")));
    }

    @Test
    void testGettersAndSetters() {
        UpdateEvaluationCriterionRequestDTO dto = new UpdateEvaluationCriterionRequestDTO();

        dto.setDescription("New Description");
        dto.setMaximumScore(BigDecimal.valueOf(20));
        dto.setWeight(BigDecimal.valueOf(2));

        assertEquals("New Description", dto.getDescription());
        assertEquals(BigDecimal.valueOf(20), dto.getMaximumScore());
        assertEquals(BigDecimal.valueOf(2), dto.getWeight());
    }
}
