package ifpb.edu.br.avaliappgti.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "research_lines")
public class ResearchLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", nullable = false)
    private SelectionProcess selectionProcess; // Reference to SelectionProcess

    @Column(name = "name", nullable = false)
    private String name;

    // Constructors
    public ResearchLine(SelectionProcess selectionProcess, String name) {
        this.selectionProcess = selectionProcess;
        this.name = name;
    }

}