package ifpb.edu.br.avaliappgti.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProcessStageTest {

    @Test
    void testConstructorAndGetters() {
        SelectionProcess selectionProcess = new SelectionProcess();
        selectionProcess.setId(1);
        selectionProcess.setName("Processo Seletivo 2025");

        ProcessStage processStage = new ProcessStage(
                selectionProcess,
                "Entrevista",
                2,
                "Eliminatory",
                new BigDecimal("60.00")
        );

        assertEquals("Entrevista", processStage.getStageName());
        assertEquals(2, processStage.getStageOrder());
        assertEquals("Eliminatory", processStage.getStageCharacter());
        assertEquals(new BigDecimal("60.00"), processStage.getMinimumPassingScore());
        assertEquals(selectionProcess, processStage.getSelectionProcess());
    }

    @Test
    void testSettersAndGetters() {
        ProcessStage processStage = new ProcessStage();

        SelectionProcess selectionProcess = new SelectionProcess();
        selectionProcess.setId(1);

        processStage.setId(10);
        processStage.setSelectionProcess(selectionProcess);
        processStage.setStageName("Prova Escrita");
        processStage.setStageOrder(1);
        processStage.setStageCharacter("Classificatory");
        processStage.setMinimumPassingScore(new BigDecimal("50.00"));

        assertEquals(10, processStage.getId());
        assertEquals(selectionProcess, processStage.getSelectionProcess());
        assertEquals("Prova Escrita", processStage.getStageName());
        assertEquals(1, processStage.getStageOrder());
        assertEquals("Classificatory", processStage.getStageCharacter());
        assertEquals(new BigDecimal("50.00"), processStage.getMinimumPassingScore());
    }
}
