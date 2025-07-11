package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.StageWeightDTO;
import ifpb.edu.br.avaliappgti.dto.UpdateStageWeightDTO;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.repository.ProcessStageRepository;
import ifpb.edu.br.avaliappgti.repository.SelectionProcessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
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

    /**
     * Updates the weights of the stages for the currently active selection process.
     * @param weightsToUpdate A list of DTOs containing the stageId and its new weight.
     */
    @Transactional
    public void updateCurrentProcessStageWeights(List<UpdateStageWeightDTO> weightsToUpdate) {
        // 1. Find the current selection process
        SelectionProcess currentProcess = getCurrentSelectionProcess()
                .orElseThrow(() -> new NoSuchElementException("No active selection process found."));

        // 2. Business Validation: Check if the sum of weights equals 1
        BigDecimal totalWeight = weightsToUpdate.stream()
                .map(UpdateStageWeightDTO::getStageWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalWeight.compareTo(BigDecimal.ONE) != 0) {
            throw new IllegalArgumentException("The sum of all stage weights must be exactly 1.");
        }

        // 3. Fetch all stages for the current process
        List<ProcessStage> stages = processStageRepository.findBySelectionProcess(currentProcess);
        Map<Integer, ProcessStage> stageMap = stages.stream()
                .collect(Collectors.toMap(ProcessStage::getId, stage -> stage));

        // 4. Update the weight for each stage
        for (UpdateStageWeightDTO dto : weightsToUpdate) {
            ProcessStage stage = stageMap.get(dto.getStageId());
            if (stage == null) {
                // This validation ensures the provided stageId is valid for the current process
                throw new NoSuchElementException("Process Stage with ID " + dto.getStageId() + " not found in the current selection process.");
            }
            stage.setStageWeight(dto.getStageWeight());
            processStageRepository.save(stage);
        }
    }
}
