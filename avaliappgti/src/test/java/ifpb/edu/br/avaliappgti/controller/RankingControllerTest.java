package ifpb.edu.br.avaliappgti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifpb.edu.br.avaliappgti.dto.*;
import ifpb.edu.br.avaliappgti.model.*;
import ifpb.edu.br.avaliappgti.repository.*;
import ifpb.edu.br.avaliappgti.service.*;

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
@WebMvcTest(RankingController.class)
class RankingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RankingService rankingService;

    // ObjectMapper for JSON conversion
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getRanking_shouldReturnListOfRankedApplications() throws Exception {
        RankedApplicationDTO dto = new RankedApplicationDTO();
        dto.setApplicationId(1);
        when(rankingService.getRankingForProcess(1)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/ranking/process/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].applicationId").value(1));
    }

    @Test
    void generateRanking_shouldReturnGeneratedRanking() throws Exception {
        RankedApplicationDTO dto = new RankedApplicationDTO();
        dto.setApplicationId(2);
        when(rankingService.generateRankingForProcess(1)).thenReturn(List.of(dto));

        mockMvc.perform(post("/api/ranking/generate/process/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].applicationId").value(2));
    }

    @Test
    void getStageRanking_shouldReturnStageRanking() throws Exception {
        StageRankingDTO dto = new StageRankingDTO();
        dto.setStageEvaluationId(5);
        when(rankingService.getRankingForStage(1, 2)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/ranking/process/1/stage/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stageEvaluationId").value(5));
    }

    @Test
    void getStageRankingByResearchLine_shouldReturnFilteredRanking() throws Exception {
        StageRankingDTO dto = new StageRankingDTO();
        dto.setStageEvaluationId(6);
        when(rankingService.getRankingForStageByResearchLine(1, 2, 3)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/ranking/process/1/stage/2/line/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stageEvaluationId").value(6));
    }

    @Test
    void getStageRankingByResearchTopic_shouldReturnFilteredRanking() throws Exception {
        StageRankingDTO dto = new StageRankingDTO();
        dto.setStageEvaluationId(7);
        when(rankingService.getRankingForStageByResearchTopic(1, 2, 4)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/ranking/process/1/stage/2/topic/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stageEvaluationId").value(7));
    }

    @Test
    void getStageRankingByStatus_shouldReturnFilteredRanking() throws Exception {
        StageRankingDTO dto = new StageRankingDTO();
        dto.setStageEvaluationId(8);
        when(rankingService.getRankingForStageByStatus(1, 2, "Classificado")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/ranking/process/1/stage/2/status/Classificado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stageEvaluationId").value(8));
    }

}
