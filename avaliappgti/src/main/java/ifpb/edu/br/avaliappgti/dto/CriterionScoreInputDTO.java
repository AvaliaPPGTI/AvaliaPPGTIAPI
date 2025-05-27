package ifpb.edu.br.avaliappgti.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

public class CriterionScoreInputDTO {

    @NotNull(message = "Evaluation criterion ID is required")
    private Integer evaluationCriterionId;

    @NotNull(message = "Score value is required")
    @DecimalMin(value = "0.0", message = "Score must be non-negative")
    private BigDecimal scoreValue;

    public CriterionScoreInputDTO() {}

    public CriterionScoreInputDTO(Integer evaluationCriterionId, BigDecimal scoreValue) {
        this.evaluationCriterionId = evaluationCriterionId;
        this.scoreValue = scoreValue;
    }

    public Integer getEvaluationCriterionId() {
        return evaluationCriterionId;
    }

    public void setEvaluationCriterionId(Integer evaluationCriterionId) {
        this.evaluationCriterionId = evaluationCriterionId;
    }

    public BigDecimal getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(BigDecimal scoreValue) {
        this.scoreValue = scoreValue;
    }
}