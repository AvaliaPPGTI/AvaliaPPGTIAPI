package ifpb.edu.br.avaliappgti.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StageEvaluationTest {

    @Test
    void testAllArgsConstructor() {
        Application application = new Application();
        ProcessStage processStage = new ProcessStage();
        CommitteeMember committeeMember = new CommitteeMember();

        BigDecimal score = new BigDecimal("7.50");
        Boolean eliminated = true;
        LocalDateTime evaluationDate = LocalDateTime.of(2024, 10, 1, 14, 30);
        String observations = "Bom desempenho";

        StageEvaluation evaluation = new StageEvaluation(
                application,
                processStage,
                score,
                eliminated,
                evaluationDate,
                committeeMember,
                observations
        );

        assertEquals(application, evaluation.getApplication());
        assertEquals(processStage, evaluation.getProcessStage());
        assertEquals(score, evaluation.getTotalStageScore());
        assertEquals(eliminated, evaluation.getIsEliminatedInStage());
        assertEquals(evaluationDate, evaluation.getEvaluationDate());
        assertEquals(committeeMember, evaluation.getCommitteeMember());
        assertEquals(observations, evaluation.getObservations());
    }

    @Test
    void testSettersAndGetters() {
        StageEvaluation evaluation = new StageEvaluation();

        Application application = new Application();
        ProcessStage processStage = new ProcessStage();
        CommitteeMember committeeMember = new CommitteeMember();
        BigDecimal score = new BigDecimal("8.75");
        LocalDateTime date = LocalDateTime.of(2024, 12, 5, 10, 0);

        evaluation.setId(42);
        evaluation.setApplication(application);
        evaluation.setProcessStage(processStage);
        evaluation.setTotalStageScore(score);
        evaluation.setIsEliminatedInStage(false);
        evaluation.setEvaluationDate(date);
        evaluation.setCommitteeMember(committeeMember);
        evaluation.setObservations("Observação de teste");

        assertEquals(42, evaluation.getId());
        assertEquals(application, evaluation.getApplication());
        assertEquals(processStage, evaluation.getProcessStage());
        assertEquals(score, evaluation.getTotalStageScore());
        assertFalse(evaluation.getIsEliminatedInStage());
        assertEquals(date, evaluation.getEvaluationDate());
        assertEquals(committeeMember, evaluation.getCommitteeMember());
        assertEquals("Observação de teste", evaluation.getObservations());
    }
}
