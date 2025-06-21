package ifpb.edu.br.avaliappgti.service;


import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.repository.ProcessStageRepository;
import ifpb.edu.br.avaliappgti.repository.SelectionProcessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcessStageServiceTest {

    private ProcessStageRepository processStageRepository;
    private SelectionProcessRepository selectionProcessRepository;
    private ProcessStageService processStageService;

    @BeforeEach
    void setUp() {
        processStageRepository = mock(ProcessStageRepository.class);
        selectionProcessRepository = mock(SelectionProcessRepository.class);
        processStageService = new ProcessStageService(processStageRepository, selectionProcessRepository);
    }

    @Test
    void testGetProcessStageById_found() {
        ProcessStage stage = new ProcessStage();
        stage.setId(1);
        when(processStageRepository.findById(1)).thenReturn(Optional.of(stage));

        Optional<ProcessStage> result = processStageService.getProcessStageById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void testGetProcessStageById_notFound() {
        when(processStageRepository.findById(1)).thenReturn(Optional.empty());

        Optional<ProcessStage> result = processStageService.getProcessStageById(1);
        assertFalse(result.isPresent());
    }

    @Test
    void testGetStagesBySelectionProcessId_found() {
        SelectionProcess selectionProcess = new SelectionProcess();
        selectionProcess.setId(1);

        ProcessStage stage1 = new ProcessStage();
        ProcessStage stage2 = new ProcessStage();
        List<ProcessStage> stages = Arrays.asList(stage1, stage2);

        when(selectionProcessRepository.findById(1)).thenReturn(Optional.of(selectionProcess));
        when(processStageRepository.findBySelectionProcess(selectionProcess)).thenReturn(stages);

        List<ProcessStage> result = processStageService.getStagesBySelectionProcessId(1);
        assertEquals(2, result.size());
    }

    @Test
    void testGetStagesBySelectionProcessId_notFound() {
        when(selectionProcessRepository.findById(99)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                processStageService.getStagesBySelectionProcessId(99));
        assertEquals("Selection Process not found with ID: 99", exception.getMessage());
    }

    @Test
    void testSaveProcessStage() {
        ProcessStage stage = new ProcessStage();
        when(processStageRepository.save(stage)).thenReturn(stage);

        ProcessStage result = processStageService.saveProcessStage(stage);
        assertNotNull(result);
        verify(processStageRepository, times(1)).save(stage);
    }

    @Test
    void testDeleteProcessStage() {
        processStageService.deleteProcessStage(1);
        verify(processStageRepository, times(1)).deleteById(1);
    }
}