package ifpb.edu.br.avaliappgti.dto;

import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateStageWeightDTOTest {

    private final Validator validator;

    public UpdateStageWeightDTOTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSettersAndGetters() {
        UpdateStageWeightDTO dto = new UpdateStageWeightDTO();
        dto.setStageId(123);
        dto.setStageWeight(new BigDecimal("0.75"));

        assertEquals(123, dto.getStageId());
        assertEquals(new BigDecimal("0.75"), dto.getStageWeight());
    }

    @Test
    void testValidation_validValues() {
        UpdateStageWeightDTO dto = new UpdateStageWeightDTO();
        dto.setStageId(1);
        dto.setStageWeight(new BigDecimal("0.5"));

        Set<ConstraintViolation<UpdateStageWeightDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_nullStageId() {
        UpdateStageWeightDTO dto = new UpdateStageWeightDTO();
        dto.setStageId(null);
        dto.setStageWeight(new BigDecimal("0.5"));

        Set<ConstraintViolation<UpdateStageWeightDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("stageId")));
    }

    @Test
    void testValidation_nullStageWeight() {
        UpdateStageWeightDTO dto = new UpdateStageWeightDTO();
        dto.setStageId(1);
        dto.setStageWeight(null);

        Set<ConstraintViolation<UpdateStageWeightDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("stageWeight")));
    }

    @Test
    void testValidation_negativeStageWeight() {
        UpdateStageWeightDTO dto = new UpdateStageWeightDTO();
        dto.setStageId(1);
        dto.setStageWeight(new BigDecimal("-0.1"));

        Set<ConstraintViolation<UpdateStageWeightDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("stageWeight")));
    }

    @Test
    void testValidation_stageWeightGreaterThanOne() {
        UpdateStageWeightDTO dto = new UpdateStageWeightDTO();
        dto.setStageId(1);
        dto.setStageWeight(new BigDecimal("1.1"));

        Set<ConstraintViolation<UpdateStageWeightDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("stageWeight")));
    }
}
