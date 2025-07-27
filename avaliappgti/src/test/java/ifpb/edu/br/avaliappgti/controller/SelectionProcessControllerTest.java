package ifpb.edu.br.avaliappgti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifpb.edu.br.avaliappgti.config.SecurityConfig;
import ifpb.edu.br.avaliappgti.dto.StageWeightDTO;
import ifpb.edu.br.avaliappgti.dto.UpdateStageWeightDTO;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.service.SelectionProcessService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SelectionProcessController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "COMMITTEE")
class SelectionProcessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SelectionProcessService selectionProcessService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetCurrentSelectionProcess_found() throws Exception {
        SelectionProcess process = new SelectionProcess();
        process.setId(1);
        process.setName("Test Process");
        when(selectionProcessService.getCurrentSelectionProcess()).thenReturn(Optional.of(process));

        mockMvc.perform(get("/api/selection-processes/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Process"));
    }

    @Test
    void testGetCurrentSelectionProcess_notFound() throws Exception {
        when(selectionProcessService.getCurrentSelectionProcess()).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/selection-processes/current"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testGetCurrentProcessStageWeights_found() throws Exception {
        ProcessStage stage1 = new ProcessStage();
        stage1.setId(10);
        stage1.setStageWeight(new BigDecimal("0.4"));
        ProcessStage stage2 = new ProcessStage();
        stage2.setId(20);
        stage2.setStageWeight(new BigDecimal("0.6"));
        List<StageWeightDTO> weights = Arrays.asList(new StageWeightDTO(stage1), new StageWeightDTO(stage2));

        when(selectionProcessService.getCurrentProcessStageWeights()).thenReturn(weights);

        mockMvc.perform(get("/api/selection-processes/current/weights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stageId").value(10))
                .andExpect(jsonPath("$[0].stageWeight").value(0.4))
                .andExpect(jsonPath("$[1].stageId").value(20))
                .andExpect(jsonPath("$[1].stageWeight").value(0.6));
    }

    @Test
    void testGetCurrentProcessStageWeights_empty() throws Exception {
        when(selectionProcessService.getCurrentProcessStageWeights()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/selection-processes/current/weights"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateCurrentProcessStageWeights_success() throws Exception {
        UpdateStageWeightDTO dto1 = new UpdateStageWeightDTO();
        dto1.setStageId(10);
        dto1.setStageWeight(new BigDecimal("0.5"));
        UpdateStageWeightDTO dto2 = new UpdateStageWeightDTO();
        dto2.setStageId(20);
        dto2.setStageWeight(new BigDecimal("0.5"));
        List<UpdateStageWeightDTO> updateList = Arrays.asList(dto1, dto2);

        mockMvc.perform(put("/api/selection-processes/current/weights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Stage weights updated successfully."));
    }

    @Test
    void testUpdateCurrentProcessStageWeights_notFound() throws Exception {
        UpdateStageWeightDTO dto = new UpdateStageWeightDTO();
        dto.setStageId(999);
        dto.setStageWeight(BigDecimal.ONE);
        List<UpdateStageWeightDTO> updateList = Collections.singletonList(dto);

        doThrow(new NoSuchElementException("No active selection process found."))
                .when(selectionProcessService).updateCurrentProcessStageWeights(any());

        mockMvc.perform(put("/api/selection-processes/current/weights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateList)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No active selection process found."));
    }

    @Test
    void testUpdateCurrentProcessStageWeights_badRequest() throws Exception {
        UpdateStageWeightDTO dto = new UpdateStageWeightDTO();
        dto.setStageId(10);
        dto.setStageWeight(new BigDecimal("0.7"));
        List<UpdateStageWeightDTO> updateList = Collections.singletonList(dto);

        doThrow(new IllegalArgumentException("The sum of all stage weights must be exactly 1."))
                .when(selectionProcessService).updateCurrentProcessStageWeights(any());

        mockMvc.perform(put("/api/selection-processes/current/weights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The sum of all stage weights must be exactly 1."));
    }

    @Test
    void testUpdateCurrentProcessStageWeights_internalError() throws Exception {
        UpdateStageWeightDTO dto = new UpdateStageWeightDTO();
        dto.setStageId(10);
        dto.setStageWeight(new BigDecimal("0.5"));
        List<UpdateStageWeightDTO> updateList = Collections.singletonList(dto);

        doThrow(new RuntimeException("Unexpected error"))
                .when(selectionProcessService).updateCurrentProcessStageWeights(any());

        mockMvc.perform(put("/api/selection-processes/current/weights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateList)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An unexpected error occurred."));
    }
}
