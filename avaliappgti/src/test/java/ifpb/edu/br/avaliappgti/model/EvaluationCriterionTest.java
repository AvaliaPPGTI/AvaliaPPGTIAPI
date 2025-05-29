package ifpb.edu.br.avaliappgti.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationCriterionTest {

    @Test
    void testConstructorAndGetters() {
        ProcessStage stage = new ProcessStage();
        stage.setId(1);
        stage.setStageName("Entrevista");

        BigDecimal maxScore = new BigDecimal("10.00");
        BigDecimal weight = new BigDecimal("1.50");

        EvaluationCriterion criterion = new EvaluationCriterion(
                stage,
                "Clareza na comunicação",
                maxScore,
                weight
        );

        assertEquals(stage, criterion.getProcessStage());
        assertEquals("Clareza na comunicação", criterion.getCriterionDescription());
        assertEquals(maxScore, criterion.getMaximumScore());
        assertEquals(weight, criterion.getWeight());
    }

    @Test
    void testSettersAndGetters() {
        EvaluationCriterion criterion = new EvaluationCriterion();

        ProcessStage stage = new ProcessStage();
        stage.setId(2);

        criterion.setId(10);
        criterion.setProcessStage(stage);
        criterion.setCriterionDescription("Domínio técnico");
        criterion.setMaximumScore(new BigDecimal("8.00"));
        criterion.setWeight(new BigDecimal("1.25"));

        assertEquals(10, criterion.getId());
        assertEquals(stage, criterion.getProcessStage());
        assertEquals("Domínio técnico", criterion.getCriterionDescription());
        assertEquals(new BigDecimal("8.00"), criterion.getMaximumScore());
        assertEquals(new BigDecimal("1.25"), criterion.getWeight());
    }
}
