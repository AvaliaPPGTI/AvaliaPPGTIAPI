package ifpb.edu.br.avaliappgti.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.*;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StageEvaluationCreateDTOTest {

    private Validator validator;

    @BeforeEach
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        StageEvaluationCreateDTO dto = new StageEvaluationCreateDTO();
        LocalDateTime now = LocalDateTime.now();

        dto.setApplicationId(1);
        dto.setProcessStageId(2);
        dto.setCommitteeMemberId(3);
        dto.setEvaluationDate(now);

        assertEquals(1, dto.getApplicationId());
        assertEquals(2, dto.getProcessStageId());
        assertEquals(3, dto.getCommitteeMemberId());
        assertEquals(now, dto.getEvaluationDate());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        StageEvaluationCreateDTO dto = new StageEvaluationCreateDTO(1, 2, 3, now);

        assertEquals(1, dto.getApplicationId());
        assertEquals(2, dto.getProcessStageId());
        assertEquals(3, dto.getCommitteeMemberId());
        assertEquals(now, dto.getEvaluationDate());
    }

    @Test
    void testValidationFailsWhenRequiredFieldsMissing() {
        StageEvaluationCreateDTO dto = new StageEvaluationCreateDTO();
        Set<ConstraintViolation<StageEvaluationCreateDTO>> violations = validator.validate(dto);

        assertEquals(2, violations.size());

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("applicationId")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("processStageId")));
    }

    @Test
    void testValidationPassesWithValidData() {
        StageEvaluationCreateDTO dto = new StageEvaluationCreateDTO();
        dto.setApplicationId(1);
        dto.setProcessStageId(2);
        dto.setCommitteeMemberId(3);
        dto.setEvaluationDate(LocalDateTime.now());

        Set<ConstraintViolation<StageEvaluationCreateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
