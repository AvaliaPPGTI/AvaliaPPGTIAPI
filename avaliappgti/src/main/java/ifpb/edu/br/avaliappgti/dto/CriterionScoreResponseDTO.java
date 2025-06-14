package ifpb.edu.br.avaliappgti.dto;


import ifpb.edu.br.avaliappgti.model.CriterionScore;
import java.math.BigDecimal;

public class CriterionScoreResponseDTO {
    private Integer id;
    private Integer stageEvaluationId;
    private Integer evaluationCriterionId;
    private String evaluationCriterionDescription; // Description of the leaf criterion
    private BigDecimal evaluationCriterionMaximumScore; // Max score of the leaf criterion
    private BigDecimal scoreObtained;

    public CriterionScoreResponseDTO() {}

    public CriterionScoreResponseDTO(CriterionScore criterionScore) {
        this.id = criterionScore.getId();
        if (criterionScore.getStageEvaluation() != null) {
            this.stageEvaluationId = criterionScore.getStageEvaluation().getId();
        }
        if (criterionScore.getEvaluationCriterion() != null) {
            this.evaluationCriterionId = criterionScore.getEvaluationCriterion().getId();
            this.evaluationCriterionDescription = criterionScore.getEvaluationCriterion().getCriterionDescription();
            this.evaluationCriterionMaximumScore = criterionScore.getEvaluationCriterion().getMaximumScore();
        }
        this.scoreObtained = criterionScore.getScoreObtained();
    }

    public Integer getId() {
        return id;
    }

    public Integer getStageEvaluationId() {
        return stageEvaluationId;
    }

    public void setStageEvaluationId(Integer stageEvaluationId) {
        this.stageEvaluationId = stageEvaluationId;
    }

    public Integer getEvaluationCriterionId() {
        return evaluationCriterionId;
    }

    public void setEvaluationCriterionId(Integer evaluationCriterionId) {
        this.evaluationCriterionId = evaluationCriterionId;
    }

    public String getEvaluationCriterionDescription() {
        return evaluationCriterionDescription;
    }

    public void setEvaluationCriterionDescription(String evaluationCriterionDescription) {
        this.evaluationCriterionDescription = evaluationCriterionDescription;
    }

    public BigDecimal getEvaluationCriterionMaximumScore() {
        return evaluationCriterionMaximumScore;
    }

    public void setEvaluationCriterionMaximumScore(BigDecimal evaluationCriterionMaximumScore) {
        this.evaluationCriterionMaximumScore = evaluationCriterionMaximumScore;
    }

    public BigDecimal getScoreObtained() {
        return scoreObtained;
    }

    public void setScoreObtained(BigDecimal scoreObtained) {
        this.scoreObtained = scoreObtained;
    }
}