package ifpb.edu.br.avaliappgti.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateTopLevelCriterionRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CreateTopLevelCriterionRequestDTO buildValidDTO() {
        CreateTopLevelCriterionRequestDTO dto = new CreateTopLevelCriterionRequestDTO();
        dto.setDescription("Apresentação em eventos");
        dto.setMaximumScore(BigDecimal.valueOf(10));
        dto.setWeight(BigDecimal.valueOf(1));
        dto.setProcessStageId(5);
        return dto;
    }

    @Test
    void validDTO_shouldPassValidation() {
        CreateTopLevelCriterionRequestDTO dto = buildValidDTO();
        Set<ConstraintViolation<CreateTopLevelCriterionRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    void missingDescription_shouldFailValidation() {
        CreateTopLevelCriterionRequestDTO dto = buildValidDTO();
        dto.setDescription(null);
        Set<ConstraintViolation<CreateTopLevelCriterionRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void blankDescription_shouldFailValidation() {
        CreateTopLevelCriterionRequestDTO dto = buildValidDTO();
        dto.setDescription("  ");
        Set<ConstraintViolation<CreateTopLevelCriterionRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void missingMaximumScore_shouldFailValidation() {
        CreateTopLevelCriterionRequestDTO dto = buildValidDTO();
        dto.setMaximumScore(null);
        Set<ConstraintViolation<CreateTopLevelCriterionRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("maximumScore")));
    }

    @Test
    void negativeMaximumScore_shouldFailValidation() {
        CreateTopLevelCriterionRequestDTO dto = buildValidDTO();
        dto.setMaximumScore(BigDecimal.valueOf(-1));
        Set<ConstraintViolation<CreateTopLevelCriterionRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("maximumScore")));
    }

    @Test
    void missingProcessStageId_shouldFailValidation() {
        CreateTopLevelCriterionRequestDTO dto = buildValidDTO();
        dto.setProcessStageId(null);
        Set<ConstraintViolation<CreateTopLevelCriterionRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("processStageId")));
    }

    @Test
    void nullWeight_shouldPassValidation() {
        CreateTopLevelCriterionRequestDTO dto = buildValidDTO();
        dto.setWeight(null);
        Set<ConstraintViolation<CreateTopLevelCriterionRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Weight is optional and should allow null");
    }
}
