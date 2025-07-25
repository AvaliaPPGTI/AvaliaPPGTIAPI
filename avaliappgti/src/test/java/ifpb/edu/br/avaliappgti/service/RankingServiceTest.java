package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.RankedApplicationDTO;
import ifpb.edu.br.avaliappgti.model.*;
import ifpb.edu.br.avaliappgti.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private SelectionProcessRepository selectionProcessRepository;
    @Mock
    private StageEvaluationRepository stageEvaluationRepository;
    @Mock
    private ProcessStageRepository processStageRepository;
    @Mock
    private ResearchLineRepository researchLineRepository;
    @Mock
    private ResearchTopicRepository researchTopicRepository;

    @InjectMocks
    private RankingService rankingService;

    private SelectionProcess selectionProcess;
    private ResearchTopic researchTopic;

    @BeforeEach
    void setUp() {
        selectionProcess = new SelectionProcess();
        selectionProcess.setId(1);

        researchTopic = new ResearchTopic();
        researchTopic.setId(10);
        researchTopic.setName("Test Topic");
        researchTopic.setVacancies(2);
    }

    private StageEvaluation createStageEvaluation(int stageOrder, BigDecimal score, boolean isEliminated, BigDecimal weight) {
        ProcessStage stage = new ProcessStage();
        stage.setStageOrder(stageOrder);
        stage.setStageWeight(weight);

        StageEvaluation evaluation = new StageEvaluation();
        evaluation.setProcessStage(stage);
        evaluation.setTotalStageScore(score);
        evaluation.setIsEliminatedInStage(isEliminated);
        return evaluation;
    }

    private Candidate createCandidate(int id, String quotaName) {
        Candidate candidate = new Candidate();
        candidate.setId(id);
        if (quotaName != null) {
            Quota quota = new Quota();
            quota.setName(quotaName);
            candidate.setQuota(quota);
        }
        return candidate;
    }

    @Test
    void testGenerateRanking_SuccessfulRanking() {
        // --- Setup ---
        Application appA = new Application(); appA.setId(100); appA.setResearchTopic(researchTopic); appA.setCandidate(createCandidate(1, null));
        List<StageEvaluation> evalsA = Arrays.asList(
                createStageEvaluation(1, new BigDecimal("80"), false, new BigDecimal("0.4")), createStageEvaluation(2, new BigDecimal("90"), false, new BigDecimal("0.3")), createStageEvaluation(3, new BigDecimal("100"), false, new BigDecimal("0.3"))
        ); // Score: 89.0

        Application appB = new Application(); appB.setId(200); appB.setResearchTopic(researchTopic); appB.setCandidate(createCandidate(2, null));
        List<StageEvaluation> evalsB = Arrays.asList(
                createStageEvaluation(1, new BigDecimal("70"), false, new BigDecimal("0.4")), createStageEvaluation(2, new BigDecimal("80"), false, new BigDecimal("0.3")), createStageEvaluation(3, new BigDecimal("90"), false, new BigDecimal("0.3"))
        ); // Score: 79.0

        when(selectionProcessRepository.findById(1)).thenReturn(Optional.of(selectionProcess));
        when(applicationRepository.findBySelectionProcess(selectionProcess)).thenReturn(Arrays.asList(appA, appB));
        when(stageEvaluationRepository.findByApplication(appA)).thenReturn(evalsA);
        when(stageEvaluationRepository.findByApplication(appB)).thenReturn(evalsB);

        // --- Action ---
        List<RankedApplicationDTO> result = rankingService.generateRankingForProcess(1);

        // --- Assertions ---
        RankedApplicationDTO dtoA = result.stream().filter(r -> r.getApplicationId() == 100).findFirst().get();
        assertEquals(1, dtoA.getRankingByTopic());
        assertTrue(dtoA.isApproved());
        assertEquals("Classificado", dtoA.getApplicationStatus());

        RankedApplicationDTO dtoB = result.stream().filter(r -> r.getApplicationId() == 200).findFirst().get();
        assertEquals(2, dtoB.getRankingByTopic());
        assertTrue(dtoB.isApproved());
        assertEquals("Classificado", dtoB.getApplicationStatus());
    }

    @Test
    void testGenerateRanking_WithDisqualifiedCandidate() {
        // --- Setup ---
        Application appA = new Application(); appA.setId(100); appA.setResearchTopic(researchTopic); appA.setCandidate(createCandidate(1, null));
        List<StageEvaluation> evalsA = Arrays.asList(createStageEvaluation(1, BigDecimal.TEN, false, new BigDecimal("0.4")), createStageEvaluation(2, new BigDecimal("80"), false, new BigDecimal("0.3")), createStageEvaluation(3, new BigDecimal("80"), false, new BigDecimal("0.3")));

        Application appB = new Application(); appB.setId(200); appB.setResearchTopic(researchTopic); appB.setCandidate(createCandidate(2, null));
        List<StageEvaluation> evalsB = Arrays.asList(createStageEvaluation(1, BigDecimal.TEN, false, new BigDecimal("0.4")), createStageEvaluation(2, new BigDecimal("90"), false, new BigDecimal("0.3")), createStageEvaluation(3, new BigDecimal("60"), true, new BigDecimal("0.3")));

        when(selectionProcessRepository.findById(1)).thenReturn(Optional.of(selectionProcess));
        when(applicationRepository.findBySelectionProcess(selectionProcess)).thenReturn(Arrays.asList(appA, appB));
        when(stageEvaluationRepository.findByApplication(appA)).thenReturn(evalsA);
        when(stageEvaluationRepository.findByApplication(appB)).thenReturn(evalsB);

        // --- Action ---
        List<RankedApplicationDTO> result = rankingService.generateRankingForProcess(1);

        // --- Assertions ---
        RankedApplicationDTO dtoB = result.stream().filter(r -> r.getApplicationId() == 200).findFirst().get();
        assertEquals("Desclassificado", dtoB.getApplicationStatus());
        assertNull(dtoB.getFinalScore());
        assertNull(dtoB.getRankingByTopic());
        assertFalse(dtoB.isApproved());
    }

    @Test
    void testGenerateRanking_TieBreakingLogic() {
        // --- Setup: Both candidates have a final score of 90.0 ---
        Application appA_TieWinner = new Application(); appA_TieWinner.setId(100); appA_TieWinner.setResearchTopic(researchTopic); appA_TieWinner.setCandidate(createCandidate(1, null));
        List<StageEvaluation> evalsA = Arrays.asList(
                createStageEvaluation(1, new BigDecimal("90"), false, new BigDecimal("0.4")),
                createStageEvaluation(2, new BigDecimal("90"), false, new BigDecimal("0.3")), // Higher pre-project score
                createStageEvaluation(3, new BigDecimal("90"), false, new BigDecimal("0.3"))
        ); // Final Score: 90.0

        Application appB_TieLoser = new Application(); appB_TieLoser.setId(200); appB_TieLoser.setResearchTopic(researchTopic); appB_TieLoser.setCandidate(createCandidate(2, null));
        List<StageEvaluation> evalsB = Arrays.asList(
                createStageEvaluation(1, new BigDecimal("90"), false, new BigDecimal("0.4")),
                createStageEvaluation(2, new BigDecimal("80"), false, new BigDecimal("0.3")), // Lower pre-project score
                createStageEvaluation(3, new BigDecimal("100"), false, new BigDecimal("0.3"))
        ); // Final Score: 90.0

        when(selectionProcessRepository.findById(1)).thenReturn(Optional.of(selectionProcess));
        when(applicationRepository.findBySelectionProcess(selectionProcess)).thenReturn(Arrays.asList(appA_TieWinner, appB_TieLoser));
        when(stageEvaluationRepository.findByApplication(appA_TieWinner)).thenReturn(evalsA);
        when(stageEvaluationRepository.findByApplication(appB_TieLoser)).thenReturn(evalsB);

        // --- Action ---
        List<RankedApplicationDTO> result = rankingService.generateRankingForProcess(1);

        // --- Assertions ---
        assertEquals(100, result.get(0).getApplicationId());
        assertEquals(1, result.get(0).getRankingByTopic());
        assertEquals(200, result.get(1).getApplicationId());
        assertEquals(2, result.get(1).getRankingByTopic());
    }

    @Test
    void testGenerateRanking_CandidateOnWaitingList() {
        // --- Setup ---
        researchTopic.setVacancies(1); // Set to 1 vacancy for this test
        Application appA = new Application(); appA.setId(100); appA.setResearchTopic(researchTopic); appA.setCandidate(createCandidate(1, null));
        List<StageEvaluation> evalsA = Arrays.asList(createStageEvaluation(1, new BigDecimal("90"), false, new BigDecimal("0.4")), createStageEvaluation(2, new BigDecimal("90"), false, new BigDecimal("0.3")), createStageEvaluation(3, new BigDecimal("90"), false, new BigDecimal("0.3")));

        Application appB = new Application(); appB.setId(200); appB.setResearchTopic(researchTopic); appB.setCandidate(createCandidate(2, null));
        List<StageEvaluation> evalsB = Arrays.asList(createStageEvaluation(1, new BigDecimal("80"), false, new BigDecimal("0.4")), createStageEvaluation(2, new BigDecimal("80"), false, new BigDecimal("0.3")), createStageEvaluation(3, new BigDecimal("80"), false, new BigDecimal("0.3")));

        when(selectionProcessRepository.findById(1)).thenReturn(Optional.of(selectionProcess));
        when(applicationRepository.findBySelectionProcess(selectionProcess)).thenReturn(Arrays.asList(appA, appB));
        when(stageEvaluationRepository.findByApplication(appA)).thenReturn(evalsA);
        when(stageEvaluationRepository.findByApplication(appB)).thenReturn(evalsB);

        // --- Action ---
        List<RankedApplicationDTO> result = rankingService.generateRankingForProcess(1);

        // --- Assertions ---
        RankedApplicationDTO dtoA = result.stream().filter(r -> r.getApplicationId() == 100).findFirst().get();
        assertTrue(dtoA.isApproved());

        RankedApplicationDTO dtoB = result.stream().filter(r -> r.getApplicationId() == 200).findFirst().get();
        assertFalse(dtoB.isApproved());
        assertEquals("Classificado", dtoB.getApplicationStatus());
        assertNull(dtoB.getRankingByTopic(), "Waiting list candidates should not have a final rank");
    }

    @Test
    void testGenerateRanking_QuotaAdjustment() {
        // --- Setup ---
        researchTopic.setVacancies(2);
        Application appA = new Application(); appA.setId(100); appA.setResearchTopic(researchTopic); appA.setCandidate(createCandidate(1, null));
        List<StageEvaluation> evalsA = Arrays.asList(createStageEvaluation(1, new BigDecimal("95"), false, new BigDecimal("0.4")), createStageEvaluation(2, new BigDecimal("95"), false, new BigDecimal("0.3")), createStageEvaluation(3, new BigDecimal("95"), false, new BigDecimal("0.3")));

        Application appB = new Application(); appB.setId(200); appB.setResearchTopic(researchTopic); appB.setCandidate(createCandidate(2, null));
        List<StageEvaluation> evalsB = Arrays.asList(createStageEvaluation(1, new BigDecimal("90"), false, new BigDecimal("0.4")), createStageEvaluation(2, new BigDecimal("90"), false, new BigDecimal("0.3")), createStageEvaluation(3, new BigDecimal("90"), false, new BigDecimal("0.3")));

        Application appC = new Application(); appC.setId(300); appC.setResearchTopic(researchTopic); appC.setCandidate(createCandidate(3, "Pessoa com deficiência"));
        List<StageEvaluation> evalsC = Arrays.asList(createStageEvaluation(1, new BigDecimal("85"), false, new BigDecimal("0.4")), createStageEvaluation(2, new BigDecimal("85"), false, new BigDecimal("0.3")), createStageEvaluation(3, new BigDecimal("85"), false, new BigDecimal("0.3")));

        when(selectionProcessRepository.findById(1)).thenReturn(Optional.of(selectionProcess));
        when(applicationRepository.findBySelectionProcess(selectionProcess)).thenReturn(Arrays.asList(appA, appB, appC));
        when(stageEvaluationRepository.findByApplication(appA)).thenReturn(evalsA);
        when(stageEvaluationRepository.findByApplication(appB)).thenReturn(evalsB);
        when(stageEvaluationRepository.findByApplication(appC)).thenReturn(evalsC);

        // --- Action ---
        List<RankedApplicationDTO> result = rankingService.generateRankingForProcess(1);

        // --- Assertions ---
        RankedApplicationDTO dtoA = result.stream().filter(r -> r.getApplicationId() == 100).findFirst().get();
        RankedApplicationDTO dtoB = result.stream().filter(r -> r.getApplicationId() == 200).findFirst().get();
        RankedApplicationDTO dtoC = result.stream().filter(r -> r.getApplicationId() == 300).findFirst().get();

        assertTrue(dtoA.isApproved(), "High-scorer A should be approved");
        assertFalse(dtoB.isApproved(), "Medium-scorer B should be replaced by quota candidate");
        assertTrue(dtoC.isApproved(), "Quota candidate C should be approved");

        assertEquals(1, dtoA.getRankingByTopic());
        assertEquals(2, dtoC.getRankingByTopic());
        assertNull(dtoB.getRankingByTopic());
    }
}