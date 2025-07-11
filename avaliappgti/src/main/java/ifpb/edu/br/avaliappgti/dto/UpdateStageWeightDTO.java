package ifpb.edu.br.avaliappgti.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateStageWeightDTO {
    @NotNull(message = "Stage ID cannot be null")
    private Integer stageId;

    @NotNull(message = "Stage weight cannot be null")
    @DecimalMin(value = "0.0", message = "Weight must be non-negative")
    @DecimalMax(value = "1.0", message = "Weight cannot be greater than 1.0")
    private BigDecimal stageWeight;
}
