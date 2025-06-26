package ifpb.edu.br.avaliappgti.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StageEvaluationTest {

    @Test
    void testNoArgsConstructor_shouldInitializeDefaults() {
        // Act
        StageEvaluation evaluation = new StageEvaluation();

        // Assert
        assertNull(evaluation.getId());
        assertNull(evaluation.getApplication());
        assertNull(evaluation.getProcessStage());
        assertNull(evaluation.getTotalStageScore());
        assertFalse(evaluation.getIsEliminatedInStage()); // Should default to false
        assertNull(evaluation.getEvaluationDate());
        assertNull(evaluation.getCommitteeMember());
        assertNull(evaluation.getObservations());
    }

    @Test
    void testAllArgsConstructor_shouldSetAllFieldsCorrectly() {
        // Arrange
        Application app = new Application();
        ProcessStage stage = new ProcessStage();
        CommitteeMember member = new CommitteeMember();
        BigDecimal score = BigDecimal.valueOf(8.75);
        LocalDateTime now = LocalDateTime.now();
        String observations = "Strong candidate";

        // Act
        StageEvaluation evaluation = new StageEvaluation(
                app,
                stage,
                score,
                true,
                now,
                member,
                observations
        );

        // Assert
        assertEquals(app, evaluation.getApplication());
        assertEquals(stage, evaluation.getProcessStage());
        assertEquals(score, evaluation.getTotalStageScore());
        assertTrue(evaluation.getIsEliminatedInStage());
        assertEquals(now, evaluation.getEvaluationDate());
        assertEquals(member, evaluation.getCommitteeMember());
        assertEquals(observations, evaluation.getObservations());
    }

    @Test
    void testSettersAndGetters_shouldWorkCorrectly() {
        // Arrange
        StageEvaluation evaluation = new StageEvaluation();

        Application app = new Application();
        ProcessStage stage = new ProcessStage();
        CommitteeMember member = new CommitteeMember();
        BigDecimal score = BigDecimal.valueOf(9.25);
        LocalDateTime dateTime = LocalDateTime.of(2025, 6, 18, 10, 30);
        String note = "Promissor";

        // Act
        evaluation.setId(100);
        evaluation.setApplication(app);
        evaluation.setProcessStage(stage);
        evaluation.setTotalStageScore(score);
        evaluation.setIsEliminatedInStage(true);
        evaluation.setEvaluationDate(dateTime);
        evaluation.setCommitteeMember(member);
        evaluation.setObservations(note);

        // Assert
        assertEquals(100, evaluation.getId());
        assertEquals(app, evaluation.getApplication());
        assertEquals(stage, evaluation.getProcessStage());
        assertEquals(score, evaluation.getTotalStageScore());
        assertTrue(evaluation.getIsEliminatedInStage());
        assertEquals(dateTime, evaluation.getEvaluationDate());
        assertEquals(member, evaluation.getCommitteeMember());
        assertEquals(note, evaluation.getObservations());
    }
}
