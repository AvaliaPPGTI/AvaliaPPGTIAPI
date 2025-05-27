package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.repository.ProcessStageRepository;
import ifpb.edu.br.avaliappgti.repository.SelectionProcessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProcessStageService {

    private final ProcessStageRepository processStageRepository;
    private final SelectionProcessRepository selectionProcessRepository;

    public ProcessStageService(ProcessStageRepository processStageRepository,
                               SelectionProcessRepository selectionProcessRepository) {
        this.processStageRepository = processStageRepository;
        this.selectionProcessRepository = selectionProcessRepository;
    }

    @Transactional(readOnly = true)
    public Optional<ProcessStage> getProcessStageById(Integer id) {
        return processStageRepository.findById(id);
    }


    @Transactional(readOnly = true)
    public List<ProcessStage> getStagesBySelectionProcessId(Integer processId) {
        // find the SelectionProcess entity
        SelectionProcess selectionProcess = selectionProcessRepository.findById(processId)
                .orElseThrow(() -> new NoSuchElementException("Selection Process not found with ID: " + processId));

        // find all ProcessStages associated with that SelectionProcess
        // order by stage_order to ensure consistent listing
        return processStageRepository.findBySelectionProcess(selectionProcess);
    }

    @Transactional
    public ProcessStage saveProcessStage(ProcessStage processStage) {
        return processStageRepository.save(processStage);
    }

    @Transactional
    public void deleteProcessStage(Integer id) {
        processStageRepository.deleteById(id);
    }
}