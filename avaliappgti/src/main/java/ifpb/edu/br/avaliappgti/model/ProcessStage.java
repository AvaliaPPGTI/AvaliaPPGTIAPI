package ifpb.edu.br.avaliappgti.model;
import jakarta.persistence.*;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "process_stages")
public class ProcessStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_selection_process", nullable = false)
    private SelectionProcess selectionProcess;

    @Column(name = "stage_name", nullable = false)
    private String stageName;

    @Column(name = "stage_order", nullable = false)
    private Integer stageOrder;

    @Column(name = "stage_character", nullable = false, length = 50)
    private String stageCharacter; // e.g., 'Classificatory', 'Eliminatory', 'Classificatory and Eliminatory'

    @Column(name = "minimum_passing_score", precision = 5, scale = 2)
    private BigDecimal minimumPassingScore; // Nullable if not eliminatory

    @Column(name = "stage_weight", precision = 5, scale = 2)
    private BigDecimal stageWeight;

    // Constructors
    public ProcessStage(SelectionProcess selectionProcess, String stageName, Integer stageOrder, String stageCharacter, BigDecimal minimumPassingScore, BigDecimal stageWeight) {
        this.selectionProcess = selectionProcess;
        this.stageName = stageName;
        this.stageOrder = stageOrder;
        this.stageCharacter = stageCharacter;
        this.minimumPassingScore = minimumPassingScore;
        this.stageWeight = stageWeight;
    }


}