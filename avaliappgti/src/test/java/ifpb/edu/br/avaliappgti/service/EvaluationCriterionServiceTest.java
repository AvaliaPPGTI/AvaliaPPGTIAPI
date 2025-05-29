package ifpb.edu.br.avaliappgti.service;


import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.repository.EvaluationCriterionRepository;
import ifpb.edu.br.avaliappgti.repository.ProcessStageRepository;
import ifpb.edu.br.avaliappgti.repository.SelectionProcessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvaluationCriterionServiceTest {

    private EvaluationCriterionRepository evaluationCriterionRepository;
    private ProcessStageRepository processStageRepository;
    private SelectionProcessRepository selectionProcessRepository;
    private EvaluationCriterionService evaluationCriterionService;

    @BeforeEach
    void setUp() {
        evaluationCriterionRepository = mock(EvaluationCriterionRepository.class);
        processStageRepository = mock(ProcessStageRepository.class);
        selectionProcessRepository = mock(SelectionProcessRepository.class);
        evaluationCriterionService = new EvaluationCriterionService(
                evaluationCriterionRepository,
                processStageRepository,
                selectionProcessRepository
        );
    }

    @Test
    void testGetEvaluationCriterionById_found() {
        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setId(1);
        when(evaluationCriterionRepository.findById(1)).thenReturn(Optional.of(criterion));

        Optional<EvaluationCriterion> result = evaluationCriterionService.getEvaluationCriterionById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void testGetEvaluationCriterionById_notFound() {
        when(evaluationCriterionRepository.findById(1)).thenReturn(Optional.empty());

        Optional<EvaluationCriterion> result = evaluationCriterionService.getEvaluationCriterionById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetCriteriaByProcessStageAndSelectionProcessId_success() {
        Integer processId = 10;
        Integer stageId = 20;

        SelectionProcess selectionProcess = new SelectionProcess();
        selectionProcess.setId(processId);

        ProcessStage processStage = new ProcessStage();
        processStage.setId(stageId);
        processStage.setSelectionProcess(selectionProcess);

        EvaluationCriterion ec1 = new EvaluationCriterion();
        EvaluationCriterion ec2 = new EvaluationCriterion();
        List<EvaluationCriterion> criteria = Arrays.asList(ec1, ec2);

        when(selectionProcessRepository.findById(processId)).thenReturn(Optional.of(selectionProcess));
        when(processStageRepository.findById(stageId)).thenReturn(Optional.of(processStage));
        when(evaluationCriterionRepository.findByProcessStage(processStage)).thenReturn(criteria);

        List<EvaluationCriterion> result = evaluationCriterionService
                .getCriteriaByProcessStageAndSelectionProcessId(processId, stageId);

        assertEquals(2, result.size());
        verify(evaluationCriterionRepository).findByProcessStage(processStage);
    }

    @Test
    void testGetCriteriaByProcessStageAndSelectionProcessId_selectionProcessNotFound() {
        when(selectionProcessRepository.findById(99)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                evaluationCriterionService.getCriteriaByProcessStageAndSelectionProcessId(99, 1));

        assertEquals("Selection Process not found with ID: 99", exception.getMessage());
    }

    @Test
    void testGetCriteriaByProcessStageAndSelectionProcessId_processStageNotFound() {
        SelectionProcess selectionProcess = new SelectionProcess();
        selectionProcess.setId(10);

        when(selectionProcessRepository.findById(10)).thenReturn(Optional.of(selectionProcess));
        when(processStageRepository.findById(999)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                evaluationCriterionService.getCriteriaByProcessStageAndSelectionProcessId(10, 999));

        assertEquals("Process Stage not found with ID: 999", exception.getMessage());
    }

    @Test
    void testGetCriteriaByProcessStageAndSelectionProcessId_stageDoesNotBelongToProcess() {
        SelectionProcess sp1 = new SelectionProcess();
        sp1.setId(1);

        SelectionProcess sp2 = new SelectionProcess();
        sp2.setId(2);

        ProcessStage stage = new ProcessStage();
        stage.setId(5);
        stage.setSelectionProcess(sp2); // belongs to a different process

        when(selectionProcessRepository.findById(1)).thenReturn(Optional.of(sp1));
        when(processStageRepository.findById(5)).thenReturn(Optional.of(stage));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                evaluationCriterionService.getCriteriaByProcessStageAndSelectionProcessId(1, 5));

        assertEquals("Process Stage with ID 5 does not belong to Selection Process with ID 1", exception.getMessage());
    }

    @Test
    void testSaveEvaluationCriterion() {
        EvaluationCriterion criterion = new EvaluationCriterion();
        when(evaluationCriterionRepository.save(criterion)).thenReturn(criterion);

        EvaluationCriterion result = evaluationCriterionService.saveEvaluationCriterion(criterion);

        assertNotNull(result);
        verify(evaluationCriterionRepository, times(1)).save(criterion);
    }

    @Test
    void testDeleteEvaluationCriterion() {
        evaluationCriterionService.deleteEvaluationCriterion(10);

        verify(evaluationCriterionRepository, times(1)).deleteById(10);
    }
}