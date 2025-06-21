package ifpb.edu.br.avaliappgti.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ifpb.edu.br.avaliappgti.dto.*;
import ifpb.edu.br.avaliappgti.model.*;
import ifpb.edu.br.avaliappgti.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

class CriterionScoreServiceTest {

    @Mock private CriterionScoreRepository criterionScoreRepository;
    @Mock private StageEvaluationRepository stageEvaluationRepository;
    @Mock private EvaluationCriterionRepository evaluationCriterionRepository;

    @InjectMocks private CriterionScoreService criterionScoreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveCriteriaScoresForStageEvaluation_success() {
        Integer stageEvaluationId = 1;
        Integer criterionId = 10;

        StageEvaluation stageEvaluation = new StageEvaluation();
        stageEvaluation.setId(stageEvaluationId);

        ProcessStage processStage = new ProcessStage();
        processStage.setId(100);
        processStage.setMinimumPassingScore(BigDecimal.valueOf(6));
        stageEvaluation.setProcessStage(processStage);

        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setId(criterionId);
        criterion.setWeight(BigDecimal.ONE);
        criterion.setProcessStage(processStage);

        SaveCriterionScoresRequest request = new SaveCriterionScoresRequest();
        CriterionScoreInputDTO inputDTO = new CriterionScoreInputDTO();
        inputDTO.setEvaluationCriterionId(criterionId);
        inputDTO.setScoreObtained(BigDecimal.valueOf(7));
        request.setScores(List.of(inputDTO));

        when(stageEvaluationRepository.findById(stageEvaluationId)).thenReturn(Optional.of(stageEvaluation));
        when(evaluationCriterionRepository.findById(criterionId)).thenReturn(Optional.of(criterion));
        when(evaluationCriterionRepository.findByProcessStageAndParentIsNull(processStage)).thenReturn(List.of(criterion));
        when(criterionScoreRepository.findByStageEvaluationAndEvaluationCriterion(stageEvaluation, criterion)).thenReturn(Optional.empty());
        when(criterionScoreRepository.save(any(CriterionScore.class))).thenAnswer(i -> i.getArgument(0));
        when(stageEvaluationRepository.save(any(StageEvaluation.class))).thenAnswer(i -> i.getArgument(0));
        when(criterionScoreRepository.findByStageEvaluationAndEvaluationCriterion(stageEvaluation, criterion))
                .thenReturn(Optional.of(new CriterionScore(stageEvaluation, criterion, BigDecimal.valueOf(7))));

        StageEvaluationResponseDTO response = criterionScoreService.saveCriteriaScoresForStageEvaluation(stageEvaluationId, request);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(7), response.getTotalStageScore());
        assertFalse(response.getIsEliminatedInStage());
    }

    @Test
    void testGetCriterionScoreById_found() {
        CriterionScore score = new CriterionScore();
        score.setId(1);
        when(criterionScoreRepository.findById(1)).thenReturn(Optional.of(score));
        Optional<CriterionScore> result = criterionScoreService.getCriterionScoreById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void testGetCriterionScoreById_notFound() {
        when(criterionScoreRepository.findById(1)).thenReturn(Optional.empty());
        Optional<CriterionScore> result = criterionScoreService.getCriterionScoreById(1);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateCriterionScore_success() {
        CriterionScore score = new CriterionScore();
        score.setId(1);
        score.setScoreObtained(BigDecimal.valueOf(5));

        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setWeight(BigDecimal.ONE);

        StageEvaluation stageEvaluation = new StageEvaluation();
        ProcessStage processStage = new ProcessStage();
        processStage.setMinimumPassingScore(BigDecimal.valueOf(5));
        stageEvaluation.setProcessStage(processStage);

        score.setEvaluationCriterion(criterion);
        score.setStageEvaluation(stageEvaluation);

        when(criterionScoreRepository.findById(1)).thenReturn(Optional.of(score));
        when(criterionScoreRepository.findByStageEvaluation(stageEvaluation)).thenReturn(List.of(score));
        when(evaluationCriterionRepository.findByProcessStageAndParentIsNull(processStage)).thenReturn(List.of(criterion));
        when(criterionScoreRepository.findByStageEvaluationAndEvaluationCriterion(stageEvaluation, criterion)).thenReturn(Optional.of(score));
        when(stageEvaluationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UpdateCriterionScoreDTO updateDTO = new UpdateCriterionScoreDTO();
        updateDTO.setScoreObtained(BigDecimal.valueOf(8));

        StageEvaluationResponseDTO response = criterionScoreService.updateCriterionScore(1, updateDTO);

        assertEquals(BigDecimal.valueOf(8), response.getTotalStageScore());
        assertFalse(response.getIsEliminatedInStage());
    }

    @Test
    void testGetScoresByStageEvaluation_returnsDTOs() {
        StageEvaluation stageEvaluation = new StageEvaluation();
        stageEvaluation.setId(1);

        CriterionScore score1 = new CriterionScore();
        score1.setId(101);
        score1.setScoreObtained(BigDecimal.TEN);
        score1.setStageEvaluation(stageEvaluation);

        when(stageEvaluationRepository.findById(1)).thenReturn(Optional.of(stageEvaluation));
        when(criterionScoreRepository.findByStageEvaluation(stageEvaluation)).thenReturn(List.of(score1));

        List<CriterionScoreResponseDTO> result = criterionScoreService.getScoresByStageEvaluation(1);
        assertEquals(1, result.size());
        assertEquals(BigDecimal.TEN, result.get(0).getScoreObtained());
    }
}
