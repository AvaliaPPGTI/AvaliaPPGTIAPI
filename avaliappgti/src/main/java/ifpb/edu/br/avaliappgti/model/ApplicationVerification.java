package ifpb.edu.br.avaliappgti.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "applications_verification")
public class ApplicationVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application; // Reference to Application

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", nullable = false)
    private SelectionProcess selectionProcess; // Reference to SelectionProcess

    @Column(name = "academic_data_verified", nullable = false)
    private Boolean academicDataVerified = false;

    @Column(name = "score_form_verified", nullable = false)
    private Boolean scoreFormVerified = false;

    @Column(name = "diploma_certificate_verified", nullable = false)
    private Boolean diplomaCertificateVerified = false;

    @Column(name = "undergraduate_transcript_verified", nullable = false)
    private Boolean undergraduateTranscriptVerified = false;

    @Column(name = "electoral_clearance_verified", nullable = false)
    private Boolean electoralClearanceVerified = false;

    @Column(name = "proof_of_residence_verified", nullable = false)
    private Boolean proofOfResidenceVerified = false;

    @Column(name = "military_clearance_verified", nullable = false)
    private Boolean militaryClearanceVerified = false;

    @Column(name = "quota_declaration_admission_verified", nullable = false)
    private Boolean quotaDeclarationAdmissionVerified = false;

    @Column(name = "quota_declaration_if_verified", nullable = false)
    private Boolean quotaDeclarationIfVerified = false;

    @Column(name = "registration_clearance_verified", nullable = false)
    private Boolean registrationClearanceVerified = false;

    @Column(name = "final_status")
    private Integer finalStatus; // e.g., 0 for Recusado, 1 for Homologado

    // Constructors
    public ApplicationVerification(Application application, SelectionProcess selectionProcess) {
        this.application = application;
        this.selectionProcess = selectionProcess;
    }

}