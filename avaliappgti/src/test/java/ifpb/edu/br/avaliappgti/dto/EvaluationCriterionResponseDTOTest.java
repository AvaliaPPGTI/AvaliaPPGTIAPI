package ifpb.edu.br.avaliappgti.dto;

import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationCriterionResponseDTOTest {

    @Test
    void constructor_withTopLevelCriterion_shouldMapFieldsCorrectly() {
        // Arrange
        ProcessStage stage = new ProcessStage();
        stage.setId(1);

        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setId(10);
        criterion.setCriterionDescription("Critério Principal");
        criterion.setMaximumScore(BigDecimal.valueOf(10));
        criterion.setWeight(BigDecimal.valueOf(1.0));
        criterion.setProcessStage(stage);
        criterion.setParent(null);
        criterion.setChildren(Collections.emptyList());

        // Act
        EvaluationCriterionResponseDTO dto = new EvaluationCriterionResponseDTO(criterion);

        // Assert
        assertEquals(10, dto.getId());
        assertEquals("Critério Principal", dto.getDescription());
        assertEquals(BigDecimal.valueOf(10), dto.getMaximumScore());
        assertEquals(BigDecimal.valueOf(1.0), dto.getWeight());
        assertEquals(1, dto.getProcessStageId());
        assertNull(dto.getParentId());
        assertTrue(dto.getChildren() == null || dto.getChildren().isEmpty());
        assertTrue(dto.isLeaf());
        assertNull(dto.getScoreObtained());
        assertNull(dto.getAggregatedScore());
    }

    @Test
    void constructor_withSubCriterion_shouldMapParentAndChildren() {
        // Arrange
        EvaluationCriterion parent = new EvaluationCriterion();
        parent.setId(1);

        EvaluationCriterion child = new EvaluationCriterion();
        child.setId(2);
        child.setCriterionDescription("Subcritério");
        child.setMaximumScore(BigDecimal.valueOf(5));
        child.setWeight(BigDecimal.valueOf(0.5));
        child.isLeaf();
        child.setParent(parent);
        child.setChildren(Collections.emptyList());

        parent.setChildren(List.of(child));

        // Act
        EvaluationCriterionResponseDTO dto = new EvaluationCriterionResponseDTO(parent);

        // Assert
        assertEquals(1, dto.getId());
        assertEquals(1, dto.getChildren().get(0).getParentId());
        assertEquals(2, dto.getChildren().get(0).getId());
        assertEquals("Subcritério", dto.getChildren().get(0).getDescription());
        assertTrue(dto.getChildren().get(0).isLeaf());
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        EvaluationCriterionResponseDTO dto = new EvaluationCriterionResponseDTO();

        dto.setDescription("Test Desc");
        dto.setMaximumScore(BigDecimal.valueOf(8));
        dto.setWeight(BigDecimal.valueOf(0.75));
        dto.setProcessStageId(99);
        dto.setParentId(88);
        dto.setLeaf(true);
        dto.setScoreObtained(BigDecimal.valueOf(7.5));
        dto.setAggregatedScore(BigDecimal.valueOf(8.0));

        EvaluationCriterionResponseDTO child = new EvaluationCriterionResponseDTO();
        child.setDescription("Filho");
        dto.setChildren(List.of(child));

        assertEquals("Test Desc", dto.getDescription());
        assertEquals(BigDecimal.valueOf(8), dto.getMaximumScore());
        assertEquals(BigDecimal.valueOf(0.75), dto.getWeight());
        assertEquals(99, dto.getProcessStageId());
        assertEquals(88, dto.getParentId());
        assertTrue(dto.isLeaf());
        assertEquals(BigDecimal.valueOf(7.5), dto.getScoreObtained());
        assertEquals(BigDecimal.valueOf(8.0), dto.getAggregatedScore());
        assertEquals("Filho", dto.getChildren().get(0).getDescription());
    }

    @Test
    void constructor_withNullChildren_shouldHandleGracefully() {
        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setId(5);
        criterion.setCriterionDescription("Sem filhos");
        criterion.setChildren(null); // Important case
        criterion.isLeaf();

        EvaluationCriterionResponseDTO dto = new EvaluationCriterionResponseDTO(criterion);

        assertEquals(5, dto.getId());
        assertNull(dto.getChildren());
    }
}
