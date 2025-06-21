package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.StageEvaluationCreateDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationUpdateTotalScoreDTO;
import ifpb.edu.br.avaliappgti.model.*;
import ifpb.edu.br.avaliappgti.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageEvaluationServiceTest {

    @Mock
    private StageEvaluationRepository stageEvaluationRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ProcessStageRepository processStageRepository;

    @Mock
    private CommitteeMemberRepository committeeMemberRepository;

    @Mock
    private CriterionScoreRepository criterionScoreRepository;

    @Mock
    private EvaluationCriterionRepository evaluationCriterionRepository;

    @InjectMocks
    private StageEvaluationService service;

    private Application application;
    private ProcessStage processStage;
    private CommitteeMember committeeMember;

    @BeforeEach
    void setUp() {
        application = new Application();
        application.setId(1);

        processStage = new ProcessStage();
        processStage.setId(2);
        processStage.setMinimumPassingScore(new BigDecimal("5"));

        committeeMember = new CommitteeMember();
        committeeMember.setId(3);
    }

    @Test
    void createStageEvaluation_shouldSaveAndReturnDTO() {
        StageEvaluationCreateDTO dto = new StageEvaluationCreateDTO();
        dto.setApplicationId(1);
        dto.setProcessStageId(2);
        dto.setCommitteeMemberId(3);
        dto.setEvaluationDate(LocalDateTime.now());

        StageEvaluation saved = new StageEvaluation();
        saved.setId(99);
        saved.setApplication(application);
        saved.setProcessStage(processStage);
        saved.setCommitteeMember(committeeMember);
        saved.setEvaluationDate(dto.getEvaluationDate());
        saved.setIsEliminatedInStage(false);

        when(applicationRepository.findById(1)).thenReturn(Optional.of(application));
        when(processStageRepository.findById(2)).thenReturn(Optional.of(processStage));
        when(committeeMemberRepository.findById(3)).thenReturn(Optional.of(committeeMember));
        when(stageEvaluationRepository.save(any())).thenReturn(saved);

        StageEvaluationResponseDTO result = service.createStageEvaluation(dto);

        assertEquals(99, result.getId());
        verify(stageEvaluationRepository).save(any(StageEvaluation.class));
    }

    @Test
    void updateStageTotalScore_shouldUpdateScoreAndEliminationStatus() {
        StageEvaluationUpdateTotalScoreDTO updateDTO = new StageEvaluationUpdateTotalScoreDTO();
        updateDTO.setTotalStageScore(new BigDecimal("6"));

        StageEvaluation stageEvaluation = new StageEvaluation();
        stageEvaluation.setId(10);
        stageEvaluation.setProcessStage(processStage);

        when(stageEvaluationRepository.findById(10)).thenReturn(Optional.of(stageEvaluation));
        when(stageEvaluationRepository.save(any())).thenReturn(stageEvaluation);

        StageEvaluationResponseDTO result = service.updateStageTotalScore(10, updateDTO);

        assertEquals(new BigDecimal("6"), result.getTotalStageScore());
        assertFalse(result.getIsEliminatedInStage());
    }

    @Test
    void calculateAndSaveTotalScore_shouldAggregateCorrectly() {
        StageEvaluation evaluation = new StageEvaluation();
        evaluation.setId(1);
        evaluation.setProcessStage(processStage);

        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setId(5);
        criterion.setMaximumScore(new BigDecimal("10"));
        criterion.setWeight(new BigDecimal("1.0"));
        criterion.setParent(null);
        criterion.setChildren(Collections.emptyList()); // It's a leaf

        CriterionScore score = new CriterionScore();
        score.setScoreObtained(new BigDecimal("7"));

        when(stageEvaluationRepository.findById(1)).thenReturn(Optional.of(evaluation));
        when(evaluationCriterionRepository.findByProcessStageAndParentIsNull(processStage)).thenReturn(List.of(criterion));
        when(criterionScoreRepository.findByStageEvaluationAndEvaluationCriterion(evaluation, criterion)).thenReturn(Optional.of(score));
        when(stageEvaluationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StageEvaluationResponseDTO result = service.calculateAndSaveTotalScore(1);

        assertEquals(new BigDecimal("7.0"), result.getTotalStageScore());
        assertFalse(result.getIsEliminatedInStage());
    }

    @Test
    void getStageEvaluationById_shouldReturnDTOIfExists() {
        StageEvaluation eval = new StageEvaluation();
        eval.setId(22);

        when(stageEvaluationRepository.findById(22)).thenReturn(Optional.of(eval));

        Optional<StageEvaluationResponseDTO> result = service.getStageEvaluationById(22);

        assertTrue(result.isPresent());
        assertEquals(22, result.get().getId());
    }

    @Test
    void findStageEvaluationByDetails_shouldReturnDTOIfFound() {
        StageEvaluation eval = new StageEvaluation();
        eval.setId(33);

        when(stageEvaluationRepository.findByApplicationIdAndProcessStageIdAndCommitteeMemberId(1, 2, 3))
                .thenReturn(Optional.of(eval));

        Optional<StageEvaluationResponseDTO> result = service.findStageEvaluationByDetails(1, 2, 3);

        assertTrue(result.isPresent());
        assertEquals(33, result.get().getId());
    }
}