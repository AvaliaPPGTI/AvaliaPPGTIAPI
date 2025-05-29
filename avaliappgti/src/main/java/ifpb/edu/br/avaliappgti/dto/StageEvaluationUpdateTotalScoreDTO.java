package ifpb.edu.br.avaliappgti.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class StageEvaluationUpdateTotalScoreDTO {

    @NotNull(message = "Total stage score is required")
    @DecimalMin(value = "0.0", message = "Total score must be non-negative")
    // Consider adding @DecimalMax if there's an upper limit, e.g., for a 100-point scale
    // @DecimalMax(value = "100.0", message = "Total score cannot exceed 100")
    private BigDecimal totalStageScore;

    // Optional: If you want to explicitly set elimination status from the client
    // @NotNull(message = "Elimination status is required")
    // private Boolean isEliminatedInStage;

    public StageEvaluationUpdateTotalScoreDTO() {}

    public StageEvaluationUpdateTotalScoreDTO(BigDecimal totalStageScore) {
        this.totalStageScore = totalStageScore;
    }

    // Getters and Setters
    public BigDecimal getTotalStageScore() {
        return totalStageScore;
    }

    public void setTotalStageScore(BigDecimal totalStageScore) {
        this.totalStageScore = totalStageScore;
    }

    // public Boolean getIsEliminatedInStage() {
    //     return isEliminatedInStage;
    // }
    // public void setIsEliminatedInStage(Boolean isEliminatedInStage) {
    //     this.isEliminatedInStage = isEliminatedInStage;
    // }
}
