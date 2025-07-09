package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.StageRankingDTO;
import ifpb.edu.br.avaliappgti.model.*;
import ifpb.edu.br.avaliappgti.repository.*;
import ifpb.edu.br.avaliappgti.dto.RankedApplicationDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RankingServiceInitialTest {

    private RankingService rankingService;

    private ApplicationRepository applicationRepository;
    private SelectionProcessRepository selectionProcessRepository;
    private StageEvaluationRepository stageEvaluationRepository;
    private ProcessStageRepository processStageRepository;
    private ResearchLineRepository researchLineRepository;
    private ResearchTopicRepository researchTopicRepository;

    @BeforeEach
    void setUp() {
        applicationRepository = mock(ApplicationRepository.class);
        selectionProcessRepository = mock(SelectionProcessRepository.class);
        stageEvaluationRepository = mock(StageEvaluationRepository.class);
        processStageRepository = mock(ProcessStageRepository.class);
        researchLineRepository = mock(ResearchLineRepository.class);
        researchTopicRepository = mock(ResearchTopicRepository.class);

        rankingService = new RankingService(
                applicationRepository,
                selectionProcessRepository,
                stageEvaluationRepository,
                processStageRepository,
                researchLineRepository,
                researchTopicRepository
        );
    }

    @Test
    void generateRankingForProcess_shouldRankApplicationsCorrectly() {
        // Arrange
        SelectionProcess process = new SelectionProcess();
        process.setId(1);

        ResearchTopic topic = new ResearchTopic();
        topic.setId(1);
        topic.setName("AI");
        topic.setVacancies(1);

        Application app1 = new Application();
        app1.setId(1);
        app1.setResearchTopic(topic);

        Application app2 = new Application();
        app2.setId(2);
        app2.setResearchTopic(topic);

        List<Application> applications = Arrays.asList(app1, app2);

        ProcessStage stage1 = new ProcessStage(); stage1.setStageOrder(1); stage1.setStageWeight(new BigDecimal("0.4"));
        ProcessStage stage2 = new ProcessStage(); stage2.setStageOrder(2); stage2.setStageWeight(new BigDecimal("0.3"));
        ProcessStage stage3 = new ProcessStage(); stage3.setStageOrder(3); stage3.setStageWeight(new BigDecimal("0.3"));

        StageEvaluation ev1_app1 = new StageEvaluation(app1, stage1, new BigDecimal("8.0"), false, null, null, null);
        StageEvaluation ev2_app1 = new StageEvaluation(app1, stage2, new BigDecimal("7.0"), false, null, null, null);
        StageEvaluation ev3_app1 = new StageEvaluation(app1, stage3, new BigDecimal("9.0"), false, null, null, null);

        StageEvaluation ev1_app2 = new StageEvaluation(app2, stage1, new BigDecimal("7.0"), false, null, null, null);
        StageEvaluation ev2_app2 = new StageEvaluation(app2, stage2, new BigDecimal("7.0"), true, null, null, null); // Eliminated
        StageEvaluation ev3_app2 = new StageEvaluation(app2, stage3, new BigDecimal("9.0"), false, null, null, null);

        when(selectionProcessRepository.findById(1)).thenReturn(Optional.of(process));
        when(applicationRepository.findBySelectionProcess(process)).thenReturn(applications);
        when(stageEvaluationRepository.findByApplication(app1)).thenReturn(Arrays.asList(ev1_app1, ev2_app1, ev3_app1));
        when(stageEvaluationRepository.findByApplication(app2)).thenReturn(Arrays.asList(ev1_app2, ev2_app2, ev3_app2));

        // Act
        List<RankedApplicationDTO> result = rankingService.generateRankingForProcess(1);

        // Assert
        assertEquals(2, result.size());
        RankedApplicationDTO dto1 = result.get(0);
        assertEquals(app1.getId(), dto1.getApplicationId());
        assertEquals("Classificado", dto1.getApplicationStatus());
        assertEquals(1, dto1.getRankingByTopic());
        assertTrue(dto1.isApproved());

        RankedApplicationDTO dto2 = result.get(1);
        assertEquals(app2.getId(), dto2.getApplicationId());
        assertEquals("Desclassificado", dto2.getApplicationStatus());
        assertFalse(dto2.isApproved());
    }

    @Test
    void generateRankingForProcess_shouldThrowIfSelectionProcessNotFound() {
        // Arrange
        when(selectionProcessRepository.findById(99)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(NoSuchElementException.class, () -> rankingService.generateRankingForProcess(99));
    }

    @Test
    void testGetRankingForStage_shouldReturnSortedRankingDTOs() {
        Integer processId = 1, stageId = 2;
        SelectionProcess process = new SelectionProcess();
        process.setId(processId);

        ProcessStage stage = new ProcessStage();
        stage.setId(stageId);
        stage.setSelectionProcess(process);

        Application app1 = new Application(); app1.setId(10);
        Application app2 = new Application(); app2.setId(20);

        StageEvaluation eval1 = new StageEvaluation(); eval1.setId(1); eval1.setTotalStageScore(BigDecimal.valueOf(85)); eval1.setApplication(app1); eval1.setProcessStage(stage);
        StageEvaluation eval2 = new StageEvaluation(); eval2.setId(2); eval2.setTotalStageScore(BigDecimal.valueOf(90)); eval2.setApplication(app2); eval2.setProcessStage(stage);

        when(processStageRepository.findById(stageId)).thenReturn(Optional.of(stage));
        when(stageEvaluationRepository.findByProcessStage(stage)).thenReturn(new ArrayList<>(List.of(eval1, eval2))); // ✅ Fix

        List<StageRankingDTO> result = rankingService.getRankingForStage(processId, stageId);

        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getStageEvaluationId()); // Highest score first
    }

    @Test
    void testGetRankingForStageByResearchLine_shouldFilterAndSort() {
        Integer processId = 1, stageId = 2, lineId = 3;

        SelectionProcess process = new SelectionProcess(); process.setId(processId);
        ResearchLine line = new ResearchLine(); line.setId(lineId); line.setSelectionProcess(process);
        ProcessStage stage = new ProcessStage(); stage.setId(stageId); stage.setSelectionProcess(process);

        Application app1 = new Application(); app1.setId(10); app1.setResearchLine(line);
        Application app2 = new Application(); app2.setId(20); app2.setResearchLine(null); // Should be filtered out

        StageEvaluation eval1 = new StageEvaluation(); eval1.setId(1); eval1.setTotalStageScore(BigDecimal.valueOf(90)); eval1.setApplication(app1); eval1.setProcessStage(stage);
        StageEvaluation eval2 = new StageEvaluation(); eval2.setId(2); eval2.setTotalStageScore(BigDecimal.valueOf(70)); eval2.setApplication(app2); eval2.setProcessStage(stage);

        when(processStageRepository.findById(stageId)).thenReturn(Optional.of(stage));
        when(researchLineRepository.findById(lineId)).thenReturn(Optional.of(line));
        when(stageEvaluationRepository.findByProcessStage(stage)).thenReturn(List.of(eval1, eval2));

        List<StageRankingDTO> result = rankingService.getRankingForStageByResearchLine(processId, stageId, lineId);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getStageEvaluationId());
    }
    @Test
    void testGetRankingForStageByResearchTopic_shouldFilterByTopic() {
        Integer processId = 1, stageId = 2, topicId = 5;
        SelectionProcess process = new SelectionProcess(); process.setId(processId);
        ResearchLine line = new ResearchLine(); line.setSelectionProcess(process);
        ResearchTopic topic = new ResearchTopic(); topic.setId(topicId); topic.setResearchLine(line);
        ProcessStage stage = new ProcessStage(); stage.setId(stageId); stage.setSelectionProcess(process);

        Application app = new Application(); app.setId(30); app.setResearchTopic(topic);
        StageEvaluation eval = new StageEvaluation(); eval.setId(3); eval.setApplication(app); eval.setProcessStage(stage); eval.setTotalStageScore(BigDecimal.valueOf(88));

        when(processStageRepository.findById(stageId)).thenReturn(Optional.of(stage));
        when(researchTopicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(stageEvaluationRepository.findByProcessStage(stage)).thenReturn(List.of(eval));

        List<StageRankingDTO> result = rankingService.getRankingForStageByResearchTopic(processId, stageId, topicId);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getStageEvaluationId());
    }
    @Test
    void testGetRankingForStageByStatus_shouldFilterByApplicationStatus() {
        Integer processId = 1, stageId = 2;
        ProcessStage stage = new ProcessStage(); stage.setId(stageId);
        SelectionProcess process = new SelectionProcess(); process.setId(processId);
        stage.setSelectionProcess(process);

        Application app1 = new Application(); app1.setId(10); app1.setApplicationStatus("Classificado");
        Application app2 = new Application(); app2.setId(20); app2.setApplicationStatus("Desclassificado");

        StageEvaluation eval1 = new StageEvaluation(); eval1.setId(1); eval1.setApplication(app1); eval1.setProcessStage(stage); eval1.setTotalStageScore(BigDecimal.valueOf(95));
        StageEvaluation eval2 = new StageEvaluation(); eval2.setId(2); eval2.setApplication(app2); eval2.setProcessStage(stage); eval2.setTotalStageScore(BigDecimal.valueOf(85));

        when(processStageRepository.findById(stageId)).thenReturn(Optional.of(stage));
        when(stageEvaluationRepository.findByProcessStage(stage)).thenReturn(List.of(eval1, eval2));

        List<StageRankingDTO> result = rankingService.getRankingForStageByStatus(processId, stageId, "Classificado");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getStageEvaluationId());
    }

}