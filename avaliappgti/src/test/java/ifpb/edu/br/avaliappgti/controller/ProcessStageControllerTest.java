package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.service.CustomUserDetailsService;
import ifpb.edu.br.avaliappgti.service.ProcessStageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@WebMvcTest(ProcessStageController.class)
@WithMockUser(roles = "COMMITTEE")
class ProcessStageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcessStageService processStageService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetProcessStageById_whenFound() throws Exception {
        ProcessStage stage = new ProcessStage();
        stage.setId(1);
        stage.setStageName("Stage 1");

        when(processStageService.getProcessStageById(1)).thenReturn(Optional.of(stage));

        mockMvc.perform(get("/api/process-stages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stageName").value("Stage 1"));
    }

    @Test
    void testGetProcessStageById_whenNotFound() throws Exception {
        when(processStageService.getProcessStageById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/process-stages/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetStagesBySelectionProcess_whenStagesExist() throws Exception {
        ProcessStage stage = new ProcessStage();
        stage.setId(1);
        stage.setStageName("Stage 1");

        when(processStageService.getStagesBySelectionProcessId(1)).thenReturn(List.of(stage));

        mockMvc.perform(get("/api/process-stages/by-process/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].stageName").value("Stage 1"));
    }

    @Test
    void testGetStagesBySelectionProcess_whenEmpty() throws Exception {
        when(processStageService.getStagesBySelectionProcessId(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/process-stages/by-process/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetStagesBySelectionProcess_whenException() throws Exception {
        when(processStageService.getStagesBySelectionProcessId(1)).thenThrow(new NoSuchElementException("Not found"));

        mockMvc.perform(get("/api/process-stages/by-process/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProcessStage() throws Exception {
        ProcessStage stage = new ProcessStage();
        stage.setStageName("New Stage");

        ProcessStage saved = new ProcessStage();
        saved.setId(1);
        saved.setStageName("New Stage");

        when(processStageService.saveProcessStage(any(ProcessStage.class))).thenReturn(saved);

        mockMvc.perform(post("/api/process-stages")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stage)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stageName").value("New Stage"));
    }

    @Test
    void testUpdateProcessStage_whenExists() throws Exception {
        ProcessStage stage = new ProcessStage();
        stage.setStageName("Updated Stage");

        ProcessStage existing = new ProcessStage();
        existing.setId(1);
        existing.setStageName("Old Name");

        when(processStageService.getProcessStageById(1)).thenReturn(Optional.of(existing));
        when(processStageService.saveProcessStage(any(ProcessStage.class))).thenReturn(stage);

        mockMvc.perform(put("/api/process-stages/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stage)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateProcessStage_whenNotFound() throws Exception {
        ProcessStage stage = new ProcessStage();
        stage.setStageName("Updated Stage");

        when(processStageService.getProcessStageById(1)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/process-stages/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stage)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProcessStage_whenExists() throws Exception {
        ProcessStage stage = new ProcessStage();
        stage.setId(1);

        when(processStageService.getProcessStageById(1)).thenReturn(Optional.of(stage));
        Mockito.doNothing().when(processStageService).deleteProcessStage(1);

        mockMvc.perform(delete("/api/process-stages/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProcessStage_whenNotFound() throws Exception {
        when(processStageService.getProcessStageById(1)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/process-stages/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }
}
