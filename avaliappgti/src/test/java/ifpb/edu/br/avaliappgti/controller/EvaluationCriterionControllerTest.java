package ifpb.edu.br.avaliappgti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.service.EvaluationCriterionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluationCriterionController.class)
class EvaluationCriterionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvaluationCriterionService evaluationCriterionService;

    @Autowired
    private ObjectMapper objectMapper;

    private final ProcessStage processStage = new ProcessStage();
    private final EvaluationCriterion criterion = new EvaluationCriterion(
            processStage, "Clareza", new BigDecimal("10.00"), new BigDecimal("1.0")
    );

    @Test
    void testGetEvaluationCriterionById_found() throws Exception {
        criterion.setId(1);
        given(evaluationCriterionService.getEvaluationCriterionById(1)).willReturn(Optional.of(criterion));

        mockMvc.perform(get("/api/evaluation-criteria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.criterionDescription").value("Clareza"))
                .andExpect(jsonPath("$.maximumScore").value(10.00));
    }

    @Test
    void testGetEvaluationCriterionById_notFound() throws Exception {
        given(evaluationCriterionService.getEvaluationCriterionById(99)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/evaluation-criteria/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCriteriaByProcessStageAndSelectionProcess_success() throws Exception {
        given(evaluationCriterionService.getCriteriaByProcessStageAndSelectionProcessId(1, 1))
                .willReturn(List.of(criterion));

        mockMvc.perform(get("/api/evaluation-criteria/by-process/1/stage/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].criterionDescription").value("Clareza"));
    }

    @Test
    void testGetCriteriaByProcessStageAndSelectionProcess_emptyList() throws Exception {
        given(evaluationCriterionService.getCriteriaByProcessStageAndSelectionProcessId(1, 1))
                .willReturn(List.of());

        mockMvc.perform(get("/api/evaluation-criteria/by-process/1/stage/1"))
                .andExpect(status().isNoContent());
    }

//     @Test
//     void testCreateEvaluationCriterion() throws Exception {
//         given(evaluationCriterionService.saveEvaluationCriterion(criterion)).willReturn(criterion);

//         mockMvc.perform(post("/api/evaluation-criteria")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(criterion)))
//                 .andExpect(status().isCreated())
//                 .andExpect(jsonPath("$.criterionDescription").value("Clareza"));
//     }

//     @Test
//     void testUpdateEvaluationCriterion_found() throws Exception {
//         criterion.setId(1);
//         given(evaluationCriterionService.getEvaluationCriterionById(1)).willReturn(Optional.of(criterion));
//         given(evaluationCriterionService.saveEvaluationCriterion(criterion)).willReturn(criterion);

//         mockMvc.perform(put("/api/evaluation-criteria/1")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(criterion)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.criterionDescription").value("Clareza"));
//     }

    @Test
    void testUpdateEvaluationCriterion_notFound() throws Exception {
        given(evaluationCriterionService.getEvaluationCriterionById(99)).willReturn(Optional.empty());

        mockMvc.perform(put("/api/evaluation-criteria/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criterion)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteEvaluationCriterion_found() throws Exception {
        given(evaluationCriterionService.getEvaluationCriterionById(1)).willReturn(Optional.of(criterion));
        doNothing().when(evaluationCriterionService).deleteEvaluationCriterion(1);

        mockMvc.perform(delete("/api/evaluation-criteria/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteEvaluationCriterion_notFound() throws Exception {
        given(evaluationCriterionService.getEvaluationCriterionById(99)).willReturn(Optional.empty());

        mockMvc.perform(delete("/api/evaluation-criteria/99"))
                .andExpect(status().isNotFound());
    }
}
