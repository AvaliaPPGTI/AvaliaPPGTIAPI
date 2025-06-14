package ifpb.edu.br.avaliappgti.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpdateCriterionScoreDTO {

    @NotNull(message = "Score value is required")
    @DecimalMin(value = "0.0", message = "Score must be non-negative")
    private BigDecimal scoreValue;

    // Constructors
    public UpdateCriterionScoreDTO() {
    }

    public UpdateCriterionScoreDTO(BigDecimal scoreValue) {
        this.scoreValue = scoreValue;
    }

    // Getter and Setter
    public BigDecimal getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(BigDecimal scoreValue) {
        this.scoreValue = scoreValue;
    }
}