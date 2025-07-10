package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.StageWeightDTO;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.repository.ProcessStageRepository;
import ifpb.edu.br.avaliappgti.repository.SelectionProcessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SelectionProcessService {
    private final SelectionProcessRepository selectionProcessRepository;
    private final ProcessStageRepository processStageRepository;

    public SelectionProcessService(SelectionProcessRepository selectionProcessRepository, ProcessStageRepository processStageRepository) {
        this.selectionProcessRepository = selectionProcessRepository;
        this.processStageRepository = processStageRepository;
    }

    @Transactional(readOnly = true)
    public Optional<SelectionProcess> getCurrentSelectionProcess() {
        LocalDate today = LocalDate.now();
        // Use the custom query to find the active process
        return selectionProcessRepository.findCurrentSelectionProcess(today);
    }

    @Transactional(readOnly = true)
    public List<StageWeightDTO> getCurrentProcessStageWeights() {
        Optional<SelectionProcess> currentProcessOpt = getCurrentSelectionProcess();

        if (currentProcessOpt.isEmpty()) {
            return Collections.emptyList(); // Return empty list if no active process
        }

        List<ProcessStage> stages = processStageRepository.findBySelectionProcess(currentProcessOpt.get());

        // Sort by stage order and map to DTO
        return stages.stream()
                .sorted(Comparator.comparing(ProcessStage::getStageOrder))
                .map(StageWeightDTO::new)
                .collect(Collectors.toList());
    }
}
