package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.model.CriterionScore;
import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.repository.CriterionScoreRepository;
import ifpb.edu.br.avaliappgti.repository.StageEvaluationRepository;
import ifpb.edu.br.avaliappgti.repository.EvaluationCriterionRepository;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO;
import ifpb.edu.br.avaliappgti.dto.CriterionScoreInputDTO;
import ifpb.edu.br.avaliappgti.dto.SaveCriterionScoresRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

        BigDecimal totalScore = BigDecimal.ZERO;

        for (CriterionScoreInputDTO scoreDto : request.getScores()) {
            EvaluationCriterion evaluationCriterion = evaluationCriterionRepository.findById(scoreDto.getEvaluationCriterionId())
                    .orElseThrow(() -> new NoSuchElementException("Evaluation Criterion not found with ID: " + scoreDto.getEvaluationCriterionId()));

            if (!evaluationCriterion.getProcessStage().getId().equals(processStage.getId())) {
                throw new IllegalArgumentException("Evaluation Criterion ID " + scoreDto.getEvaluationCriterionId() +
                        " does not belong to Process Stage ID " + processStage.getId() +
                        " (from Stage Evaluation ID " + stageEvaluationId + ").");
            }

            Optional<CriterionScore> existingScore = criterionScoreRepository.findByStageEvaluationAndEvaluationCriterion(stageEvaluation, evaluationCriterion);
            if (existingScore.isPresent()) {
                CriterionScore scoreToUpdate = existingScore.get();
                scoreToUpdate.setScoreObtained(scoreDto.getScoreValue());
                criterionScoreRepository.save(scoreToUpdate);
            } else {
                CriterionScore newScore = new CriterionScore();
                newScore.setStageEvaluation(stageEvaluation);
                newScore.setEvaluationCriterion(evaluationCriterion);
                newScore.setScoreObtained(scoreDto.getScoreValue());
                criterionScoreRepository.save(newScore);
            }
            totalScore = totalScore.add(scoreDto.getScoreValue());
        }

        stageEvaluation.setTotalStageScore(totalScore);
        if (processStage.getMinimumPassingScore() != null && totalScore.compareTo(processStage.getMinimumPassingScore()) < 0) {
            stageEvaluation.setIsEliminatedInStage(true);
        } else {
            stageEvaluation.setIsEliminatedInStage(false);
        }

        // Save the updated StageEvaluation entity
        StageEvaluation updatedStageEvaluation = stageEvaluationRepository.save(stageEvaluation);

        // Convert the updated entity to DTO before returning
        return new StageEvaluationResponseDTO(updatedStageEvaluation);
    }

    @Transactional(readOnly = true)
    public Optional<CriterionScore> getCriterionScoreById(Integer id) {
        return criterionScoreRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<CriterionScore> getScoresByStageEvaluation(Integer stageEvaluationId) {
        StageEvaluation stageEvaluation = stageEvaluationRepository.findById(stageEvaluationId)
                .orElseThrow(() -> new NoSuchElementException("Stage Evaluation not found with ID: " + stageEvaluationId));
        return criterionScoreRepository.findByStageEvaluation(stageEvaluation);
    }
}