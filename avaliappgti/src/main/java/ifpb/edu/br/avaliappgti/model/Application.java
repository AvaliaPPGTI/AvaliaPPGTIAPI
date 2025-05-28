package ifpb.edu.br.avaliappgti.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private SelectionProcess selectionProcess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private ResearchLine researchLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private ResearchTopic researchTopic;

    @Column(name = "project_title")
    private String projectTitle;

    @Column(name = "project_path")
    private String projectPath;

    @Column(name = "application_date")
    private LocalDateTime applicationDate;

    // --- New fields for evaluation and status ---
    @Column(name = "application_status", nullable = false, length = 50)
    private String applicationStatus = "Pending";

    @Column(name = "final_score", precision = 5, scale = 2)
    private BigDecimal finalScore;

    @Column(name = "overall_ranking")
    private Integer overallRanking;

    @Column(name = "ranking_by_topic")
    private Integer rankingByTopic;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;
    // --- End of new fields ---


    // Constructors
    public Application(Candidate candidate, SelectionProcess selectionProcess, ResearchLine researchLine, ResearchTopic researchTopic, String projectTitle, String projectPath) {
        this.candidate = candidate;
        this.selectionProcess = selectionProcess;
        this.researchLine = researchLine;
        this.researchTopic = researchTopic;
        this.projectTitle = projectTitle;
        this.projectPath = projectPath;
        this.applicationDate = LocalDateTime.now();
        this.applicationStatus = "Pending";
        this.isApproved = false;
    }

}
