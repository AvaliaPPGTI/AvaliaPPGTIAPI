package ifpb.edu.br.avaliappgti.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// TODO: FIX
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "approved_applications")
public class ApprovedApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idApplication;

    @ManyToOne
    @JoinColumn(name = "id_applications_verification", nullable = false)
    private ApplicationVerification idApplicationVerification;

    @Column(name = "final_score", precision = 5, scale = 2)
    private BigDecimal finalScore;

    @Column(name = "overall_ranking")
    private Integer overallRanking;

    @Column(name = "ranking_by_topic")
    private Integer rankingByTopic;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;

    // Constructors
    public ApprovedApplication() {
    }

    public ApprovedApplication(ApplicationVerification applicationVerification) {
        this.idApplicationVerification = applicationVerification;
    }

}