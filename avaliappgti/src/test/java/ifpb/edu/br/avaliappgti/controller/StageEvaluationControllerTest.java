package ifpb.edu.br.avaliappgti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationCreateDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationUpdateObservationsDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationUpdateTotalScoreDTO;
import ifpb.edu.br.avaliappgti.service.AuthService;
import ifpb.edu.br.avaliappgti.service.CustomUserDetailsService;
import ifpb.edu.br.avaliappgti.service.StageEvaluationService;
import ifpb.edu.br.avaliappgti.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StageEvaluationController.class)
@WithMockUser(authorities = "ROLE_COMMITTEE")
class StageEvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StageEvaluationService stageEvaluationService;

    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private AuthService authService;

    @Test
    void testCreateStageEvaluation_success() throws Exception {
        StageEvaluationCreateDTO dto = new StageEvaluationCreateDTO();
        // Set required fields to avoid validation errors
        dto.setApplicationId(1);
        dto.setProcessStageId(2);
        dto.setCommitteeMemberId(3);
        
        StageEvaluationResponseDTO responseDTO = new StageEvaluationResponseDTO();
        responseDTO.setId(1);

        when(stageEvaluationService.createStageEvaluation(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/stage-evaluations")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetStageEvaluationById_found() throws Exception {
        Integer id = 1;
        StageEvaluationResponseDTO dto = new StageEvaluationResponseDTO();
        dto.setId(id);
        
        when(stageEvaluationService.getStageEvaluationById(id)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/stage-evaluations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void testGetStageEvaluationById_notFound() throws Exception {
        when(stageEvaluationService.getStageEvaluationById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/stage-evaluations/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateStageTotalScore_success() throws Exception {
        StageEvaluationUpdateTotalScoreDTO dto = new StageEvaluationUpdateTotalScoreDTO();
        dto.setTotalStageScore(new BigDecimal("95.5"));
        StageEvaluationResponseDTO responseDTO = new StageEvaluationResponseDTO();
        responseDTO.setId(1);
        
        when(stageEvaluationService.updateStageTotalScore(eq(1), any(StageEvaluationUpdateTotalScoreDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/stage-evaluations/{id}/total-score", 1)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testFindStageEvaluationByDetails_found() throws Exception {
        StageEvaluationResponseDTO dto = new StageEvaluationResponseDTO();
        dto.setId(1);
        
        when(stageEvaluationService.findStageEvaluationByDetails(3, 2, 1)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/stage-evaluations/find")
                .param("applicationId", "3")
                .param("processStageId", "2") 
                .param("committeeMemberId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testFindStageEvaluationByDetails_notFound() throws Exception {
        when(stageEvaluationService.findStageEvaluationByDetails(3, 2, 1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/stage-evaluations/find")
                .param("applicationId", "3")
                .param("processStageId", "2")
                .param("committeeMemberId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No object found."));
    }

    @Test
    void testCalculateTotalScore_success() throws Exception {
        StageEvaluationResponseDTO dto = new StageEvaluationResponseDTO();
        dto.setId(1);
        
        when(stageEvaluationService.calculateAndSaveTotalScore(1)).thenReturn(dto);

        mockMvc.perform(post("/api/stage-evaluations/{id}/calculate-total-score", 1)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCalculateTotalScore_notFound() throws Exception {
        when(stageEvaluationService.calculateAndSaveTotalScore(1)).thenThrow(new java.util.NoSuchElementException());

        mockMvc.perform(post("/api/stage-evaluations/{id}/calculate-total-score", 1)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCalculateTotalScore_conflict() throws Exception {
        when(stageEvaluationService.calculateAndSaveTotalScore(1)).thenThrow(new IllegalStateException());

        mockMvc.perform(post("/api/stage-evaluations/{id}/calculate-total-score", 1)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    void whenUpdateObservations_thenReturns200() throws Exception {
        Integer stageEvaluationId = 1;
        String newObservations = "This is a test observation.";

        StageEvaluationUpdateObservationsDTO updateDTO = new StageEvaluationUpdateObservationsDTO();
        updateDTO.setObservations(newObservations);

        StageEvaluationResponseDTO responseDTO = new StageEvaluationResponseDTO();
        responseDTO.setId(stageEvaluationId);
        responseDTO.setObservations(newObservations);

        when(stageEvaluationService.updateObservations(eq(stageEvaluationId), any(StageEvaluationUpdateObservationsDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(patch("/api/stage-evaluations/{id}/observations", stageEvaluationId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(stageEvaluationId))
                .andExpect(jsonPath("$.observations").value(newObservations));
    }
}
