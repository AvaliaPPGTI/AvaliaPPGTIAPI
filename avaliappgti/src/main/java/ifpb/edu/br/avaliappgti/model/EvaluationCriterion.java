package ifpb.edu.br.avaliappgti.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "evaluation_criteria")
public class EvaluationCriterion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_process_stage", nullable = false)
    private ProcessStage processStage;

    @Column(name = "criterion_description", nullable = false, columnDefinition = "TEXT")
    private String criterionDescription;

    @Column(name = "maximum_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal maximumScore;

    @Column(name = "weight", precision = 3, scale = 2)
    private BigDecimal weight; // Nullable if not all criteria are weighted

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // Self-referencing FK
    private EvaluationCriterion parent = null;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC") // Or by a specific order field
    private List<EvaluationCriterion> children;

    // Helper to determine if it's a leaf node (can receive a score directly)
    @Transient // Not mapped to a database column
    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }

    // Helper to determine if it's a top-level criterion (no parent)
    @Transient // Not mapped to a database column
    public boolean isTopLevel() {
        return parent == null;
    }


    // Constructors
    public EvaluationCriterion(ProcessStage processStage, String criterionDescription, BigDecimal maximumScore, BigDecimal weight, EvaluationCriterion parent) {
        this.processStage = processStage;
        this.criterionDescription = criterionDescription;
        this.maximumScore = maximumScore;
        this.weight = weight;
        this.parent = parent;
    }

}