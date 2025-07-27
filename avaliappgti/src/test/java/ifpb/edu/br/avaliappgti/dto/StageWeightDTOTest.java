package ifpb.edu.br.avaliappgti.dto;

import ifpb.edu.br.avaliappgti.dto.StageWeightDTO;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StageWeightDTOTest {

    @Test
    void testConstructorMapsFieldsCorrectly() {
        ProcessStage stage = new ProcessStage();
        stage.setId(123);
        stage.setStageName("Entrevista");
        stage.setStageOrder(2);
        stage.setStageWeight(new BigDecimal("0.5"));

        StageWeightDTO dto = new StageWeightDTO(stage);

        assertEquals(123, dto.getStageId());
        assertEquals("Entrevista", dto.getStageName());
        assertEquals(2, dto.getStageOrder());
        assertEquals(new BigDecimal("0.5"), dto.getStageWeight());
    }

    @Test
    void testSettersAndGetters() {
        StageWeightDTO dto = new StageWeightDTO(new ProcessStage());
        dto.setStageId(10);
        dto.setStageName("Currículo");
        dto.setStageOrder(1);
        dto.setStageWeight(new BigDecimal("0.4"));

        assertEquals(10, dto.getStageId());
        assertEquals("Currículo", dto.getStageName());
        assertEquals(1, dto.getStageOrder());
        assertEquals(new BigDecimal("0.4"), dto.getStageWeight());
    }
}
