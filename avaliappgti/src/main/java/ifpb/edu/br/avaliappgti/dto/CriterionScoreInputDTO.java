package ifpb.edu.br.avaliappgti.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

public class CriterionScoreInputDTO {

    @NotNull(message = "Evaluation criterion ID is required")
    private Integer evaluationCriterionId;

    @NotNull(message = "Score value is required")
    @DecimalMin(value = "0.0", message = "Score must be non-negative")
    private BigDecimal scoreObtained;

    public CriterionScoreInputDTO() {}

    public CriterionScoreInputDTO(Integer evaluationCriterionId, BigDecimal scoreObtained) {
        this.evaluationCriterionId = evaluationCriterionId;
        this.scoreObtained = scoreObtained;
    }

    public Integer getEvaluationCriterionId() {
        return evaluationCriterionId;
    }

    public void setEvaluationCriterionId(Integer evaluationCriterionId) {
        this.evaluationCriterionId = evaluationCriterionId;
    }

    public BigDecimal getScoreObtained() {
        return scoreObtained;
    }

    public void setScoreObtained(BigDecimal scoreObtained) {
        this.scoreObtained = scoreObtained;
    }
}