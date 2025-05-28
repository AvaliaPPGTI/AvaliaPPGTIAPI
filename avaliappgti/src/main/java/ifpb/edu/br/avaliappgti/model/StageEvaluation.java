package ifpb.edu.br.avaliappgti.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime; // Use LocalDateTime for combined date and time

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stage_evaluations")
public class StageEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_application", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
    private Application application;

    @ManyToOne
    @JoinColumn(name = "id_process_stage", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
    private ProcessStage processStage;

    @Column(name = "total_stage_score", precision = 5, scale = 2)
    private BigDecimal totalStageScore; // Final score obtained by the candidate in this specific stage

    @Column(name = "is_eliminated_in_stage", nullable = false)
    private Boolean isEliminatedInStage = false;

    @Column(name = "evaluation_date", nullable = false)
    private LocalDateTime evaluationDate;

    @ManyToOne
    @JoinColumn(name = "id_committeeMember")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
    private CommitteeMember committeeMember;

    @Column(name = "observations")
    private String observations;

    // Constructors
    public StageEvaluation(Application application, ProcessStage processStage, BigDecimal totalStageScore, Boolean isEliminatedInStage, LocalDateTime evaluationDate, CommitteeMember committeeMember, String observations) {
        this.application = application;
        this.processStage = processStage;
        this.totalStageScore = totalStageScore;
        this.isEliminatedInStage = isEliminatedInStage;
        this.evaluationDate = evaluationDate;
        this.committeeMember = committeeMember;
        this.observations = observations;
    }
}
