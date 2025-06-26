package ifpb.edu.br.avaliappgti.dto;

import ifpb.edu.br.avaliappgti.dto.StageRankingDTO;
import ifpb.edu.br.avaliappgti.model.*;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StageRankingDTOTest {

    @Test
    void constructor_shouldMapAllFieldsCorrectly_whenFullyPopulated() {
        // Arrange
        Quota quota = new Quota();
        quota.setId(1);
        quota.setName("Cotas Sociais");

        Candidate candidate = new Candidate();
        candidate.setId(101);
        candidate.setName("Maria Lima");
        candidate.setQuota(quota);

        ResearchLine researchLine = new ResearchLine();
        researchLine.setId(201);
        researchLine.setName("Ciência de Dados");

        ResearchTopic researchTopic = new ResearchTopic();
        researchTopic.setId(301);
        researchTopic.setName("Visão Computacional");

        Application application = new Application();
        application.setId(401);
        application.setCandidate(candidate);
        application.setResearchLine(researchLine);
        application.setResearchTopic(researchTopic);

        ProcessStage processStage = new ProcessStage();
        processStage.setId(501);
        processStage.setStageName("Entrevista");

        StageEvaluation stageEvaluation = new StageEvaluation();
        stageEvaluation.setId(601);
        stageEvaluation.setTotalStageScore(BigDecimal.valueOf(9.5));
        stageEvaluation.setIsEliminatedInStage(false);
        stageEvaluation.setApplication(application);
        stageEvaluation.setProcessStage(processStage);

        // Act
        StageRankingDTO dto = new StageRankingDTO(stageEvaluation);

        // Assert
        assertEquals(601, dto.getStageEvaluationId());
        assertEquals(BigDecimal.valueOf(9.5), dto.getTotalStageScore());
        assertFalse(dto.getIsEliminatedInStage());

        assertEquals(501, dto.getProcessStageId());
        assertEquals("Entrevista", dto.getProcessStageName());

        assertEquals(401, dto.getApplicationId());

        assertEquals(101, dto.getCandidateId());
        assertEquals("Maria Lima", dto.getCandidateName());

        assertEquals(1, dto.getQuotaId());
        assertEquals("Cotas Sociais", dto.getQuotaName());

        assertEquals(201, dto.getResearchLineId());
        assertEquals("Ciência de Dados", dto.getResearchLineName());

        assertEquals(301, dto.getResearchTopicId());
        assertEquals("Visão Computacional", dto.getResearchTopicName());
    }

    @Test
    void constructor_shouldHandleNullFieldsGracefully() {
        // Arrange: Missing candidate, quota, researchLine, researchTopic, and processStage
        Application application = new Application();
        application.setId(400); // Only application ID is set

        StageEvaluation stageEvaluation = new StageEvaluation();
        stageEvaluation.setId(600);
        stageEvaluation.setTotalStageScore(BigDecimal.ZERO);
        stageEvaluation.setIsEliminatedInStage(true);
        stageEvaluation.setApplication(application);
        stageEvaluation.setProcessStage(null); // No process stage

        // Act
        StageRankingDTO dto = new StageRankingDTO(stageEvaluation);

        // Assert
        assertEquals(600, dto.getStageEvaluationId());
        assertEquals(BigDecimal.ZERO, dto.getTotalStageScore());
        assertTrue(dto.getIsEliminatedInStage());

        assertEquals(400, dto.getApplicationId());

        assertNull(dto.getProcessStageId());
        assertNull(dto.getProcessStageName());

        assertNull(dto.getCandidateId());
        assertNull(dto.getCandidateName());

        assertNull(dto.getQuotaId());
        assertNull(dto.getQuotaName());

        assertNull(dto.getResearchLineId());
        assertNull(dto.getResearchLineName());

        assertNull(dto.getResearchTopicId());
        assertNull(dto.getResearchTopicName());
    }
}
