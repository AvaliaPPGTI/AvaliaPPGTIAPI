package ifpb.edu.br.avaliappgti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifpb.edu.br.avaliappgti.dto.CreateSubCriterionRequestDTO;
import ifpb.edu.br.avaliappgti.dto.CreateTopLevelCriterionRequestDTO;
import ifpb.edu.br.avaliappgti.dto.EvaluationCriterionResponseDTO;
import ifpb.edu.br.avaliappgti.dto.UpdateEvaluationCriterionRequestDTO;
import ifpb.edu.br.avaliappgti.model.*;
import ifpb.edu.br.avaliappgti.repository.*;

import ifpb.edu.br.avaliappgti.service.EvaluationCriterionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EvaluationCriterionController.class)
class EvaluationCriterionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvaluationCriterionService evaluationCriterionService;

    @Autowired
    private ObjectMapper objectMapper;

    // Dummy objects used across tests
    private EvaluationCriterion criterion;
    private EvaluationCriterionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        criterion = new EvaluationCriterion();
        criterion.setId(1);
        criterion.setCriterionDescription("Critério Teste");
        responseDTO = new EvaluationCriterionResponseDTO(criterion);
    }

    @Test
    void createTopLevelCriterion_shouldReturnCreated() throws Exception {
        CreateTopLevelCriterionRequestDTO dto = new CreateTopLevelCriterionRequestDTO();
        dto.setDescription("Descrição");
        dto.setProcessStageId(1);
        dto.setMaximumScore(new BigDecimal("10.0"));

        when(evaluationCriterionService.createTopLevelCriterion(anyInt(), anyString(), any(), any()))
                .thenReturn(criterion);

        mockMvc.perform(post("/api/evaluation-criteria/top-level")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createSubCriterion_shouldReturnCreated() throws Exception {
        CreateSubCriterionRequestDTO dto = new CreateSubCriterionRequestDTO();
        dto.setDescription("SubCritério");
        dto.setMaximumScore(new BigDecimal("5.0"));
        dto.setWeight(new BigDecimal("0.5"));

        when(evaluationCriterionService.createSubCriterion(any(), any(), any(), eq(1)))
                .thenReturn(criterion);

        mockMvc.perform(post("/api/evaluation-criteria/1/sub-criterion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getEvaluationCriteriaTreeByProcessStage_shouldReturnOk() throws Exception {
        when(evaluationCriterionService.getTopLevelCriteriaByProcessStage(1))
                .thenReturn(List.of(criterion));

        mockMvc.perform(get("/api/evaluation-criteria/by-process-stage/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getEvaluationCriterionById_shouldReturnOk() throws Exception {
        when(evaluationCriterionService.getEvaluationCriterionById(1)).thenReturn(criterion);

        mockMvc.perform(get("/api/evaluation-criteria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateEvaluationCriterion_shouldReturnOk() throws Exception {
        UpdateEvaluationCriterionRequestDTO dto = new UpdateEvaluationCriterionRequestDTO();
        dto.setDescription("Atualizado");

        when(evaluationCriterionService.updateEvaluationCriterion(eq(1), any()))
                .thenReturn(criterion);

        mockMvc.perform(put("/api/evaluation-criteria/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void patchEvaluationCriterion_shouldReturnOk() throws Exception {
        UpdateEvaluationCriterionRequestDTO dto = new UpdateEvaluationCriterionRequestDTO();
        dto.setMaximumScore(new BigDecimal("7.5"));

        when(evaluationCriterionService.updateEvaluationCriterion(eq(1), any()))
                .thenReturn(criterion);

        mockMvc.perform(patch("/api/evaluation-criteria/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getEvaluationCriterionById_shouldReturnNotFound() throws Exception {
        when(evaluationCriterionService.getEvaluationCriterionById(1)).thenThrow(new NoSuchElementException());

        mockMvc.perform(get("/api/evaluation-criteria/1"))
                .andExpect(status().isNotFound());
    }
}
