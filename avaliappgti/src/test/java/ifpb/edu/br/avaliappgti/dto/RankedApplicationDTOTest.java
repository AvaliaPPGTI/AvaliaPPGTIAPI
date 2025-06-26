package ifpb.edu.br.avaliappgti.dto;

import ifpb.edu.br.avaliappgti.dto.RankedApplicationDTO;
import ifpb.edu.br.avaliappgti.model.*;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RankedApplicationDTOTest {

    @Test
    void constructor_shouldMapAllFieldsCorrectly_whenApplicationIsFullyPopulated() {
        // Arrange
        Quota quota = new Quota();
        quota.setName("Ampla Concorrência");

        Candidate candidate = new Candidate();
        candidate.setId(100);
        candidate.setName("João da Silva");
        candidate.setQuota(quota);

        ResearchLine researchLine = new ResearchLine();
        researchLine.setId(200);
        researchLine.setName("IA e Otimização");

        ResearchTopic researchTopic = new ResearchTopic();
        researchTopic.setId(300);
        researchTopic.setName("Aprendizado de Máquina");
        researchTopic.setResearchLine(researchLine);

        Application application = new Application();
        application.setId(1);
        application.setCandidate(candidate);
        application.setResearchLine(researchLine);
        application.setResearchTopic(researchTopic);
        application.setFinalScore(BigDecimal.valueOf(87.5));
        application.setRankingByTopic(2);
        application.setIsApproved(true);
        application.setApplicationStatus("HOMOLOGADA");

        // Act
        RankedApplicationDTO dto = new RankedApplicationDTO(application);

        // Assert
        assertEquals(1, dto.getApplicationId());
        assertEquals(100, dto.getCandidateId());
        assertEquals("João da Silva", dto.getCandidateName());
        assertEquals("Ampla Concorrência", dto.getQuotaName());
        assertEquals(200, dto.getResearchLineId());
        assertEquals("IA e Otimização", dto.getResearchLineName());
        assertEquals(300, dto.getResearchTopicId());
        assertEquals("Aprendizado de Máquina", dto.getResearchTopicName());
        assertEquals(BigDecimal.valueOf(87.5), dto.getFinalScore());
        assertEquals(2, dto.getRankingByTopic());
        assertTrue(dto.isApproved());
        assertEquals("HOMOLOGADA", dto.getApplicationStatus());
    }

    @Test
    void constructor_shouldHandleNullNestedObjectsSafely() {
        // Arrange
        Application application = new Application();
        application.setId(2);
        application.setCandidate(null); // no candidate
        application.setResearchLine(null); // no research line
        application.setResearchTopic(null); // no research topic
        application.setFinalScore(null);
        application.setRankingByTopic(null);
        application.setIsApproved(false);
        application.setApplicationStatus(null);

        // Act
        RankedApplicationDTO dto = new RankedApplicationDTO(application);

        // Assert
        assertEquals(2, dto.getApplicationId());
        assertNull(dto.getCandidateId());
        assertNull(dto.getCandidateName());
        assertNull(dto.getQuotaName());
        assertNull(dto.getResearchLineId());
        assertNull(dto.getResearchLineName());
        assertNull(dto.getResearchTopicId());
        assertNull(dto.getResearchTopicName());
        assertNull(dto.getFinalScore());
        assertNull(dto.getRankingByTopic());
        assertFalse(dto.isApproved());
        assertNull(dto.getApplicationStatus());
    }
}
