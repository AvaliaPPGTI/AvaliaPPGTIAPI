package ifpb.edu.br.avaliappgti.dto;

import ifpb.edu.br.avaliappgti.model.ProcessStage;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class StageWeightDTO {

    private Integer stageId;
    private String stageName;
    private Integer stageOrder;
    private BigDecimal stageWeight;

    public StageWeightDTO(ProcessStage processStage) {
        this.stageId = processStage.getId();
        this.stageName = processStage.getStageName();
        this.stageOrder = processStage.getStageOrder();
        this.stageWeight = processStage.getStageWeight();
    }
}
