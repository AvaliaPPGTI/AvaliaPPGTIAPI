package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.StageWeightDTO;
import ifpb.edu.br.avaliappgti.dto.UpdateStageWeightDTO;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.repository.ProcessStageRepository;
import ifpb.edu.br.avaliappgti.repository.SelectionProcessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SelectionProcessServiceTest {

    @Mock
    private SelectionProcessRepository selectionProcessRepository;
    @Mock
    private ProcessStageRepository processStageRepository;

    @InjectMocks
    private SelectionProcessService selectionProcessService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        selectionProcessService = new SelectionProcessService(selectionProcessRepository, processStageRepository);
    }

    @Test
    void testGetCurrentSelectionProcess_found() {
        SelectionProcess process = new SelectionProcess();
        process.setId(1);
        LocalDate today = LocalDate.now();
        when(selectionProcessRepository.findCurrentSelectionProcess(today)).thenReturn(Optional.of(process));

        Optional<SelectionProcess> result = selectionProcessService.getCurrentSelectionProcess();
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void testGetCurrentSelectionProcess_notFound() {
        LocalDate today = LocalDate.now();
        when(selectionProcessRepository.findCurrentSelectionProcess(today)).thenReturn(Optional.empty());

        Optional<SelectionProcess> result = selectionProcessService.getCurrentSelectionProcess();
        assertFalse(result.isPresent());
    }

    @Test
    void testGetCurrentProcessStageWeights_returnsWeights() {
        SelectionProcess process = new SelectionProcess();
        process.setId(1);
        when(selectionProcessRepository.findCurrentSelectionProcess(any(LocalDate.class))).thenReturn(Optional.of(process));

        ProcessStage stage1 = new ProcessStage();
        stage1.setId(10);
        stage1.setStageOrder(1);
        stage1.setStageWeight(new BigDecimal("0.4"));

        ProcessStage stage2 = new ProcessStage();
        stage2.setId(20);
        stage2.setStageOrder(2);
        stage2.setStageWeight(new BigDecimal("0.6"));

        when(processStageRepository.findBySelectionProcess(process)).thenReturn(Arrays.asList(stage2, stage1));

        List<StageWeightDTO> result = selectionProcessService.getCurrentProcessStageWeights();
        assertEquals(2, result.size());
        assertEquals(10, result.get(0).getStageId()); // Sorted by stageOrder
        assertEquals(new BigDecimal("0.4"), result.get(0).getStageWeight());
        assertEquals(20, result.get(1).getStageId());
        assertEquals(new BigDecimal("0.6"), result.get(1).getStageWeight());
    }

    @Test
    void testGetCurrentProcessStageWeights_noActiveProcess() {
        when(selectionProcessRepository.findCurrentSelectionProcess(any(LocalDate.class))).thenReturn(Optional.empty());
        List<StageWeightDTO> result = selectionProcessService.getCurrentProcessStageWeights();
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateCurrentProcessStageWeights_success() {
        SelectionProcess process = new SelectionProcess();
        process.setId(1);
        when(selectionProcessRepository.findCurrentSelectionProcess(any(LocalDate.class))).thenReturn(Optional.of(process));

        ProcessStage stage1 = new ProcessStage();
        stage1.setId(10);
        stage1.setStageWeight(new BigDecimal("0.4"));

        ProcessStage stage2 = new ProcessStage();
        stage2.setId(20);
        stage2.setStageWeight(new BigDecimal("0.6"));

        when(processStageRepository.findBySelectionProcess(process)).thenReturn(Arrays.asList(stage1, stage2));
        when(processStageRepository.save(any(ProcessStage.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateStageWeightDTO dto1 = new UpdateStageWeightDTO();
        dto1.setStageId(10);
        dto1.setStageWeight(new BigDecimal("0.5"));
        UpdateStageWeightDTO dto2 = new UpdateStageWeightDTO();
        dto2.setStageId(20);
        dto2.setStageWeight(new BigDecimal("0.5"));
        List<UpdateStageWeightDTO> updateList = Arrays.asList(dto1, dto2);

        selectionProcessService.updateCurrentProcessStageWeights(updateList);

        assertEquals(new BigDecimal("0.5"), stage1.getStageWeight());
        assertEquals(new BigDecimal("0.5"), stage2.getStageWeight());
    }

    @Test
    void testUpdateCurrentProcessStageWeights_sumNotOne() {
        SelectionProcess process = new SelectionProcess();
        process.setId(1);
        when(selectionProcessRepository.findCurrentSelectionProcess(any(LocalDate.class))).thenReturn(Optional.of(process));

        ProcessStage stage1 = new ProcessStage();
        stage1.setId(10);
        ProcessStage stage2 = new ProcessStage();
        stage2.setId(20);

        when(processStageRepository.findBySelectionProcess(process)).thenReturn(Arrays.asList(stage1, stage2));

        UpdateStageWeightDTO dto1 = new UpdateStageWeightDTO();
        dto1.setStageId(10);
        dto1.setStageWeight(new BigDecimal("0.7"));
        UpdateStageWeightDTO dto2 = new UpdateStageWeightDTO();
        dto2.setStageId(20);
        dto2.setStageWeight(new BigDecimal("0.2"));
        List<UpdateStageWeightDTO> updateList = Arrays.asList(dto1, dto2);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                selectionProcessService.updateCurrentProcessStageWeights(updateList));
        assertTrue(ex.getMessage().contains("sum of all stage weights"));
    }

    @Test
    void testUpdateCurrentProcessStageWeights_stageNotFound() {
        SelectionProcess process = new SelectionProcess();
        process.setId(1);
        when(selectionProcessRepository.findCurrentSelectionProcess(any(LocalDate.class))).thenReturn(Optional.of(process));

        ProcessStage stage1 = new ProcessStage();
        stage1.setId(10);

        when(processStageRepository.findBySelectionProcess(process)).thenReturn(List.of(stage1));

        UpdateStageWeightDTO dto = new UpdateStageWeightDTO();
        dto.setStageId(999);
        dto.setStageWeight(BigDecimal.ONE);
        List<UpdateStageWeightDTO> updateList = Arrays.asList(dto);

        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () ->
                selectionProcessService.updateCurrentProcessStageWeights(updateList));
        assertTrue(ex.getMessage().contains("not found"));
    }
}
