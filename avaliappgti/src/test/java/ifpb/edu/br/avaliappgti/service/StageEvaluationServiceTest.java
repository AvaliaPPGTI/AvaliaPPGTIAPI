package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.StageEvaluationCreateDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationUpdateTotalScoreDTO;
import ifpb.edu.br.avaliappgti.model.*;
import ifpb.edu.br.avaliappgti.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StageEvaluationServiceTest {

    private StageEvaluationRepository stageEvaluationRepository;
    private ApplicationRepository applicationRepository;
    private ProcessStageRepository processStageRepository;
    private CommitteeMemberRepository committeeMemberRepository;

    private StageEvaluationService service;

    @BeforeEach
    void setup() {
        stageEvaluationRepository = mock(StageEvaluationRepository.class);
        applicationRepository = mock(ApplicationRepository.class);
        processStageRepository = mock(ProcessStageRepository.class);
        committeeMemberRepository = mock(CommitteeMemberRepository.class);

        service = new StageEvaluationService(
                stageEvaluationRepository,
                applicationRepository,
                processStageRepository,
                committeeMemberRepository
        );
    }

    @Test
    void testCreateStageEvaluation_success() {
        StageEvaluationCreateDTO dto = new StageEvaluationCreateDTO();
        dto.setApplicationId(1);
        dto.setProcessStageId(2);
        dto.setCommitteeMemberId(3);
        dto.setEvaluationDate(LocalDateTime.now());

        Application app = new Application();
        ProcessStage stage = new ProcessStage();
        CommitteeMember member = new CommitteeMember();

        when(applicationRepository.findById(1)).thenReturn(Optional.of(app));
        when(processStageRepository.findById(2)).thenReturn(Optional.of(stage));
        when(committeeMemberRepository.findById(3)).thenReturn(Optional.of(member));

        StageEvaluation saved = new StageEvaluation();
        saved.setId(99);
        saved.setApplication(app);
        saved.setProcessStage(stage);
        saved.setCommitteeMember(member);
        when(stageEvaluationRepository.save(any())).thenReturn(saved);

        StageEvaluationResponseDTO response = service.createStageEvaluation(dto);

        assertNotNull(response);
        assertEquals(99, response.getId());
    }

    @Test
    void testCreateStageEvaluation_applicationNotFound() {
        StageEvaluationCreateDTO dto = new StageEvaluationCreateDTO();
        dto.setApplicationId(1);
        when(applicationRepository.findById(1)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> service.createStageEvaluation(dto));
        assertEquals("Application not found with ID: 1", ex.getMessage());
    }

    @Test
    void testCreateStageEvaluation_processStageNotFound() {
        StageEvaluationCreateDTO dto = new StageEvaluationCreateDTO();
        dto.setApplicationId(1);
        dto.setProcessStageId(2);

        when(applicationRepository.findById(1)).thenReturn(Optional.of(new Application()));
        when(processStageRepository.findById(2)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> service.createStageEvaluation(dto));
        assertEquals("Process Stage not found with ID: 2", ex.getMessage());
    }

    @Test
    void testCreateStageEvaluation_committeeMemberNotFound() {
        StageEvaluationCreateDTO dto = new StageEvaluationCreateDTO();
        dto.setApplicationId(1);
        dto.setProcessStageId(2);
        dto.setCommitteeMemberId(3);

        when(applicationRepository.findById(1)).thenReturn(Optional.of(new Application()));
        when(processStageRepository.findById(2)).thenReturn(Optional.of(new ProcessStage()));
        when(committeeMemberRepository.findById(3)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> service.createStageEvaluation(dto));
        assertEquals("Evaluating Faculty not found with ID: 3", ex.getMessage());
    }

    @Test
    void testGetStageEvaluationById_found() {
        StageEvaluation se = new StageEvaluation();
        se.setId(10);
        when(stageEvaluationRepository.findById(10)).thenReturn(Optional.of(se));

        Optional<StageEvaluationResponseDTO> result = service.getStageEvaluationById(10);

        assertTrue(result.isPresent());
        assertEquals(10, result.get().getId());
    }

    @Test
    void testGetStageEvaluationById_notFound() {
        when(stageEvaluationRepository.findById(10)).thenReturn(Optional.empty());

        Optional<StageEvaluationResponseDTO> result = service.getStageEvaluationById(10);

        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateStageTotalScore_passed() {
        StageEvaluation se = new StageEvaluation();
        se.setId(1);

        ProcessStage stage = new ProcessStage();
        stage.setMinimumPassingScore(new BigDecimal("70"));

        se.setProcessStage(stage);

        when(stageEvaluationRepository.findById(1)).thenReturn(Optional.of(se));

        StageEvaluationUpdateTotalScoreDTO dto = new StageEvaluationUpdateTotalScoreDTO();
        dto.setTotalStageScore(new BigDecimal("85"));

        when(stageEvaluationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StageEvaluationResponseDTO result = service.updateStageTotalScore(1, dto);

        assertNotNull(result);
        assertFalse(se.getIsEliminatedInStage());
        assertEquals(new BigDecimal("85"), se.getTotalStageScore());
    }

    @Test
    void testUpdateStageTotalScore_failed() {
        StageEvaluation se = new StageEvaluation();
        se.setId(2);

        ProcessStage stage = new ProcessStage();
        stage.setMinimumPassingScore(new BigDecimal("70"));
        se.setProcessStage(stage);

        when(stageEvaluationRepository.findById(2)).thenReturn(Optional.of(se));

        StageEvaluationUpdateTotalScoreDTO dto = new StageEvaluationUpdateTotalScoreDTO();
        dto.setTotalStageScore(new BigDecimal("50"));

        when(stageEvaluationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StageEvaluationResponseDTO result = service.updateStageTotalScore(2, dto);

        assertNotNull(result);
        assertTrue(se.getIsEliminatedInStage());
        assertEquals(new BigDecimal("50"), se.getTotalStageScore());
    }

    @Test
    void testUpdateStageTotalScore_stageEvaluationNotFound() {
        when(stageEvaluationRepository.findById(999)).thenReturn(Optional.empty());

        StageEvaluationUpdateTotalScoreDTO dto = new StageEvaluationUpdateTotalScoreDTO();
        dto.setTotalStageScore(new BigDecimal("100"));

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> service.updateStageTotalScore(999, dto));

        assertEquals("Stage Evaluation not found with ID: 999", ex.getMessage());
    }

    @Test
    void testUpdateStageTotalScore_processStageIsNull() {
        StageEvaluation se = new StageEvaluation();
        se.setId(3);
        se.setProcessStage(null);

        when(stageEvaluationRepository.findById(3)).thenReturn(Optional.of(se));

        StageEvaluationUpdateTotalScoreDTO dto = new StageEvaluationUpdateTotalScoreDTO();
        dto.setTotalStageScore(new BigDecimal("90"));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.updateStageTotalScore(3, dto));

        assertEquals("StageEvaluation with ID 3 is not linked to a ProcessStage.", ex.getMessage());
    }
}
