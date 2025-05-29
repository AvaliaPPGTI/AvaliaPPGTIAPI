package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.CriterionScoreInputDTO;
import ifpb.edu.br.avaliappgti.dto.SaveCriterionScoresRequest;
import ifpb.edu.br.avaliappgti.model.*;
import ifpb.edu.br.avaliappgti.repository.*;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CriterionScoreServiceTest {

    @Mock private CriterionScoreRepository criterionScoreRepository;
    @Mock private StageEvaluationRepository stageEvaluationRepository;
    @Mock private EvaluationCriterionRepository evaluationCriterionRepository;

    @InjectMocks private CriterionScoreService criterionScoreService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveCriteriaScoresForStageEvaluation_successful() {
        // Given
        int stageEvaluationId = 1;
        int criterionId = 101;
        BigDecimal scoreValue = new BigDecimal("8.5");

        ProcessStage stage = new ProcessStage();
        stage.setId(1);
        stage.setMinimumPassingScore(new BigDecimal("7.0"));

        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setId(criterionId);
        criterion.setProcessStage(stage);

        StageEvaluation evaluation = new StageEvaluation();
        evaluation.setId(stageEvaluationId);
        evaluation.setProcessStage(stage);

        CriterionScoreInputDTO scoreDTO = new CriterionScoreInputDTO(criterionId, scoreValue);
        SaveCriterionScoresRequest request = new SaveCriterionScoresRequest(List.of(scoreDTO));

        when(stageEvaluationRepository.findById(stageEvaluationId)).thenReturn(Optional.of(evaluation));
        when(evaluationCriterionRepository.findById(criterionId)).thenReturn(Optional.of(criterion));
        when(criterionScoreRepository.findByStageEvaluationAndEvaluationCriterion(evaluation, criterion)).thenReturn(Optional.empty());
        when(criterionScoreRepository.save(any(CriterionScore.class))).thenAnswer(i -> i.getArgument(0));
        when(stageEvaluationRepository.save(any(StageEvaluation.class))).thenReturn(evaluation);

        // When
        StageEvaluationResponseDTO result = criterionScoreService.saveCriteriaScoresForStageEvaluation(stageEvaluationId, request);

        // Then
        assertNotNull(result);
        verify(criterionScoreRepository, times(1)).save(any(CriterionScore.class));
        verify(stageEvaluationRepository, times(1)).save(evaluation);
    }

    @Test
    void testSaveCriteriaScoresForStageEvaluation_invalidCriterionThrowsException() {
        int stageEvaluationId = 1;
        int invalidCriterionId = 99;

        ProcessStage stage = new ProcessStage();
        stage.setId(1);

        StageEvaluation evaluation = new StageEvaluation();
        evaluation.setId(stageEvaluationId);
        evaluation.setProcessStage(stage);

        CriterionScoreInputDTO scoreDTO = new CriterionScoreInputDTO(invalidCriterionId, BigDecimal.TEN);
        SaveCriterionScoresRequest request = new SaveCriterionScoresRequest(List.of(scoreDTO));

        when(stageEvaluationRepository.findById(stageEvaluationId)).thenReturn(Optional.of(evaluation));
        when(evaluationCriterionRepository.findById(invalidCriterionId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                criterionScoreService.saveCriteriaScoresForStageEvaluation(stageEvaluationId, request));
    }

    @Test
    void testGetCriterionScoreById_found() {
        CriterionScore score = new CriterionScore();
        score.setId(1);
        when(criterionScoreRepository.findById(1)).thenReturn(Optional.of(score));
        Optional<CriterionScore> result = criterionScoreService.getCriterionScoreById(1);
        assertTrue(result.isPresent());
    }

    @Test
    void testGetScoresByStageEvaluation_success() {
        int stageEvaluationId = 1;
        StageEvaluation evaluation = new StageEvaluation();
        evaluation.setId(stageEvaluationId);

        when(stageEvaluationRepository.findById(stageEvaluationId)).thenReturn(Optional.of(evaluation));
        when(criterionScoreRepository.findByStageEvaluation(evaluation)).thenReturn(List.of(new CriterionScore()));

        List<CriterionScore> result = criterionScoreService.getScoresByStageEvaluation(stageEvaluationId);
        assertEquals(1, result.size());
    }

    @Test
    void testGetScoresByStageEvaluation_notFound() {
        when(stageEvaluationRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () ->
                criterionScoreService.getScoresByStageEvaluation(99));
    }
}
