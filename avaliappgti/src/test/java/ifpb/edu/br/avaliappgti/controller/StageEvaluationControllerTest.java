package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.dto.StageEvaluationCreateDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationUpdateTotalScoreDTO;
import ifpb.edu.br.avaliappgti.service.StageEvaluationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StageEvaluationControllerTest {

    @Mock
    private StageEvaluationService stageEvaluationService;

    @InjectMocks
    private StageEvaluationController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Helper to create a dummy response DTO
    private StageEvaluationResponseDTO dummyResponseDTO() {
        StageEvaluationResponseDTO dto = new StageEvaluationResponseDTO();
        dto.setId(1);
        dto.setApplicationId(2);
        return dto;
    }

    private StageEvaluationCreateDTO dummyCreateDTO() {
        return new StageEvaluationCreateDTO(1, 1, 1, null);
    }

    private StageEvaluationUpdateTotalScoreDTO dummyUpdateDTO() {
        StageEvaluationUpdateTotalScoreDTO dto = new StageEvaluationUpdateTotalScoreDTO();
        dto.setTotalStageScore(new java.math.BigDecimal("75"));
        return dto;
    }

    @Test
    void createStageEvaluation_Success() {
        StageEvaluationCreateDTO createDTO = dummyCreateDTO();
        StageEvaluationResponseDTO responseDTO = dummyResponseDTO();

        when(stageEvaluationService.createStageEvaluation(createDTO)).thenReturn(responseDTO);

        ResponseEntity<StageEvaluationResponseDTO> response = controller.createStageEvaluation(createDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(stageEvaluationService).createStageEvaluation(createDTO);
    }

    @Test
    void createStageEvaluation_NotFound() {
        StageEvaluationCreateDTO createDTO = dummyCreateDTO();

        when(stageEvaluationService.createStageEvaluation(createDTO)).thenThrow(new NoSuchElementException("Not found"));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.createStageEvaluation(createDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(stageEvaluationService).createStageEvaluation(createDTO);
    }

    @Test
    void createStageEvaluation_InternalServerError() {
        StageEvaluationCreateDTO createDTO = dummyCreateDTO();

        when(stageEvaluationService.createStageEvaluation(createDTO)).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.createStageEvaluation(createDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(stageEvaluationService).createStageEvaluation(createDTO);
    }

    @Test
    void getStageEvaluationById_Found() {
        int id = 1;
        StageEvaluationResponseDTO responseDTO = dummyResponseDTO();

        when(stageEvaluationService.getStageEvaluationById(id)).thenReturn(Optional.of(responseDTO));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.getStageEvaluationById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(stageEvaluationService).getStageEvaluationById(id);
    }

    @Test
    void getStageEvaluationById_NotFound() {
        int id = 1;

        when(stageEvaluationService.getStageEvaluationById(id)).thenReturn(Optional.empty());

        ResponseEntity<StageEvaluationResponseDTO> response = controller.getStageEvaluationById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(stageEvaluationService).getStageEvaluationById(id);
    }

    @Test
    void updateStageTotalScore_Success() {
        int id = 1;
        StageEvaluationUpdateTotalScoreDTO updateDTO = dummyUpdateDTO();
        StageEvaluationResponseDTO responseDTO = dummyResponseDTO();

        when(stageEvaluationService.updateStageTotalScore(id, updateDTO)).thenReturn(responseDTO);

        ResponseEntity<StageEvaluationResponseDTO> response = controller.updateStageTotalScore(id, updateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(stageEvaluationService).updateStageTotalScore(id, updateDTO);
    }

    @Test
    void updateStageTotalScore_NotFound() {
        int id = 1;
        StageEvaluationUpdateTotalScoreDTO updateDTO = dummyUpdateDTO();

        when(stageEvaluationService.updateStageTotalScore(id, updateDTO)).thenThrow(new NoSuchElementException("Not found"));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.updateStageTotalScore(id, updateDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(stageEvaluationService).updateStageTotalScore(id, updateDTO);
    }

    @Test
    void updateStageTotalScore_Conflict() {
        int id = 1;
        StageEvaluationUpdateTotalScoreDTO updateDTO = dummyUpdateDTO();

        when(stageEvaluationService.updateStageTotalScore(id, updateDTO)).thenThrow(new IllegalStateException("Conflict"));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.updateStageTotalScore(id, updateDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
        verify(stageEvaluationService).updateStageTotalScore(id, updateDTO);
    }

    @Test
    void updateStageTotalScore_InternalServerError() {
        int id = 1;
        StageEvaluationUpdateTotalScoreDTO updateDTO = dummyUpdateDTO();

        when(stageEvaluationService.updateStageTotalScore(id, updateDTO)).thenThrow(new RuntimeException("Unexpected"));

        ResponseEntity<StageEvaluationResponseDTO> response = controller.updateStageTotalScore(id, updateDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(stageEvaluationService).updateStageTotalScore(id, updateDTO);
    }
}
