package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.dto.*;
import ifpb.edu.br.avaliappgti.service.CriterionScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CriterionScoreControllerTest {

    private CriterionScoreService criterionScoreService;
    private CriterionScoreController controller;

    @BeforeEach
    void setUp() {
        criterionScoreService = mock(CriterionScoreService.class);
        controller = new CriterionScoreController(criterionScoreService);
    }

    // === Test: GET /by-stage-evaluation/{id} ===
    @Test
    void getScoresByStageEvaluation_shouldReturnScores() {
        Integer stageEvaluationId = 1;
        List<CriterionScoreResponseDTO> mockScores = List.of(new CriterionScoreResponseDTO());

        when(criterionScoreService.getScoresByStageEvaluation(stageEvaluationId)).thenReturn(mockScores);

        ResponseEntity<List<CriterionScoreResponseDTO>> response = controller.getScoresByStageEvaluation(stageEvaluationId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockScores, response.getBody());
    }

    @Test
    void getScoresByStageEvaluation_shouldReturnNoContent() {
        when(criterionScoreService.getScoresByStageEvaluation(1)).thenReturn(Collections.emptyList());

        ResponseEntity<List<CriterionScoreResponseDTO>> response = controller.getScoresByStageEvaluation(1);

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getScoresByStageEvaluation_shouldReturnNotFound() {
        when(criterionScoreService.getScoresByStageEvaluation(1)).thenThrow(new NoSuchElementException());

        ResponseEntity<List<CriterionScoreResponseDTO>> response = controller.getScoresByStageEvaluation(1);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void getScoresByStageEvaluation_shouldReturnInternalServerError() {
        when(criterionScoreService.getScoresByStageEvaluation(1)).thenThrow(new RuntimeException("DB failure"));

        ResponseEntity<List<CriterionScoreResponseDTO>> response = controller.getScoresByStageEvaluation(1);

        assertEquals(500, response.getStatusCodeValue());
    }

    // === Test: POST /evaluate/{stageEvaluationId} ===
    @Test
    void saveCriteriaScoresForStageEvaluation_shouldReturnOk() {
        Integer stageEvaluationId = 1;
        SaveCriterionScoresRequest request = new SaveCriterionScoresRequest();
        StageEvaluationResponseDTO responseDTO = new StageEvaluationResponseDTO();

        when(criterionScoreService.saveCriteriaScoresForStageEvaluation(stageEvaluationId, request)).thenReturn(responseDTO);

        ResponseEntity<StageEvaluationResponseDTO> response = controller.saveCriteriaScoresForStageEvaluation(stageEvaluationId, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void saveCriteriaScoresForStageEvaluation_shouldReturnNotFound() {
        when(criterionScoreService.saveCriteriaScoresForStageEvaluation(eq(1), any()))
                .thenThrow(new NoSuchElementException());

        ResponseEntity<StageEvaluationResponseDTO> response = controller.saveCriteriaScoresForStageEvaluation(1, new SaveCriterionScoresRequest());

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void saveCriteriaScoresForStageEvaluation_shouldReturnBadRequest() {
        when(criterionScoreService.saveCriteriaScoresForStageEvaluation(eq(1), any()))
                .thenThrow(new IllegalArgumentException("Invalid score"));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.saveCriteriaScoresForStageEvaluation(1, new SaveCriterionScoresRequest());

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void saveCriteriaScoresForStageEvaluation_shouldReturnConflict() {
        when(criterionScoreService.saveCriteriaScoresForStageEvaluation(eq(1), any()))
                .thenThrow(new IllegalStateException("Stage mismatch"));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.saveCriteriaScoresForStageEvaluation(1, new SaveCriterionScoresRequest());

        assertEquals(409, response.getStatusCodeValue());
    }

    @Test
    void saveCriteriaScoresForStageEvaluation_shouldReturnInternalServerError() {
        when(criterionScoreService.saveCriteriaScoresForStageEvaluation(eq(1), any()))
                .thenThrow(new RuntimeException("Unexpected failure"));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.saveCriteriaScoresForStageEvaluation(1, new SaveCriterionScoresRequest());

        assertEquals(500, response.getStatusCodeValue());
    }

    // === Test: PUT /{id} ===
    @Test
    void updateCriterionScore_shouldReturnOk() {
        UpdateCriterionScoreDTO updateDTO = new UpdateCriterionScoreDTO();
        StageEvaluationResponseDTO updated = new StageEvaluationResponseDTO();

        when(criterionScoreService.updateCriterionScore(1, updateDTO)).thenReturn(updated);

        ResponseEntity<StageEvaluationResponseDTO> response = controller.updateCriterionScore(1, updateDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updated, response.getBody());
    }

    @Test
    void updateCriterionScore_shouldReturnNotFound() {
        when(criterionScoreService.updateCriterionScore(eq(1), any()))
                .thenThrow(new NoSuchElementException());

        ResponseEntity<StageEvaluationResponseDTO> response = controller.updateCriterionScore(1, new UpdateCriterionScoreDTO());

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void updateCriterionScore_shouldReturnInternalServerError() {
        when(criterionScoreService.updateCriterionScore(eq(1), any()))
                .thenThrow(new RuntimeException("Update error"));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.updateCriterionScore(1, new UpdateCriterionScoreDTO());

        assertEquals(500, response.getStatusCodeValue());
    }
}
