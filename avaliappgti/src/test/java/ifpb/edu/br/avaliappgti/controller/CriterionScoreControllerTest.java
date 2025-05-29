package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.dto.SaveCriterionScoresRequest;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO;
import ifpb.edu.br.avaliappgti.model.CriterionScore;
import ifpb.edu.br.avaliappgti.service.CriterionScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CriterionScoreControllerTest {

    @Mock
    private CriterionScoreService criterionScoreService;

    @InjectMocks
    private CriterionScoreController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // Helper dummy StageEvaluationResponseDTO
    private StageEvaluationResponseDTO dummyStageEvaluationResponseDTO() {
        StageEvaluationResponseDTO dto = new StageEvaluationResponseDTO();
        dto.setId(1);
        dto.setApplicationId(10);
        return dto;
    }

    // Helper dummy SaveCriterionScoresRequest
    private SaveCriterionScoresRequest dummySaveCriterionScoresRequest() {
        return new SaveCriterionScoresRequest(Collections.emptyList());
    }

    // Helper dummy CriterionScore list
    private List<CriterionScore> dummyCriterionScores() {
        CriterionScore score1 = new CriterionScore();
        score1.setId(1);
        score1.setScoreObtained(new BigDecimal("80"));

        CriterionScore score2 = new CriterionScore();
        score2.setId(2);
        score2.setScoreObtained(new BigDecimal("90"));

        return Arrays.asList(score1, score2);
    }

    // Tests for POST /evaluate/{stageEvaluationId}

    @Test
    void evaluateStage_Success() {
        int stageEvaluationId = 1;
        SaveCriterionScoresRequest request = dummySaveCriterionScoresRequest();
        StageEvaluationResponseDTO responseDTO = dummyStageEvaluationResponseDTO();

        when(criterionScoreService.saveCriteriaScoresForStageEvaluation(stageEvaluationId, request))
                .thenReturn(responseDTO);

        ResponseEntity<StageEvaluationResponseDTO> response = controller.evaluateStage(stageEvaluationId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(criterionScoreService).saveCriteriaScoresForStageEvaluation(stageEvaluationId, request);
    }

    @Test
    void evaluateStage_NotFound() {
        int stageEvaluationId = 1;
        SaveCriterionScoresRequest request = dummySaveCriterionScoresRequest();

        when(criterionScoreService.saveCriteriaScoresForStageEvaluation(stageEvaluationId, request))
                .thenThrow(new NoSuchElementException("Stage evaluation not found"));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.evaluateStage(stageEvaluationId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(criterionScoreService).saveCriteriaScoresForStageEvaluation(stageEvaluationId, request);
    }

    @Test
    void evaluateStage_BadRequest() {
        int stageEvaluationId = 1;
        SaveCriterionScoresRequest request = dummySaveCriterionScoresRequest();

        when(criterionScoreService.saveCriteriaScoresForStageEvaluation(stageEvaluationId, request))
                .thenThrow(new IllegalArgumentException("Invalid input"));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.evaluateStage(stageEvaluationId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(criterionScoreService).saveCriteriaScoresForStageEvaluation(stageEvaluationId, request);
    }

    @Test
    void evaluateStage_Conflict() {
        int stageEvaluationId = 1;
        SaveCriterionScoresRequest request = dummySaveCriterionScoresRequest();

        when(criterionScoreService.saveCriteriaScoresForStageEvaluation(stageEvaluationId, request))
                .thenThrow(new IllegalStateException("Conflict in saving scores"));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.evaluateStage(stageEvaluationId, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
        verify(criterionScoreService).saveCriteriaScoresForStageEvaluation(stageEvaluationId, request);
    }

    @Test
    void evaluateStage_InternalServerError() {
        int stageEvaluationId = 1;
        SaveCriterionScoresRequest request = dummySaveCriterionScoresRequest();

        when(criterionScoreService.saveCriteriaScoresForStageEvaluation(stageEvaluationId, request))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.evaluateStage(stageEvaluationId, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(criterionScoreService).saveCriteriaScoresForStageEvaluation(stageEvaluationId, request);
    }

    // Tests for GET /by-stage-evaluation/{stageEvaluationId}

    @Test
    void getScoresByStageEvaluation_Success() {
        int stageEvaluationId = 1;
        List<CriterionScore> scores = dummyCriterionScores();

        when(criterionScoreService.getScoresByStageEvaluation(stageEvaluationId))
                .thenReturn(scores);

        ResponseEntity<List<CriterionScore>> response = controller.getScoresByStageEvaluation(stageEvaluationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(scores, response.getBody());
        verify(criterionScoreService).getScoresByStageEvaluation(stageEvaluationId);
    }

    @Test
    void getScoresByStageEvaluation_NoContent() {
        int stageEvaluationId = 1;

        when(criterionScoreService.getScoresByStageEvaluation(stageEvaluationId))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<CriterionScore>> response = controller.getScoresByStageEvaluation(stageEvaluationId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(criterionScoreService).getScoresByStageEvaluation(stageEvaluationId);
    }

    @Test
    void getScoresByStageEvaluation_NotFound() {
        int stageEvaluationId = 1;

        when(criterionScoreService.getScoresByStageEvaluation(stageEvaluationId))
                .thenThrow(new NoSuchElementException("Stage evaluation not found"));

        ResponseEntity<List<CriterionScore>> response = controller.getScoresByStageEvaluation(stageEvaluationId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(criterionScoreService).getScoresByStageEvaluation(stageEvaluationId);
    }

    @Test
    void getScoresByStageEvaluation_InternalServerError() {
        int stageEvaluationId = 1;

        when(criterionScoreService.getScoresByStageEvaluation(stageEvaluationId))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<List<CriterionScore>> response = controller.getScoresByStageEvaluation(stageEvaluationId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(criterionScoreService).getScoresByStageEvaluation(stageEvaluationId);
    }
}
