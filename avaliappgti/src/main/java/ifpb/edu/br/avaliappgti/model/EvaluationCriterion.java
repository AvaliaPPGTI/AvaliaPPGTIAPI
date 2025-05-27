package ifpb.edu.br.avaliappgti.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "evaluation_criteria")
public class EvaluationCriterion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCriterion;

    @ManyToOne
    @JoinColumn(name = "id_stage", nullable = false)
    private ProcessStage processStage;

    @Column(name = "criterion_description", nullable = false)
    private String criterionDescription;

    @Column(name = "maximum_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal maximumScore;

    @Column(name = "weight", precision = 3, scale = 2)
    private BigDecimal weight; // Nullable if not all criteria are weighted

    // Constructors
    public EvaluationCriterion(ProcessStage processStage, String criterionDescription, BigDecimal maximumScore, BigDecimal weight) {
        this.processStage = processStage;
        this.criterionDescription = criterionDescription;
        this.maximumScore = maximumScore;
        this.weight = weight;
    }

}