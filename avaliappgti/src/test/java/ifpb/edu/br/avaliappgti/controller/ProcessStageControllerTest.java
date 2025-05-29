package ifpb.edu.br.avaliappgti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.service.ProcessStageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProcessStageController.class)
class ProcessStageControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ProcessStageService processStageService;

    private ProcessStage stage;

    @BeforeEach               
    void setup() {
        stage = new ProcessStage();
        stage.setId(1);
        stage.setStageName("Entrevista");
    }

    @Test
    void testGetProcessStageById_found() throws Exception {
        given(processStageService.getProcessStageById(1)).willReturn(Optional.of(stage));

        mockMvc.perform(get("/api/process-stages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stageName").value("Entrevista"));
    }

    @Test
    void testGetStagesBySelectionProcess_success() throws Exception {
        given(processStageService.getStagesBySelectionProcessId(1)).willReturn(List.of(stage));

        mockMvc.perform(get("/api/process-stages/by-process/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetStagesBySelectionProcess_empty() throws Exception {
        given(processStageService.getStagesBySelectionProcessId(1)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/process-stages/by-process/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetStagesBySelectionProcess_notFoundException() throws Exception {
        given(processStageService.getStagesBySelectionProcessId(1)).willThrow(new NoSuchElementException());

        mockMvc.perform(get("/api/process-stages/by-process/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProcessStage_success() throws Exception {
        given(processStageService.saveProcessStage(any(ProcessStage.class))).willReturn(stage);

        mockMvc.perform(post("/api/process-stages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stage)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateProcessStage_success() throws Exception {
        given(processStageService.getProcessStageById(1)).willReturn(Optional.of(stage));
        given(processStageService.saveProcessStage(any(ProcessStage.class))).willReturn(stage);

        mockMvc.perform(put("/api/process-stages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stage)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateProcessStage_notFound() throws Exception {
        given(processStageService.getProcessStageById(1)).willReturn(Optional.empty());

        mockMvc.perform(put("/api/process-stages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stage)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProcessStage_success() throws Exception {
        given(processStageService.getProcessStageById(1)).willReturn(Optional.of(stage));
        willDoNothing().given(processStageService).deleteProcessStage(1);

        mockMvc.perform(delete("/api/process-stages/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProcessStage_notFound() throws Exception {
        given(processStageService.getProcessStageById(1)).willReturn(Optional.empty());

        mockMvc.perform(delete("/api/process-stages/1"))
                .andExpect(status().isNotFound());
    }
}
