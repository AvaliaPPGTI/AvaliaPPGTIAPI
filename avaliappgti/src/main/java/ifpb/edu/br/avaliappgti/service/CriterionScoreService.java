package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.model.CriterionScore;
import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.repository.CriterionScoreRepository;
import ifpb.edu.br.avaliappgti.repository.StageEvaluationRepository;
import ifpb.edu.br.avaliappgti.repository.EvaluationCriterionRepository;
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
    public StageEvaluation saveCriteriaScoresForStageEvaluation(Integer stageEvaluationId, SaveCriterionScoresRequest request) {
        // fetch the StageEvaluation
        StageEvaluation stageEvaluation = stageEvaluationRepository.findById(stageEvaluationId)
                .orElseThrow(() -> new NoSuchElementException("Stage Evaluation not found with ID: " + stageEvaluationId));

        ProcessStage processStage = stageEvaluation.getProcessStage();
        if (processStage == null) {
            throw new IllegalStateException("StageEvaluation with ID " + stageEvaluationId + " is not linked to a ProcessStage.");
        }

        // delete existing scores for this StageEvaluation to avoid duplicates if re-evaluating
        // criterionScoreRepository.deleteByStageEvaluation(stageEvaluation);

        BigDecimal totalScore = BigDecimal.ZERO;

        for (CriterionScoreInputDTO scoreDto : request.getScores()) {
            // fetch the EvaluationCriterion
            EvaluationCriterion evaluationCriterion = evaluationCriterionRepository.findById(scoreDto.getEvaluationCriterionId())
                    .orElseThrow(() -> new NoSuchElementException("Evaluation Criterion not found with ID: " + scoreDto.getEvaluationCriterionId()));

            // ensure the criterion belongs to the correct process stage
            if (!evaluationCriterion.getProcessStage().getId().equals(processStage.getId())) {
                throw new IllegalArgumentException("Evaluation Criterion ID " + scoreDto.getEvaluationCriterionId() +
                        " does not belong to Process Stage ID " + processStage.getId() +
                        " (from Stage Evaluation ID " + stageEvaluationId + ").");
            }

            // create and save the CriterionScore
            CriterionScore criterionScore = new CriterionScore();
            criterionScore.setStageEvaluation(stageEvaluation);
            criterionScore.setEvaluationCriterion(evaluationCriterion);
            criterionScore.setScoreObtained(scoreDto.getScoreValue());
            criterionScoreRepository.save(criterionScore);

            totalScore = totalScore.add(scoreDto.getScoreValue());
        }

        // update StageEvaluation's final score and elimination status
        stageEvaluation.setTotalStageScore(totalScore);

        // determine elimination status based on minimum passing score
        if (processStage.getMinimumPassingScore() != null && totalScore.compareTo(processStage.getMinimumPassingScore()) < 0) {
            stageEvaluation.setIsEliminatedInStage(true);
        } else {
            stageEvaluation.setIsEliminatedInStage(false);
        }

        return stageEvaluationRepository.save(stageEvaluation);
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