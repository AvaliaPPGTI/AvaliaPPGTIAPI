package ifpb.edu.br.avaliappgti.model;


import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "research_topics")
public class ResearchTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private ResearchLine researchLine;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "vacancies", nullable = false)
    private Integer vacancies;

    // Constructors
    public ResearchTopic(ResearchLine researchLine, String name, Integer vacancies) {
        this.researchLine = researchLine;
        this.name = name;
        this.vacancies = vacancies;
    }

}