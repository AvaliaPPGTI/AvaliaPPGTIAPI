package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.UpdateEvaluationCriterionRequestDTO;
import ifpb.edu.br.avaliappgti.model.*;
import ifpb.edu.br.avaliappgti.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvaluationCriterionServiceTest {

    @InjectMocks
    private EvaluationCriterionService service;

    @Mock
    private EvaluationCriterionRepository criterionRepository;
    @Mock
    private ProcessStageRepository stageRepository;
    @Mock
    private SelectionProcessRepository selectionRepository;
    @Mock
    private CriterionScoreRepository scoreRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new EvaluationCriterionService(criterionRepository, stageRepository, selectionRepository, scoreRepository);
    }

    @Test
    void testGetCriteriaByProcessStageAndSelectionProcessId_success() {
        Integer processId = 1;
        Integer stageId = 10;

        SelectionProcess process = new SelectionProcess();
        process.setId(processId);

        ProcessStage stage = new ProcessStage();
        stage.setId(stageId);
        stage.setSelectionProcess(process);

        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setId(100);

        when(selectionRepository.findById(processId)).thenReturn(Optional.of(process));
        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));
        when(criterionRepository.findByProcessStage(stage)).thenReturn(List.of(criterion));

        List<EvaluationCriterion> result = service.getCriteriaByProcessStageAndSelectionProcessId(processId, stageId);

        assertEquals(1, result.size());
        verify(selectionRepository).findById(processId);
        verify(stageRepository).findById(stageId);
    }

    @Test
    void testCreateTopLevelCriterion_success() {
        ProcessStage stage = new ProcessStage();
        stage.setId(1);

        when(stageRepository.findById(1)).thenReturn(Optional.of(stage));

        EvaluationCriterion saved = new EvaluationCriterion(stage, "Critério", BigDecimal.TEN, BigDecimal.ONE, null);
        when(criterionRepository.save(any())).thenReturn(saved);

        EvaluationCriterion result = service.createTopLevelCriterion(1, "Critério", BigDecimal.TEN, BigDecimal.ONE);

        assertNotNull(result);
        assertEquals("Critério", result.getCriterionDescription());
        verify(criterionRepository).save(any());
    }

    @Test
    void testCreateSubCriterion_success() {
        ProcessStage stage = new ProcessStage();
        stage.setId(1);

        EvaluationCriterion topLevel = new EvaluationCriterion(stage, "Top", BigDecimal.TEN, null, null);
        topLevel.setId(1);

        when(criterionRepository.findById(1)).thenReturn(Optional.of(topLevel));
        when(criterionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        EvaluationCriterion result = service.createSubCriterion("Sub", BigDecimal.ONE, BigDecimal.valueOf(0.5), 1);

        assertEquals("Sub", result.getCriterionDescription());
        assertEquals(topLevel, result.getParent());
        verify(criterionRepository).save(any());
    }

    @Test
    void testUpdateEvaluationCriterion_patchOnlyDescription() {
        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setId(1);
        criterion.setCriterionDescription("Old");

        UpdateEvaluationCriterionRequestDTO dto = new UpdateEvaluationCriterionRequestDTO();
        dto.setDescription("New");

        when(criterionRepository.findById(1)).thenReturn(Optional.of(criterion));
        when(criterionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        EvaluationCriterion updated = service.updateEvaluationCriterion(1, dto);

        assertEquals("New", updated.getCriterionDescription());
        verify(criterionRepository).save(criterion);
    }

    @Test
    void testDeleteEvaluationCriterion_success() {
        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setId(1);

        when(criterionRepository.findById(1)).thenReturn(Optional.of(criterion));

        service.deleteEvaluationCriterion(1);

        verify(scoreRepository).deleteByEvaluationCriterion(criterion);
        verify(criterionRepository).delete(criterion);
    }

    @Test
    void testGetEvaluationCriterionById_success() {
        EvaluationCriterion criterion = new EvaluationCriterion();
        criterion.setId(10);

        when(criterionRepository.findById(10)).thenReturn(Optional.of(criterion));

        EvaluationCriterion result = service.getEvaluationCriterionById(10);

        assertEquals(10, result.getId());
        verify(criterionRepository).findById(10);
    }

    @Test
    void testGetTopLevelCriteriaByProcessStage_success() {
        ProcessStage stage = new ProcessStage();
        stage.setId(5);
        EvaluationCriterion c1 = new EvaluationCriterion();
        c1.setId(100);

        when(stageRepository.findById(5)).thenReturn(Optional.of(stage));
        when(criterionRepository.findByProcessStageAndParentIsNull(stage)).thenReturn(List.of(c1));

        List<EvaluationCriterion> result = service.getTopLevelCriteriaByProcessStage(5);

        assertEquals(1, result.size());
        verify(criterionRepository).findByProcessStageAndParentIsNull(stage);
    }
}
