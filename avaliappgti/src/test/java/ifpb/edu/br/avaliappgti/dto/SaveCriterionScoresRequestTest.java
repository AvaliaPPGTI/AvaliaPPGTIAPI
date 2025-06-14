package ifpb.edu.br.avaliappgti.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SaveCriterionScoresRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CriterionScoreInputDTO validCriterionScoreInputDTO() {
        return new CriterionScoreInputDTO(1, new BigDecimal("50.0"));
    }

    private CriterionScoreInputDTO invalidCriterionScoreInputDTO() {
        return new CriterionScoreInputDTO(null, new BigDecimal("-1"));
    }

    @Test
    void testValidScoresList() {
        List<CriterionScoreInputDTO> scores = new ArrayList<>();
        scores.add(validCriterionScoreInputDTO());

        SaveCriterionScoresRequest request = new SaveCriterionScoresRequest(scores);
        Set<ConstraintViolation<SaveCriterionScoresRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullScoresList() {
        SaveCriterionScoresRequest request = new SaveCriterionScoresRequest(null);
        Set<ConstraintViolation<SaveCriterionScoresRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        boolean hasNotNullMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals("List of scores cannot be null"));

        assertTrue(hasNotNullMessage);
    }

    @Test
    void testEmptyScoresList() {
        SaveCriterionScoresRequest request = new SaveCriterionScoresRequest(Collections.emptyList());
        Set<ConstraintViolation<SaveCriterionScoresRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        boolean hasNotEmptyMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals("At least one score must be provided"));

        assertTrue(hasNotEmptyMessage);
    }

    @Test
    void testInvalidScoreInsideList() {
        List<CriterionScoreInputDTO> scores = new ArrayList<>();
        scores.add(validCriterionScoreInputDTO());
        scores.add(invalidCriterionScoreInputDTO()); // invalid item inside list

        SaveCriterionScoresRequest request = new SaveCriterionScoresRequest(scores);
        Set<ConstraintViolation<SaveCriterionScoresRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());

        // There should be violations related to nested CriterionScoreInputDTO properties
        boolean foundNestedViolation = violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().startsWith("scores[1].evaluationCriterionId") ||
                        v.getPropertyPath().toString().startsWith("scores[1].scoreObtained")
        );

        assertTrue(foundNestedViolation);
    }

    @Test
    void testGettersAndSetters() {
        SaveCriterionScoresRequest request = new SaveCriterionScoresRequest();
        List<CriterionScoreInputDTO> scores = new ArrayList<>();
        scores.add(validCriterionScoreInputDTO());

        request.setScores(scores);
        assertEquals(scores, request.getScores());
    }
}
