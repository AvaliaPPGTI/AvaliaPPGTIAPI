package ifpb.edu.br.avaliappgti.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateSubCriterionRequestDTO {
    @NotBlank(message = "Criterion descriptionis required")
    private String description;

    @NotNull(message = "Maximum score is required")
    @DecimalMin(value = "0.0", message = "Maximum score must be non-negative")
    private BigDecimal maximumScore;

    @NotNull(message = "Weight is required for a sub-criterion")
    @DecimalMin(value = "0.0", message = "Weight must be non-negative")
    private BigDecimal weight; // Required for sub-criteria to contribute to parent score

    // Getters and Setters

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getMaximumScore() { return maximumScore; }
    public void setMaximumScore(BigDecimal maximumScore) { this.maximumScore = maximumScore; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
}
