package ifpb.edu.br.avaliappgti.service;


import org.springframework.stereotype.Service;


import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import ifpb.edu.br.avaliappgti.model.Application;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.CommitteeMember;
import ifpb.edu.br.avaliappgti.model.CriterionScore;
import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.repository.StageEvaluationRepository;
import ifpb.edu.br.avaliappgti.repository.ApplicationRepository;
import ifpb.edu.br.avaliappgti.repository.ProcessStageRepository;
import ifpb.edu.br.avaliappgti.repository.CommitteeMemberRepository;
import ifpb.edu.br.avaliappgti.repository.CriterionScoreRepository;
import ifpb.edu.br.avaliappgti.repository.EvaluationCriterionRepository;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationCreateDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationUpdateTotalScoreDTO;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; 
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class StageEvaluationService {

    private final StageEvaluationRepository stageEvaluationRepository;
    private final ApplicationRepository applicationRepository;
    private final ProcessStageRepository processStageRepository;
    private final CommitteeMemberRepository committeeMemberRepository;
    private final CriterionScoreRepository criterionScoreRepository;
    private final EvaluationCriterionRepository evaluationCriterionRepository;

    public StageEvaluationService(StageEvaluationRepository stageEvaluationRepository,
                                  ApplicationRepository applicationRepository,
                                  ProcessStageRepository processStageRepository,
                                  CommitteeMemberRepository committeeMemberRepository,
                                  CriterionScoreRepository criterionScoreRepository,
                                  EvaluationCriterionRepository evaluationCriterionRepository) {
        this.stageEvaluationRepository = stageEvaluationRepository;
        this.applicationRepository = applicationRepository;
        this.processStageRepository = processStageRepository;
        this.committeeMemberRepository = committeeMemberRepository;
        this.criterionScoreRepository = criterionScoreRepository;
        this.evaluationCriterionRepository = evaluationCriterionRepository;
    }


    // create and save a new StageEvaluation
    @Transactional
    public StageEvaluationResponseDTO createStageEvaluation(StageEvaluationCreateDTO createDTO) {
        // fetch dependent entities
        Application application = applicationRepository.findById(createDTO.getApplicationId())
                .orElseThrow(() -> new NoSuchElementException("Application not found with ID: " + createDTO.getApplicationId()));

        ProcessStage processStage = processStageRepository.findById(createDTO.getProcessStageId())
                .orElseThrow(() -> new NoSuchElementException("Process Stage not found with ID: " + createDTO.getProcessStageId()));

        CommitteeMember committeeMember = null;
        if (createDTO.getCommitteeMemberId() != null) {
            committeeMember = committeeMemberRepository.findById(createDTO.getCommitteeMemberId())
                    .orElseThrow(() -> new NoSuchElementException("Evaluating Faculty not found with ID: " + createDTO.getCommitteeMemberId()));
        }

        // create the StageEvaluation entity
        StageEvaluation stageEvaluation = new StageEvaluation();
        stageEvaluation.setApplication(application);
        stageEvaluation.setProcessStage(processStage);
        stageEvaluation.setCommitteeMember(committeeMember);
        stageEvaluation.setEvaluationDate(createDTO.getEvaluationDate() != null ? createDTO.getEvaluationDate() : LocalDateTime.now());
        // stageEvaluation.setIsEliminatedInStage(createDTO.getIsEliminatedInStage() != null ? createDTO.getIsEliminatedInStage() : false);


        // Initialize finalScore and isEliminatedInStage to default values
        stageEvaluation.setTotalStageScore(null); // Or BigDecimal.ZERO, depending on your default
        stageEvaluation.setIsEliminatedInStage(false); // Default to not eliminated

        // Optional: Check for existing evaluation for the same application and stage
        // If you only allow one evaluation per app/stage, add a unique constraint in DB
        // and/or a check here: stageEvaluationRepository.findByApplicationAndProcessStage(...)
        StageEvaluation savedStageEvaluation = stageEvaluationRepository.save(stageEvaluation);

        // save the StageEvaluation
        return new StageEvaluationResponseDTO(savedStageEvaluation);
    }

    @Transactional(readOnly = true)
    public Optional<StageEvaluationResponseDTO> getStageEvaluationById(Integer id) {
        return stageEvaluationRepository.findById(id)
                .map(StageEvaluationResponseDTO::new);
    }

    @Transactional
    public StageEvaluationResponseDTO updateStageTotalScore(
            Integer stageEvaluationId,
            StageEvaluationUpdateTotalScoreDTO updateDTO) {

        StageEvaluation stageEvaluation = stageEvaluationRepository.findById(stageEvaluationId)
                .orElseThrow(() -> new NoSuchElementException("Stage Evaluation not found with ID: " + stageEvaluationId));

        // Get the associated ProcessStage to determine elimination status
        ProcessStage processStage = stageEvaluation.getProcessStage();
        if (processStage == null) {
            // This is a critical consistency check; StageEvaluation should always have a ProcessStage
            throw new IllegalStateException("StageEvaluation with ID " + stageEvaluationId + " is not linked to a ProcessStage.");
        }

        // Set the total score directly from the DTO
        stageEvaluation.setTotalStageScore(updateDTO.getTotalStageScore());

        // Determine elimination status based on the new total score and minimum passing score
        if (processStage.getMinimumPassingScore() != null &&
                updateDTO.getTotalStageScore().compareTo(processStage.getMinimumPassingScore()) < 0) {
            stageEvaluation.setIsEliminatedInStage(true);
        } else {
            stageEvaluation.setIsEliminatedInStage(false);
        }

        // Optional: If you included isEliminatedInStage in the DTO and want client to control it
        // if (updateDTO.getIsEliminatedInStage() != null) {
        //     stageEvaluation.setIsEliminatedInStage(updateDTO.getIsEliminatedInStage());
        // }

        StageEvaluation updatedEntity = stageEvaluationRepository.save(stageEvaluation);

        // Convert and return the DTO to avoid serialization issues
        return new StageEvaluationResponseDTO(updatedEntity);
    }

    @Transactional(readOnly = true)
    public Optional<StageEvaluationResponseDTO> findStageEvaluationByDetails(Integer applicationId, Integer processStageId, Integer committeeMemberId) {
        return stageEvaluationRepository.findByApplicationIdAndProcessStageIdAndCommitteeMemberId(applicationId, processStageId, committeeMemberId)
                .map(StageEvaluationResponseDTO::new);
    }

       @Transactional
    public StageEvaluationResponseDTO calculateAndSaveTotalScore(Integer stageEvaluationId) {
        StageEvaluation stageEvaluation = stageEvaluationRepository.findById(stageEvaluationId)
                .orElseThrow(() -> new NoSuchElementException("Stage Evaluation not found with ID: " + stageEvaluationId));

        ProcessStage processStage = stageEvaluation.getProcessStage();
        if (processStage == null) {
            throw new IllegalStateException("StageEvaluation with ID " + stageEvaluationId + " is not linked to a ProcessStage.");
        }

        // --- Aggregation Logic ---
        BigDecimal totalStageScore = calculateAggregatedScoresAndTotal(stageEvaluation);

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

    // Helper method to orchestrate score calculation for the entire stage
    private BigDecimal calculateAggregatedScoresAndTotal(StageEvaluation stageEvaluation) {
        BigDecimal totalScoreForStage = BigDecimal.ZERO;

        List<EvaluationCriterion> topLevelCriteria = evaluationCriterionRepository.findByProcessStageAndParentIsNull(stageEvaluation.getProcessStage());
        if (topLevelCriteria.isEmpty()) {
            return BigDecimal.ZERO;
        }

        for (EvaluationCriterion topLevelCriterion : topLevelCriteria) {
            BigDecimal aggregatedCriterionScore = calculateScoreForCriterionAndChildren(topLevelCriterion, stageEvaluation);
            // Apply weight if the top-level criterion has one
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
            // If it's a parent, aggregate scores from its children
            BigDecimal aggregatedScore = BigDecimal.ZERO;
            for (EvaluationCriterion child : currentCriterion.getChildren()) {
                BigDecimal childScore = calculateScoreForCriterionAndChildren(child, stageEvaluation);
                // Apply child's weight if it exists
                if (child.getWeight() != null) {
                    aggregatedScore = aggregatedScore.add(childScore.multiply(child.getWeight()));
                } else {
                    aggregatedScore = aggregatedScore.add(childScore);
                }
            }
            return aggregatedScore;
        }
    }

}