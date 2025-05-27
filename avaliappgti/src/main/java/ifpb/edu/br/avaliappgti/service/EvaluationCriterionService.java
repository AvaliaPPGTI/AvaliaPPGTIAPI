package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.repository.EvaluationCriterionRepository;
import ifpb.edu.br.avaliappgti.repository.ProcessStageRepository;
import ifpb.edu.br.avaliappgti.repository.SelectionProcessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EvaluationCriterionService {

    private final EvaluationCriterionRepository evaluationCriterionRepository;
    private final ProcessStageRepository processStageRepository;
    private final SelectionProcessRepository selectionProcessRepository;

    public EvaluationCriterionService(EvaluationCriterionRepository evaluationCriterionRepository,
                                      ProcessStageRepository processStageRepository,
                                      SelectionProcessRepository selectionProcessRepository) {
        this.evaluationCriterionRepository = evaluationCriterionRepository;
        this.processStageRepository = processStageRepository;
        this.selectionProcessRepository = selectionProcessRepository;
    }

    @Transactional(readOnly = true)
    public Optional<EvaluationCriterion> getEvaluationCriterionById(Integer id) {
        return evaluationCriterionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<EvaluationCriterion> getCriteriaByProcessStageAndSelectionProcessId(Integer processId, Integer stageId) {
        // verify the SelectionProcess exists
        SelectionProcess selectionProcess = selectionProcessRepository.findById(processId)
                .orElseThrow(() -> new NoSuchElementException("Selection Process not found with ID: " + processId));

        // find the ProcessStage
        ProcessStage processStage = processStageRepository.findById(stageId)
                .orElseThrow(() -> new NoSuchElementException("Process Stage not found with ID: " + stageId));

        // prevents accessing stages from other processes via a stageId.
        if (!processStage.getSelectionProcess().getId().equals(selectionProcess.getId())) {
            throw new IllegalArgumentException("Process Stage with ID " + stageId +
                    " does not belong to Selection Process with ID " + processId);
        }

        // get all EvaluationCriteria for this validated ProcessStage
        return evaluationCriterionRepository.findByProcessStage(processStage);
    }

    @Transactional
    public EvaluationCriterion saveEvaluationCriterion(EvaluationCriterion evaluationCriterion) {
        return evaluationCriterionRepository.save(evaluationCriterion);
    }

    @Transactional
    public void deleteEvaluationCriterion(Integer id) {
        evaluationCriterionRepository.deleteById(id);
    }
}