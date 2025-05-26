package ifpb.edu.br.avaliappgti.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private Candidate candidate; // Reference to Candidate

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private SelectionProcess selectionProcess; // Reference to SelectionProcess

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private ResearchLine researchLine; // Reference to ResearchLine

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private ResearchTopic researchTopic; // Reference to ResearchTopic

    @Column(name = "project_title")
    private String projectTitle;

    @Column(name = "project_path")
    private String projectPath;

    @Column(name = "application_date")
    private LocalDateTime applicationDate; // Maps to TIMESTAMP DEFAULT CURRENT_TIMESTAMP

    // Constructors
    public Application(Candidate candidate, SelectionProcess selectionProcess, ResearchLine researchLine, ResearchTopic researchTopic, String projectTitle, String projectPath) {
        this.candidate = candidate;
        this.selectionProcess = selectionProcess;
        this.researchLine = researchLine;
        this.researchTopic = researchTopic;
        this.projectTitle = projectTitle;
        this.projectPath = projectPath;
        this.applicationDate = LocalDateTime.now();
    }

}
