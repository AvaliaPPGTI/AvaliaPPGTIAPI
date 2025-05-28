package ifpb.edu.br.avaliappgti.model;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "selection_processes")
public class SelectionProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "program", length = 100)
    private String program;

    @Column(name = "year", length = 20)
    private String year;

    @Column(name = "semester", length = 20)
    private String semester;

    @Column(name = "edital_link")
    private String editalLink;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "weight_curriculum_step", precision = 5, scale = 2)
    private BigDecimal weightCurriculumStep;

    @Column(name = "weight_pre_project_step", precision = 5, scale = 2)
    private BigDecimal weightPreProjectStep;

    @Column(name = "weight_interview_step", precision = 5, scale = 2)
    private BigDecimal weightInterviewStep;

}