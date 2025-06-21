package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.dto.StageEvaluationCreateDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationUpdateTotalScoreDTO;
import ifpb.edu.br.avaliappgti.service.StageEvaluationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StageEvaluationControllerTest {

    private StageEvaluationService stageEvaluationService;
    private StageEvaluationController controller;

    @BeforeEach
    void setUp() {
        stageEvaluationService = mock(StageEvaluationService.class);
        controller = new StageEvaluationController(stageEvaluationService);
    }

    @Test
    void testCreateStageEvaluation_success() {
        StageEvaluationCreateDTO dto = new StageEvaluationCreateDTO();
        StageEvaluationResponseDTO responseDTO = new StageEvaluationResponseDTO();
        when(stageEvaluationService.createStageEvaluation(dto)).thenReturn(responseDTO);

        ResponseEntity<StageEvaluationResponseDTO> response = controller.createStageEvaluation(dto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testGetStageEvaluationById_found() {
        Integer id = 1;
        StageEvaluationResponseDTO dto = new StageEvaluationResponseDTO();
        when(stageEvaluationService.getStageEvaluationById(id)).thenReturn(Optional.of(dto));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.getStageEvaluationById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetStageEvaluationById_notFound() {
        when(stageEvaluationService.getStageEvaluationById(1)).thenReturn(Optional.empty());

        ResponseEntity<StageEvaluationResponseDTO> response = controller.getStageEvaluationById(1);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testUpdateStageTotalScore_success() {
        StageEvaluationUpdateTotalScoreDTO dto = new StageEvaluationUpdateTotalScoreDTO();
        StageEvaluationResponseDTO responseDTO = new StageEvaluationResponseDTO();
        when(stageEvaluationService.updateStageTotalScore(1, dto)).thenReturn(responseDTO);

        ResponseEntity<StageEvaluationResponseDTO> response = controller.updateStageTotalScore(1, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testFindStageEvaluationByDetails_found() {
        StageEvaluationResponseDTO dto = new StageEvaluationResponseDTO();
        when(stageEvaluationService.findStageEvaluationByDetails(1, 2, 3)).thenReturn(Optional.of(dto));

        ResponseEntity<?> response = controller.findStageEvaluationByDetails(1, 2, 3);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testFindStageEvaluationByDetails_notFound() {
        when(stageEvaluationService.findStageEvaluationByDetails(1, 2, 3)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.findStageEvaluationByDetails(1, 2, 3);

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        assertEquals("No object found.", ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void testCalculateTotalScore_success() {
        StageEvaluationResponseDTO dto = new StageEvaluationResponseDTO();
        when(stageEvaluationService.calculateAndSaveTotalScore(1)).thenReturn(dto);

        ResponseEntity<StageEvaluationResponseDTO> response = controller.calculateTotalScore(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testCalculateTotalScore_notFound() {
        when(stageEvaluationService.calculateAndSaveTotalScore(1)).thenThrow(new java.util.NoSuchElementException());

        ResponseEntity<StageEvaluationResponseDTO> response = controller.calculateTotalScore(1);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testCalculateTotalScore_conflict() {
        when(stageEvaluationService.calculateAndSaveTotalScore(1)).thenThrow(new IllegalStateException());

        ResponseEntity<StageEvaluationResponseDTO> response = controller.calculateTotalScore(1);

        assertEquals(409, response.getStatusCodeValue());
    }
}
