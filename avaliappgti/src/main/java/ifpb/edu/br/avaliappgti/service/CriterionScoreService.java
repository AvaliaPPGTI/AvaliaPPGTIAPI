package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.model.CriterionScore;
import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.repository.CriterionScoreRepository;
import ifpb.edu.br.avaliappgti.repository.StageEvaluationRepository;
import ifpb.edu.br.avaliappgti.repository.EvaluationCriterionRepository;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO;
import ifpb.edu.br.avaliappgti.dto.UpdateCriterionScoreDTO;
import ifpb.edu.br.avaliappgti.dto.CriterionScoreInputDTO;
import ifpb.edu.br.avaliappgti.dto.SaveCriterionScoresRequest;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO; // For output
import ifpb.edu.br.avaliappgti.dto.CriterionScoreResponseDTO; // For individual score output if needed
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CriterionScoreService {

    private final CriterionScoreRepository criterionScoreRepository;
    private final StageEvaluationRepository stageEvaluationRepository;
    private final EvaluationCriterionRepository evaluationCriterionRepository;

    public CriterionScoreService(CriterionScoreRepository criterionScoreRepository,
                                 StageEvaluationRepository stageEvaluationRepository,
                                 EvaluationCriterionRepository evaluationCriterionRepository) {
        this.criterionScoreRepository = criterionScoreRepository;
        this.stageEvaluationRepository = stageEvaluationRepository;
        this.evaluationCriterionRepository = evaluationCriterionRepository;
    }

    @Transactional
    public StageEvaluationResponseDTO saveCriteriaScoresForStageEvaluation(Integer stageEvaluationId, SaveCriterionScoresRequest request) {
        // fetch the StageEvaluation (this should include application, processStage, candidate, selectionProcess via EntityGraph)
        StageEvaluation stageEvaluation = stageEvaluationRepository.findById(stageEvaluationId)
                .orElseThrow(() -> new NoSuchElementException("Stage Evaluation not found with ID: " + stageEvaluationId));

        ProcessStage processStage = stageEvaluation.getProcessStage();
        if (processStage == null) {
            throw new IllegalStateException("StageEvaluation with ID " + stageEvaluationId + " is not linked to a ProcessStage.");
        }

        List<CriterionScore> leafScores = new ArrayList<>(); // To hold scores for leaf criteria

        for (CriterionScoreInputDTO scoreDto : request.getScores()) {
            EvaluationCriterion evaluationCriterion = evaluationCriterionRepository.findById(scoreDto.getEvaluationCriterionId())
                    .orElseThrow(() -> new NoSuchElementException("Evaluation Criterion not found with ID: " + scoreDto.getEvaluationCriterionId()));

            // *** IMPORTANT VALIDATION: Ensure it's a LEAF criterion ***
            if (!evaluationCriterion.isLeaf()) {
                throw new IllegalArgumentException("Evaluation Criterion ID " + scoreDto.getEvaluationCriterionId() +
                        " is a parent criterion and cannot directly receive a score. Scores must be assigned to leaf (mini) criteria.");
            }

            // Ensure criterion belongs to the correct process stage (check its top-level parent's process stage)
            EvaluationCriterion topLevelCriterion = getTopLevelParent(evaluationCriterion);
            if (topLevelCriterion == null || !topLevelCriterion.getProcessStage().getId().equals(processStage.getId())) {
                throw new IllegalArgumentException("Evaluation Criterion ID " + scoreDto.getEvaluationCriterionId() +
                        " does not belong to Process Stage ID " + processStage.getId() +
                        " (from Stage Evaluation ID " + stageEvaluationId + ").");
            }

            // Save/Update the CriterionScore for the leaf node
            Optional<CriterionScore> existingScore = criterionScoreRepository.findByStageEvaluationAndEvaluationCriterion(stageEvaluation, evaluationCriterion);
            CriterionScore currentScore;
            if (existingScore.isPresent()) {
                currentScore = existingScore.get();
                currentScore.setScoreObtained(scoreDto.getScoreObtained());
            } else {
                currentScore = new CriterionScore(stageEvaluation, evaluationCriterion, scoreDto.getScoreObtained());
            }
            leafScores.add(criterionScoreRepository.save(currentScore));
        }

        // --- Aggregation Logic ---
        BigDecimal totalStageScore = calculateAggregatedScoresAndTotal(stageEvaluation, leafScores);

        // Update StageEvaluation's final score and elimination status
        stageEvaluation.setTotalStageScore(totalStageScore);
        if (processStage.getMinimumPassingScore() != null && totalStageScore.compareTo(processStage.getMinimumPassingScore()) < 0) {
            stageEvaluation.setIsEliminatedInStage(true);
        } else {
            stageEvaluation.setIsEliminatedInStage(false);
        }

        StageEvaluation updatedStageEvaluation = stageEvaluationRepository.save(stageEvaluation);
        return new StageEvaluationResponseDTO(updatedStageEvaluation);
    }
    // Helper method to find the top-level parent of a criterion
    private EvaluationCriterion getTopLevelParent(EvaluationCriterion criterion) {
        EvaluationCriterion current = criterion;
        while (current != null && current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }

    // This is the core aggregation logic
    // You might store aggregated scores for parent criteria in a new table
    // or calculate them on the fly. For simplicity, let's calculate total for StageEvaluation here.
    private BigDecimal calculateAggregatedScoresAndTotal(StageEvaluation stageEvaluation, List<CriterionScore> leafScores) {
        BigDecimal totalScoreForStage = BigDecimal.ZERO;

        // Group leaf scores by their top-level criteria
        List<EvaluationCriterion> topLevelCriteria = evaluationCriterionRepository.findByProcessStageAndParentIsNull(stageEvaluation.getProcessStage());
        if (topLevelCriteria.isEmpty()) {
            // Handle case where no criteria are defined for the process stage
            return BigDecimal.ZERO;
        }

        for (EvaluationCriterion topLevelCriterion : topLevelCriteria) {
            BigDecimal aggregatedCriterionScore = calculateScoreForCriterionAndChildren(topLevelCriterion, stageEvaluation);
            // If top-level criteria also have weights, apply them here
            if (topLevelCriterion.getWeight() != null) {
                totalScoreForStage = totalScoreForStage.add(aggregatedCriterionScore.multiply(topLevelCriterion.getWeight()));
            } else {
                totalScoreForStage = totalScoreForStage.add(aggregatedCriterionScore);
            }
        }
        return totalScoreForStage;
    }

    // Recursive function to calculate score for a criterion and its children
    private BigDecimal calculateScoreForCriterionAndChildren(EvaluationCriterion currentCriterion, StageEvaluation stageEvaluation) {
        if (currentCriterion.isLeaf()) {
            // If it's a leaf, find its actual score from CriterionScore
            return criterionScoreRepository.findByStageEvaluationAndEvaluationCriterion(stageEvaluation, currentCriterion)
                    .map(CriterionScore::getScoreObtained)
                    .orElse(BigDecimal.ZERO); // Return 0 if no score found for this leaf
        } else {
            BigDecimal aggregatedScore = BigDecimal.ZERO;
            for (EvaluationCriterion child : currentCriterion.getChildren()) {
                BigDecimal childScore = calculateScoreForCriterionAndChildren(child, stageEvaluation);
                if (child.getWeight() != null) {
                    aggregatedScore = aggregatedScore.add(childScore.multiply(child.getWeight()));
                } else {
                    aggregatedScore = aggregatedScore.add(childScore);
                }
            }
            // Normalize aggregated score to the parent's maximum score if needed (e.g., if child scores are not proportional to parent's max)
            // This logic depends on how you want to sum/average scores.
            return aggregatedScore;
        }
    }

    @Transactional(readOnly = true)
    public Optional<CriterionScore> getCriterionScoreById(Integer id) {
        return criterionScoreRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<CriterionScoreResponseDTO> getScoresByStageEvaluation(Integer stageEvaluationId) {
        StageEvaluation stageEvaluation = stageEvaluationRepository.findById(stageEvaluationId)
                .orElseThrow(() -> new NoSuchElementException("Stage Evaluation not found with ID: " + stageEvaluationId));

        // Fetch scores for leaf criteria
        List<CriterionScore> scores = criterionScoreRepository.findByStageEvaluation(stageEvaluation);

        // Convert entities to DTOs
        return scores.stream()
                .map(CriterionScoreResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public StageEvaluationResponseDTO updateCriterionScore(Integer criterionScoreId, UpdateCriterionScoreDTO updateDTO) {
        // 1. Find the specific score to update
        CriterionScore criterionScore = criterionScoreRepository.findById(criterionScoreId)
                .orElseThrow(() -> new NoSuchElementException("CriterionScore not found with ID: " + criterionScoreId));

        // 2. Update the score value
        criterionScore.setScoreObtained(updateDTO.getScoreObtained());
        criterionScoreRepository.save(criterionScore);

        // 3. Recalculate the total score for the entire stage
        StageEvaluation stageEvaluation = criterionScore.getStageEvaluation();
        ProcessStage processStage = stageEvaluation.getProcessStage();

        // Fetch all leaf scores for this stage evaluation to ensure correct aggregation
        List<CriterionScore> allLeafScores = criterionScoreRepository.findByStageEvaluation(stageEvaluation);
        BigDecimal totalStageScore = calculateAggregatedScoresAndTotal(stageEvaluation, allLeafScores);

        // 4. Update the StageEvaluation's total score and elimination status
        stageEvaluation.setTotalStageScore(totalStageScore);
        if (processStage.getMinimumPassingScore() != null && totalStageScore.compareTo(processStage.getMinimumPassingScore()) < 0) {
            stageEvaluation.setIsEliminatedInStage(true);
        } else {
            stageEvaluation.setIsEliminatedInStage(false);
        }

        StageEvaluation updatedStageEvaluation = stageEvaluationRepository.save(stageEvaluation);

        // 5. Return the updated parent evaluation
        return new StageEvaluationResponseDTO(updatedStageEvaluation);
    }
}