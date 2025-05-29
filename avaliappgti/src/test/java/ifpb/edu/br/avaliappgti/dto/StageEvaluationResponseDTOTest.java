package ifpb.edu.br.avaliappgti.dto;

import ifpb.edu.br.avaliappgti.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StageEvaluationResponseDTOTest {

    @Test
    void testConstructorWithCompleteEntity() {
        // Arrange
        Candidate candidate = mock(Candidate.class);
        when(candidate.getName()).thenReturn("Jane Doe");

        SelectionProcess selectionProcess = mock(SelectionProcess.class);
        when(selectionProcess.getId()).thenReturn(10);
        when(selectionProcess.getName()).thenReturn("Process 2025");

        Application application = mock(Application.class);
        when(application.getId()).thenReturn(1);
        when(application.getCandidate()).thenReturn(candidate);
        when(application.getSelectionProcess()).thenReturn(selectionProcess);

        ProcessStage processStage = mock(ProcessStage.class);
        when(processStage.getId()).thenReturn(2);
        when(processStage.getStageName()).thenReturn("Document Evaluation");

        CommitteeMember committeeMember = mock(CommitteeMember.class);
        when(committeeMember.getId()).thenReturn(3);
        when(committeeMember.getName()).thenReturn("Dr. Smith");

        StageEvaluation stageEvaluation = mock(StageEvaluation.class);
        when(stageEvaluation.getId()).thenReturn(100);
        when(stageEvaluation.getApplication()).thenReturn(application);
        when(stageEvaluation.getProcessStage()).thenReturn(processStage);
        when(stageEvaluation.getCommitteeMember()).thenReturn(committeeMember);
        when(stageEvaluation.getEvaluationDate()).thenReturn(LocalDateTime.of(2025, 5, 20, 14, 0));
        when(stageEvaluation.getTotalStageScore()).thenReturn(new BigDecimal("88.50"));
        when(stageEvaluation.getIsEliminatedInStage()).thenReturn(false);
        when(stageEvaluation.getObservations()).thenReturn("Well done.");

        // Act
        StageEvaluationResponseDTO dto = new StageEvaluationResponseDTO(stageEvaluation);

        // Assert
        assertEquals(100, dto.getId());
        assertEquals(1, dto.getApplicationId());
        assertEquals("Jane Doe", dto.getApplicationCandidateName());
        assertEquals(10, dto.getApplicationSelectionProcessId());
        assertEquals("Process 2025", dto.getApplicationSelectionProcessName());
        assertEquals(2, dto.getProcessStageId());
        assertEquals("Document Evaluation", dto.getProcessStageName());
        assertEquals(3, dto.getCommitteeMemberId());
        assertEquals("Dr. Smith", dto.getCommitteeMemberName());
        assertEquals(LocalDateTime.of(2025, 5, 20, 14, 0), dto.getEvaluationDate());
        assertEquals(new BigDecimal("88.50"), dto.getTotalStageScore());
        assertFalse(dto.getIsEliminatedInStage());
        assertEquals("Well done.", dto.getObservations());
    }

    @Test
    void testConstructorWithNullNestedFields() {
        StageEvaluation stageEvaluation = mock(StageEvaluation.class);
        when(stageEvaluation.getId()).thenReturn(200);
        when(stageEvaluation.getApplication()).thenReturn(null);
        when(stageEvaluation.getProcessStage()).thenReturn(null);
        when(stageEvaluation.getCommitteeMember()).thenReturn(null);
        when(stageEvaluation.getEvaluationDate()).thenReturn(null);
        when(stageEvaluation.getTotalStageScore()).thenReturn(null);
        when(stageEvaluation.getIsEliminatedInStage()).thenReturn(null);
        when(stageEvaluation.getObservations()).thenReturn(null);

        StageEvaluationResponseDTO dto = new StageEvaluationResponseDTO(stageEvaluation);

        assertEquals(200, dto.getId());
        assertNull(dto.getApplicationId());
        assertNull(dto.getApplicationCandidateName());
        assertNull(dto.getApplicationSelectionProcessId());
        assertNull(dto.getApplicationSelectionProcessName());
        assertNull(dto.getProcessStageId());
        assertNull(dto.getProcessStageName());
        assertNull(dto.getCommitteeMemberId());
        assertNull(dto.getCommitteeMemberName());
        assertNull(dto.getEvaluationDate());
        assertNull(dto.getTotalStageScore());
        assertNull(dto.getIsEliminatedInStage());
        assertNull(dto.getObservations());
    }

    @Test
    void testSettersAndGetters() {
        StageEvaluationResponseDTO dto = new StageEvaluationResponseDTO();

        dto.setId(123);
        dto.setApplicationId(1);
        dto.setApplicationCandidateName("Alice");
        dto.setApplicationSelectionProcessId(2);
        dto.setApplicationSelectionProcessName("Processo 2024");
        dto.setProcessStageId(3);
        dto.setProcessStageName("Entrevista");
        dto.setCommitteeMemberId(4);
        dto.setCommitteeMemberName("Prof. Ana");
        dto.setEvaluationDate(LocalDateTime.of(2025, 6, 1, 15, 30));
        dto.setTotalStageScore(new BigDecimal("95.00"));
        dto.setIsEliminatedInStage(true);
        dto.setObservations("Excelente desempenho");

        assertEquals(123, dto.getId());
        assertEquals(1, dto.getApplicationId());
        assertEquals("Alice", dto.getApplicationCandidateName());
        assertEquals(2, dto.getApplicationSelectionProcessId());
        assertEquals("Processo 2024", dto.getApplicationSelectionProcessName());
        assertEquals(3, dto.getProcessStageId());
        assertEquals("Entrevista", dto.getProcessStageName());
        assertEquals(4, dto.getCommitteeMemberId());
        assertEquals("Prof. Ana", dto.getCommitteeMemberName());
        assertEquals(LocalDateTime.of(2025, 6, 1, 15, 30), dto.getEvaluationDate());
        assertEquals(new BigDecimal("95.00"), dto.getTotalStageScore());
        assertTrue(dto.getIsEliminatedInStage());
        assertEquals("Excelente desempenho", dto.getObservations());
    }
}
