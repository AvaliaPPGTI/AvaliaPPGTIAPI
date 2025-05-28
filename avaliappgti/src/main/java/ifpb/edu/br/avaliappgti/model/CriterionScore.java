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
@Table(name = "criterion_scores")
public class CriterionScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_stage_evaluation", nullable = false)
    private StageEvaluation stageEvaluation;

    @ManyToOne
    @JoinColumn(name = "id_criterion", nullable = false)
    private EvaluationCriterion evaluationCriterion;

    // @DecimalMin("0.00")
    // @DecimalMax("100.00")
    @Column(name = "score_obtained", precision = 5, scale = 2)
    private BigDecimal scoreObtained;

    // Constructors
    public CriterionScore(StageEvaluation stageEvaluation, EvaluationCriterion evaluationCriterion, BigDecimal scoreObtained) {
        this.stageEvaluation = stageEvaluation;
        this.evaluationCriterion = evaluationCriterion;
        this.scoreObtained = scoreObtained;
    }

}
