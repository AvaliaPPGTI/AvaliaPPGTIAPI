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
import java.time.LocalDate;
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
    // We don't need to mock ProcessStageRepository, ResearchLineRepository etc. for these tests

    @InjectMocks
    private RankingService rankingService;

    private SelectionProcess selectionProcess;
    private ResearchTopic researchTopic;

    @BeforeEach
    void setUp() {
        // Common setup for all tests
        selectionProcess = new SelectionProcess();
        selectionProcess.setId(1);
        selectionProcess.setName("Processo de Seleção de Testes");
        selectionProcess.setYear("2025");
        selectionProcess.setStartDate(LocalDate.of(2025, 1, 1));
        selectionProcess.setEndDate(LocalDate.of(2025, 12, 31)); 
        selectionProcess.setProgram("PPGTI");
        selectionProcess.setSemester("1º Semestre");

        researchTopic = new ResearchTopic();
        researchTopic.setId(10);
        researchTopic.setName("Test Topic");
        researchTopic.setVacancies(1); // Only 1 vacancy to test approval logic
    }

    private StageEvaluation createStageEvaluation(int stageOrder, BigDecimal score, boolean isEliminated) {
        ProcessStage stage = new ProcessStage();
        stage.setStageOrder(stageOrder);
        // Set stageWeight for each stageOrder
        if (stageOrder == 1) stage.setStageWeight(new BigDecimal("0.4"));
        if (stageOrder == 2) stage.setStageWeight(new BigDecimal("0.3"));
        if (stageOrder == 3) stage.setStageWeight(new BigDecimal("0.3"));

        StageEvaluation evaluation = new StageEvaluation();
        evaluation.setProcessStage(stage);
        evaluation.setTotalStageScore(score);
        evaluation.setIsEliminatedInStage(isEliminated);
        return evaluation;
    }

    @Test
    void testGenerateRanking_SuccessfulRanking() {
        // --- Setup ---
        // Candidate A (should rank 1st)
        Application appA = new Application();
        appA.setId(100);
        appA.setResearchTopic(researchTopic);
        appA.setCandidate(new Candidate());
        List<StageEvaluation> evalsA = Arrays.asList(
                createStageEvaluation(1, new BigDecimal("80"), false), // Curriculum
                createStageEvaluation(2, new BigDecimal("90"), false), // Pre-project
                createStageEvaluation(3, new BigDecimal("100"), false) // Interview
        );
        // Expected Final Score for A: (80*0.4)+(90*0.3)+(100*0.3) = 32 + 27 + 30 = 89.0

        // Candidate B (should rank 2nd)
        Application appB = new Application();
        appB.setId(200);
        appB.setResearchTopic(researchTopic);
        appB.setCandidate(new Candidate());
        List<StageEvaluation> evalsB = Arrays.asList(
                createStageEvaluation(1, new BigDecimal("70"), false),
                createStageEvaluation(2, new BigDecimal("80"), false),
                createStageEvaluation(3, new BigDecimal("90"), false)
        );
        // Expected Final Score for B: (70*0.4)+(80*0.3)+(90*0.3) = 28 + 24 + 27 = 79.0

        when(selectionProcessRepository.findById(1)).thenReturn(Optional.of(selectionProcess));
        when(applicationRepository.findBySelectionProcess(selectionProcess)).thenReturn(Arrays.asList(appA, appB));
        when(stageEvaluationRepository.findByApplication(appA)).thenReturn(evalsA);
        when(stageEvaluationRepository.findByApplication(appB)).thenReturn(evalsB);

        // --- Action ---
        List<RankedApplicationDTO> result = rankingService.generateRankingForProcess(1);

        // --- Assertions ---
        assertEquals(2, result.size());

        // Check Candidate A
        RankedApplicationDTO dtoA = result.get(0);
        assertEquals(100, dtoA.getApplicationId());
        assertEquals(0, new BigDecimal("89.0").compareTo(dtoA.getFinalScore()));
        assertEquals(1, dtoA.getRankingByTopic());
        assertTrue(dtoA.isApproved()); // Rank 1, 1 vacancy
        assertEquals("Classificado", dtoA.getApplicationStatus());

        // Check Candidate B
        RankedApplicationDTO dtoB = result.get(1);
        assertEquals(200, dtoB.getApplicationId());
        assertEquals(0, new BigDecimal("79.0").compareTo(dtoB.getFinalScore()));
        assertEquals(2, dtoB.getRankingByTopic());
        assertFalse(dtoB.isApproved()); // Rank 2, 1 vacancy
        assertEquals("Classificado", dtoB.getApplicationStatus());
    }

    @Test
    void testGenerateRanking_WithDisqualifiedCandidate() {
        // --- Setup ---
        Application appA = new Application(); // Will be ranked
        appA.setId(100);
        appA.setResearchTopic(researchTopic);
        appA.setCandidate(new Candidate());
        List<StageEvaluation> evalsA = Arrays.asList(createStageEvaluation(1, BigDecimal.TEN, false), createStageEvaluation(2, new BigDecimal("80"), false), createStageEvaluation(3, new BigDecimal("80"), false));

        Application appB = new Application(); // Will be disqualified
        appB.setId(200);
        appB.setResearchTopic(researchTopic);
        appB.setCandidate(new Candidate());
        // Disqualified because interview stage is marked as eliminated
        List<StageEvaluation> evalsB = Arrays.asList(createStageEvaluation(1, BigDecimal.TEN, false), createStageEvaluation(2, new BigDecimal("90"), false), createStageEvaluation(3, new BigDecimal("60"), true));

        when(selectionProcessRepository.findById(1)).thenReturn(Optional.of(selectionProcess));
        when(applicationRepository.findBySelectionProcess(selectionProcess)).thenReturn(Arrays.asList(appA, appB));
        when(stageEvaluationRepository.findByApplication(appA)).thenReturn(evalsA);
        when(stageEvaluationRepository.findByApplication(appB)).thenReturn(evalsB);

        // --- Action ---
        List<RankedApplicationDTO> result = rankingService.generateRankingForProcess(1);

        // --- Assertions ---
        assertEquals(2, result.size());

        // Find the DTO for the disqualified candidate
        RankedApplicationDTO dtoB = result.stream().filter(r -> r.getApplicationId() == 200).findFirst().get();
        assertEquals("Desclassificado", dtoB.getApplicationStatus());
        assertNull(dtoB.getFinalScore());
        assertNull(dtoB.getRankingByTopic());
        assertFalse(dtoB.isApproved());
    }

    @Test
    void testGenerateRanking_TieBreakingLogic() {
        // --- Setup ---
        // Both candidates will have the same final score: 85.0
        // Candidate A has a higher pre-project score (tie-breaker 1)
        Application appA = new Application();
        appA.setId(100);
        appA.setResearchTopic(researchTopic);
        appA.setCandidate(new Candidate());
        List<StageEvaluation> evalsA = Arrays.asList(
                createStageEvaluation(1, new BigDecimal("80"), false),
                createStageEvaluation(2, new BigDecimal("90"), false), // Higher pre-project score
                createStageEvaluation(3, new BigDecimal("86.667"), false)
        );

        // Candidate B
        Application appB = new Application();
        appB.setId(200);
        appB.setResearchTopic(researchTopic);
        appB.setCandidate(new Candidate());
        List<StageEvaluation> evalsB = Arrays.asList(
                createStageEvaluation(1, new BigDecimal("85"), false),
                createStageEvaluation(2, new BigDecimal("80"), false), // Lower pre-project score
                createStageEvaluation(3, new BigDecimal("90"), false)
        );

        when(selectionProcessRepository.findById(1)).thenReturn(Optional.of(selectionProcess));
        when(applicationRepository.findBySelectionProcess(selectionProcess)).thenReturn(Arrays.asList(appA, appB));
        when(stageEvaluationRepository.findByApplication(appA)).thenReturn(evalsA);
        when(stageEvaluationRepository.findByApplication(appB)).thenReturn(evalsB);

        // --- Action ---
        List<RankedApplicationDTO> result = rankingService.generateRankingForProcess(1);

        // --- Assertions ---
        // Candidate A should be ranked 1st due to higher pre-project score
        assertEquals(100, result.get(0).getApplicationId());
        assertEquals(1, result.get(0).getRankingByTopic());

        // Candidate B should be ranked 2nd
        assertEquals(200, result.get(1).getApplicationId());
        assertEquals(2, result.get(1).getRankingByTopic());
    }

    @Test
    void testGenerateRanking_CandidateOnWaitingList() {
        // --- Setup ---
        // We have a research topic with only 1 vacancy, configured in setUp()

        // Candidate A (should be approved)
        Application appA = new Application();
        appA.setId(100);
        appA.setResearchTopic(researchTopic);
        appA.setCandidate(new Candidate());
        List<StageEvaluation> evalsA = Arrays.asList(
                createStageEvaluation(1, new BigDecimal("90"), false),
                createStageEvaluation(2, new BigDecimal("90"), false),
                createStageEvaluation(3, new BigDecimal("90"), false)
        ); // Final Score: 90.0

        // Candidate B (should be on the waiting list)
        Application appB = new Application();
        appB.setId(200);
        appB.setResearchTopic(researchTopic);
        appB.setCandidate(new Candidate());
        List<StageEvaluation> evalsB = Arrays.asList(
                createStageEvaluation(1, new BigDecimal("80"), false),
                createStageEvaluation(2, new BigDecimal("80"), false),
                createStageEvaluation(3, new BigDecimal("80"), false)
        ); // Final Score: 80.0

        when(selectionProcessRepository.findById(1)).thenReturn(Optional.of(selectionProcess));
        when(applicationRepository.findBySelectionProcess(selectionProcess)).thenReturn(Arrays.asList(appA, appB));
        when(stageEvaluationRepository.findByApplication(appA)).thenReturn(evalsA);
        when(stageEvaluationRepository.findByApplication(appB)).thenReturn(evalsB);

        // --- Action ---
        List<RankedApplicationDTO> result = rankingService.generateRankingForProcess(1);

        // --- Assertions ---
        assertEquals(2, result.size());

        // Check Candidate A (Approved)
        RankedApplicationDTO dtoA = result.get(0);
        assertEquals(100, dtoA.getApplicationId());
        assertEquals(1, dtoA.getRankingByTopic());
        assertTrue(dtoA.isApproved());
        assertEquals("Classificado", dtoA.getApplicationStatus());

        // Check Candidate B (Waiting List)
        RankedApplicationDTO dtoB = result.get(1);
        assertEquals(200, dtoB.getApplicationId());
        assertEquals(2, dtoB.getRankingByTopic());
        assertFalse(dtoB.isApproved()); // Correctly NOT approved
        assertEquals("Classificado", dtoB.getApplicationStatus()); // But is still ranked
    }
}