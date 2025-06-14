package ifpb.edu.br.avaliappgti.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateTopLevelCriterionRequestDTO {

    @NotBlank(message = "Criterion descriptionis required")
    private String description;

    @NotNull(message = "Maximum score is required")
    @DecimalMin(value = "0.0", message = "Maximum score must be non-negative")
    private BigDecimal maximumScore;

    private BigDecimal weight; // Optional for top-level, but can be used if top-levels also contribute weighted to something else

    @NotNull(message = "Process Stage ID is required for a top-level criterion")
    private Integer processStageId;


    // Getters and Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getMaximumScore() { return maximumScore; }
    public void setMaximumScore(BigDecimal maximumScore) { this.maximumScore = maximumScore; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public Integer getProcessStageId() { return processStageId; }
    public void setProcessStageId(Integer processStageId) { this.processStageId = processStageId; }
}
