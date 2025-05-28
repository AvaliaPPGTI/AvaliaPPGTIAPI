package ifpb.edu.br.avaliappgti.model;


import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "candidate_documents")
public class CandidateDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpf", referencedColumnName = "cpf", nullable = false)
    private Candidate candidate;

    @Column(name = "score_form")
    private String scoreForm;

    @Column(name = "diploma_certificate")
    private String diplomaCertificate;

    @Column(name = "undergraduate_transcript")
    private String undergraduateTranscript;

    @Column(name = "electoral_clearance")
    private String electoralClearance;

    @Column(name = "proof_of_residence")
    private String proofOfResidence;

    @Column(name = "military_clearance")
    private String militaryClearance;

    @Column(name = "quota_declaration_admission")
    private String quotaDeclarationAdmission;

    @Column(name = "quota_declaration_if")
    private String quotaDeclarationIf;

    @Column(name = "registration_clearance")
    private String registrationClearance;

    // Constructors
    public CandidateDocument(Candidate candidate) {
        this.candidate = candidate;
    }

}