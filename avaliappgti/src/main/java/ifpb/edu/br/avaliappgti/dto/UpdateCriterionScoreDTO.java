package ifpb.edu.br.avaliappgti.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpdateCriterionScoreDTO {

    @NotNull(message = "Score obtained is required")
    @DecimalMin(value = "0.0", message = "Score must be non-negative")
    private BigDecimal scoreObtained;

    // Constructors
    public UpdateCriterionScoreDTO() {
    }

    public UpdateCriterionScoreDTO(BigDecimal scoreObtained) {
        this.scoreObtained = scoreObtained;
    }

    // Getter and Setter
    public BigDecimal getScoreObtained() {
        return scoreObtained;
    }

    public void setScoreObtained(BigDecimal scoreObtained) {
        this.scoreObtained = scoreObtained;
    }
}