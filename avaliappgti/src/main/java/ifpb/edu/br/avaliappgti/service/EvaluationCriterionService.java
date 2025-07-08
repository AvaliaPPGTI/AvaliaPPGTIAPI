package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.UpdateEvaluationCriterionRequestDTO;
import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.repository.CriterionScoreRepository;
import ifpb.edu.br.avaliappgti.repository.EvaluationCriterionRepository;
import ifpb.edu.br.avaliappgti.repository.ProcessStageRepository;
import ifpb.edu.br.avaliappgti.repository.SelectionProcessRepository;
import ifpb.edu.br.avaliappgti.repository.CriterionScoreRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EvaluationCriterionService {

    private final EvaluationCriterionRepository evaluationCriterionRepository;
    private final ProcessStageRepository processStageRepository;
    private final SelectionProcessRepository selectionProcessRepository;
    private final CriterionScoreRepository criterionScoreRepository;

    public EvaluationCriterionService(EvaluationCriterionRepository evaluationCriterionRepository,
                                      ProcessStageRepository processStageRepository,
                                      SelectionProcessRepository selectionProcessRepository,
                                       CriterionScoreRepository criterionScoreRepository) {
        this.evaluationCriterionRepository = evaluationCriterionRepository;
        this.processStageRepository = processStageRepository;
        this.selectionProcessRepository = selectionProcessRepository;
        this.criterionScoreRepository = criterionScoreRepository;
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
    public EvaluationCriterion createTopLevelCriterion(Integer processStageId, String description, BigDecimal maxScore, BigDecimal weight) {
        ProcessStage processStage = processStageRepository.findById(processStageId)
                .orElseThrow(() -> new NoSuchElementException("Process Stage not found with ID: " + processStageId));
        EvaluationCriterion criterion = new EvaluationCriterion(processStage, description, maxScore, weight,null);
        return evaluationCriterionRepository.save(criterion);
    }

    // Helper method to find the top-level parent of a criterion
    private EvaluationCriterion getTopLevelParent(EvaluationCriterion criterion) {
        EvaluationCriterion current = criterion;
        while (current != null && current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }


    @Transactional
    public EvaluationCriterion createSubCriterion(String description, BigDecimal maxScore, BigDecimal weight, Integer parentId) {
        EvaluationCriterion parentCriterion = evaluationCriterionRepository.findById(parentId)
                .orElseThrow(() -> new NoSuchElementException("Parent Evaluation Criterion not found with ID: " + parentId));

        // Sub-criteria should ideally inherit the process stage from their top-level parent.
        // Traverse up to find the actual ProcessStage.
        EvaluationCriterion topLevelParent = getTopLevelParent(parentCriterion);
        if (topLevelParent == null || topLevelParent.getProcessStage() == null) {
            throw new IllegalStateException("Parent criterion (ID: " + parentId + ") does not belong to a top-level criterion with an associated Process Stage.");
        }

        EvaluationCriterion criterion = new EvaluationCriterion(topLevelParent.getProcessStage(), description, maxScore, weight, parentCriterion);
        return evaluationCriterionRepository.save(criterion);
    }

    @Transactional(readOnly = true)
    public List<EvaluationCriterion> getTopLevelCriteriaByProcessStage(Integer processStageId) {
        ProcessStage processStage = processStageRepository.findById(processStageId)
                .orElseThrow(() -> new NoSuchElementException("Process Stage not found with ID: " + processStageId));
        // Ensure that findByProcessStageAndParentIsNull has @EntityGraph for 'children' to load the tree
        return evaluationCriterionRepository.findByProcessStageAndParentIsNull(processStage);
    }

   /**
     * Retrieves a single evaluation criterion by its ID, eagerly fetching its children.
     * This is useful for displaying details of a specific criterion or a sub-tree.
     */
    @Transactional(readOnly = true)
    public EvaluationCriterion getEvaluationCriterionById(Integer id) {
        return evaluationCriterionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Evaluation Criterion not found with ID: " + id));
    }

    /**
     * Updates an existing evaluation criterion (PUT/PATCH behavior).
     * Only provided fields will be updated for PATCH. All non-null fields will be updated for PUT.
     */
    @Transactional
    public EvaluationCriterion updateEvaluationCriterion(Integer id, UpdateEvaluationCriterionRequestDTO updateDTO) {
        EvaluationCriterion existingCriterion = evaluationCriterionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Evaluation Criterion not found with ID: " + id));

        // Apply updates from DTO (handle nulls for PATCH behavior)
        if (updateDTO.getDescription() != null) {
            existingCriterion.setCriterionDescription(updateDTO.getDescription());
        }
        if (updateDTO.getMaximumScore() != null) {
            existingCriterion.setMaximumScore(updateDTO.getMaximumScore());
        }
        if (updateDTO.getWeight() != null) {
            // Only update weight if the criterion is not a top-level (top-levels usually don't have weights contributing to a parent)
            if (existingCriterion.getParent() != null) {
                 existingCriterion.setWeight(updateDTO.getWeight());
            } else {
                 // Option 1: Ignore weight update for top-level criteria silently
                 // Option 2: Throw an IllegalArgumentException if weight is provided for a top-level
                 System.out.println("Warning: Weight provided for top-level criterion ID " + id + ". Ignoring.");
            }
        }
        // Relationships (parent, processStage, children) are not updated via this method
        // as they imply structural changes, handled by createSubCriterion or dedicated methods.

        return evaluationCriterionRepository.save(existingCriterion);
    }

    /**
     * Deletes an evaluation criterion by its ID.
     * WARNING: Deleting a parent criterion will cascade and delete its children due to CascadeType.ALL + orphanRemoval.
     * Also, ensure that foreign key constraints in the database (e.g., from criterion_scores) are handled.
     * JPA will throw an exception if there are associated CriterionScores unless ON DELETE CASCADE is set
     * at the DB level, or you explicitly delete them here.
     */
    @Transactional
    public void deleteEvaluationCriterion(Integer id) {
        EvaluationCriterion criterionToDelete = evaluationCriterionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Evaluation Criterion not found with ID: " + id));

        criterionScoreRepository.deleteByEvaluationCriterion(criterionToDelete); // Add this method to CriterionScoreRepository

        evaluationCriterionRepository.delete(criterionToDelete);
    }



}