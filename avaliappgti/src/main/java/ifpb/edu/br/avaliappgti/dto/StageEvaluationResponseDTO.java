package ifpb.edu.br.avaliappgti.dto;

import ifpb.edu.br.avaliappgti.model.StageEvaluation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StageEvaluationResponseDTO {
    private Integer id;
    private Integer applicationId;
    private Integer processStageId;
    private Integer committeeMemberId;
    private String applicationCandidateName;
    private String processStageName;
    private LocalDateTime evaluationDate;
    private BigDecimal totalStageScore;
    private Boolean isEliminatedInStage;
    private Integer applicationSelectionProcessId;
    private String applicationSelectionProcessName;
    private String committeeMemberName;
    private String observations;

    // Constructors (optional, good for mapping)
    public StageEvaluationResponseDTO() {}

    public StageEvaluationResponseDTO(StageEvaluation entity) {
        this.id = entity.getId();
        this.evaluationDate = entity.getEvaluationDate();
        this.totalStageScore = entity.getTotalStageScore();
        this.isEliminatedInStage = entity.getIsEliminatedInStage();
        this.observations = entity.getObservations();

        if (entity.getApplication() != null) {
            this.applicationId = entity.getApplication().getId();
            // Accessing nested lazy fields to populate DTO:
            if (entity.getApplication().getCandidate() != null) {
                this.applicationCandidateName = entity.getApplication().getCandidate().getName();
            }
            if (entity.getApplication().getSelectionProcess() != null) { // Handle the new error path
                this.applicationSelectionProcessId = entity.getApplication().getSelectionProcess().getId();
                this.applicationSelectionProcessName = entity.getApplication().getSelectionProcess().getName(); // Assuming SelectionProcess has a 'name' field
            }
        }
        if (entity.getProcessStage() != null) {
            this.processStageId = entity.getProcessStage().getId();
            this.processStageName = entity.getProcessStage().getStageName();
        }
        if (entity.getCommitteeMember() != null) {
            this.committeeMemberId = entity.getCommitteeMember().getId();
            this.committeeMemberName = entity.getCommitteeMember().getName();
        }
    }

    // --- Getters and Setters for all fields ---
    // (Lombok @Getter/@Setter can generate these if you enable it for DTOs)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getApplicationId() { return applicationId; }
    public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }
    public String getApplicationCandidateName() { return applicationCandidateName; }
    public void setApplicationCandidateName(String applicationCandidateName) { this.applicationCandidateName = applicationCandidateName; }
    public Integer getApplicationSelectionProcessId() { return applicationSelectionProcessId; }
    public void setApplicationSelectionProcessId(Integer applicationSelectionProcessId) { this.applicationSelectionProcessId = applicationSelectionProcessId; }
    public String getApplicationSelectionProcessName() { return applicationSelectionProcessName; }
    public void setApplicationSelectionProcessName(String applicationSelectionProcessName) { this.applicationSelectionProcessName = applicationSelectionProcessName; }
    public Integer getProcessStageId() { return processStageId; }
    public void setProcessStageId(Integer processStageId) { this.processStageId = processStageId; }
    public String getProcessStageName() { return processStageName; }
    public void setProcessStageName(String processStageName) { this.processStageName = processStageName; }
    public Integer getCommitteeMemberId() { return committeeMemberId; }
    public void setCommitteeMemberId(Integer committeeMemberId) { this.committeeMemberId = committeeMemberId; }
    public String getCommitteeMemberName() { return committeeMemberName; }
    public void setCommitteeMemberName(String committeeMemberName) { this.committeeMemberName = committeeMemberName; }
    public LocalDateTime getEvaluationDate() { return evaluationDate; }
    public void setEvaluationDate(LocalDateTime evaluationDate) { this.evaluationDate = evaluationDate; }
    public BigDecimal getTotalStageScore() { return totalStageScore; }
    public void setTotalStageScore(BigDecimal totalStageScore) { this.totalStageScore = totalStageScore; }
    public Boolean getIsEliminatedInStage() { return isEliminatedInStage; }
    public void setIsEliminatedInStage(Boolean isEliminatedInStage) { this.isEliminatedInStage = isEliminatedInStage; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
}