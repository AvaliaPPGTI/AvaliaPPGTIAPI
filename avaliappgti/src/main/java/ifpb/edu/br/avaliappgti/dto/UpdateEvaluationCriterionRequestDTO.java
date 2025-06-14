package ifpb.edu.br.avaliappgti.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class UpdateEvaluationCriterionRequestDTO {
    // Making fields nullable to support PATCH (partial update)
    private String description;

    @DecimalMin(value = "0.0", message = "Maximum score must be non-negative")
    private BigDecimal maximumScore;

    @DecimalMin(value = "0.0", message = "Weight must be non-negative")
    private BigDecimal weight;

    // Getters and Setters

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMaximumScore() {
        return maximumScore;
    }

    public void setMaximumScore(BigDecimal maximumScore) {
        this.maximumScore = maximumScore;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}