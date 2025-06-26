package ifpb.edu.br.avaliappgti.model;

import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationCriterionTest {

    @Test
    void testNoArgsConstructor_shouldInitializeNulls() {
        EvaluationCriterion criterion = new EvaluationCriterion();

        assertNull(criterion.getId());
        assertNull(criterion.getProcessStage());
        assertNull(criterion.getCriterionDescription());
        assertNull(criterion.getMaximumScore());
        assertNull(criterion.getWeight());
        assertNull(criterion.getParent());
        assertNull(criterion.getChildren());
        assertTrue(criterion.isLeaf()); // null children -> considered leaf
        assertTrue(criterion.isTopLevel()); // null parent -> considered top-level
    }

    @Test
    void testAllArgsConstructor_shouldSetFieldsCorrectly() {
        ProcessStage stage = new ProcessStage();
        EvaluationCriterion parent = new EvaluationCriterion();

        EvaluationCriterion criterion = new EvaluationCriterion(
                stage,
                "Conhecimentos técnicos",
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(0.5),
                parent
        );

        assertEquals(stage, criterion.getProcessStage());
        assertEquals("Conhecimentos técnicos", criterion.getCriterionDescription());
        assertEquals(BigDecimal.valueOf(10.0), criterion.getMaximumScore());
        assertEquals(BigDecimal.valueOf(0.5), criterion.getWeight());
        assertEquals(parent, criterion.getParent());
        assertNull(criterion.getChildren());
    }

    @Test
    void testIsLeaf_shouldReturnTrueWhenChildrenIsNullOrEmpty() {
        EvaluationCriterion criterion = new EvaluationCriterion();
        assertTrue(criterion.isLeaf()); // children is null

        criterion.setChildren(Collections.emptyList());
        assertTrue(criterion.isLeaf()); // empty children
    }

    @Test
    void testIsLeaf_shouldReturnFalseWhenChildrenExist() {
        EvaluationCriterion child1 = new EvaluationCriterion();
        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setChildren(List.of(child1));
        assertFalse(criterion.isLeaf());
    }

    @Test
    void testIsTopLevel_shouldReturnTrueWhenParentIsNull() {
        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setParent(null);
        assertTrue(criterion.isTopLevel());
    }

    @Test
    void testIsTopLevel_shouldReturnFalseWhenParentExists() {
        EvaluationCriterion parent = new EvaluationCriterion();
        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setParent(parent);
        assertFalse(criterion.isTopLevel());
    }

    @Test
    void testSettersAndGetters_shouldWorkCorrectly() {
        ProcessStage stage = new ProcessStage();
        EvaluationCriterion parent = new EvaluationCriterion();
        EvaluationCriterion criterion = new EvaluationCriterion();

        criterion.setId(1);
        criterion.setProcessStage(stage);
        criterion.setCriterionDescription("Clareza e coesão");
        criterion.setMaximumScore(BigDecimal.TEN);
        criterion.setWeight(BigDecimal.valueOf(0.75));
        criterion.setParent(parent);

        assertEquals(1, criterion.getId());
        assertEquals(stage, criterion.getProcessStage());
        assertEquals("Clareza e coesão", criterion.getCriterionDescription());
        assertEquals(BigDecimal.TEN, criterion.getMaximumScore());
        assertEquals(BigDecimal.valueOf(0.75), criterion.getWeight());
        assertEquals(parent, criterion.getParent());
    }
}
