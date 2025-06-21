package ifpb.edu.br.avaliappgti.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateSubCriterionRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CreateSubCriterionRequestDTO buildValidDTO() {
        CreateSubCriterionRequestDTO dto = new CreateSubCriterionRequestDTO();
        dto.setDescription("Subcritério: clareza textual");
        dto.setMaximumScore(BigDecimal.valueOf(5));
        dto.setWeight(BigDecimal.valueOf(0.5));
        return dto;
    }

    @Test
    void validDTO_shouldPassValidation() {
        CreateSubCriterionRequestDTO dto = buildValidDTO();
        Set<ConstraintViolation<CreateSubCriterionRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation errors for valid DTO");
    }

    @Test
    void missingDescription_shouldFailValidation() {
        CreateSubCriterionRequestDTO dto = buildValidDTO();
        dto.setDescription(null);
        Set<ConstraintViolation<CreateSubCriterionRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void blankDescription_shouldFailValidation() {
        CreateSubCriterionRequestDTO dto = buildValidDTO();
        dto.setDescription("   ");
        Set<ConstraintViolation<CreateSubCriterionRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void missingMaximumScore_shouldFailValidation() {
        CreateSubCriterionRequestDTO dto = buildValidDTO();
        dto.setMaximumScore(null);
        Set<ConstraintViolation<CreateSubCriterionRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("maximumScore")));
    }

    @Test
    void negativeMaximumScore_shouldFailValidation() {
        CreateSubCriterionRequestDTO dto = buildValidDTO();
        dto.setMaximumScore(BigDecimal.valueOf(-1));
        Set<ConstraintViolation<CreateSubCriterionRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("maximumScore")));
    }

    @Test
    void missingWeight_shouldFailValidation() {
        CreateSubCriterionRequestDTO dto = buildValidDTO();
        dto.setWeight(null);
        Set<ConstraintViolation<CreateSubCriterionRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("weight")));
    }

    @Test
    void negativeWeight_shouldFailValidation() {
        CreateSubCriterionRequestDTO dto = buildValidDTO();
        dto.setWeight(BigDecimal.valueOf(-0.1));
        Set<ConstraintViolation<CreateSubCriterionRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("weight")));
    }

}
