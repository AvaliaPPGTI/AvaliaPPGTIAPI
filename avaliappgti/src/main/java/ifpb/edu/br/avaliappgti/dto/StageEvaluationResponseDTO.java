package ifpb.edu.br.avaliappgti.dto;

import ifpb.edu.br.avaliappgti.model.StageEvaluation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StageEvaluationResponseDTO {
    private Integer id;
    private Integer applicationId;
    private Integer processStageId;
    private Integer committeeMemberId; // Nullable
    private String applicationCandidateName; // Example: Add candidate name
    private String processStageName; // Example: Add process stage name
    private LocalDateTime evaluationDate;
    private BigDecimal totalStageScore;
    private Boolean isEliminatedInStage;

    // Constructors (optional, good for mapping)
    public StageEvaluationResponseDTO() {}

    public StageEvaluationResponseDTO(StageEvaluation stageEvaluation) {
        this.id = stageEvaluation.getId();
        this.applicationId = stageEvaluation.getApplication().getId();
        this.processStageId = stageEvaluation.getProcessStage().getId();
        if (stageEvaluation.getCommitteeMember() != null) {
            this.committeeMemberId = stageEvaluation.getCommitteeMember().getId();
        }
        // Eagerly fetch/access names for DTO
        if (stageEvaluation.getApplication() != null && stageEvaluation.getApplication().getCandidate() != null) {
            this.applicationCandidateName = stageEvaluation.getApplication().getCandidate().getName();
        }
        if (stageEvaluation.getProcessStage() != null) {
            this.processStageName = stageEvaluation.getProcessStage().getStageName();
        }
        this.evaluationDate = stageEvaluation.getEvaluationDate();
        this.totalStageScore = stageEvaluation.getTotalStageScore();
        this.isEliminatedInStage = stageEvaluation.getIsEliminatedInStage();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getApplicationId() { return applicationId; }
    public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }
    public Integer getProcessStageId() { return processStageId; }
    public void setProcessStageId(Integer processStageId) { this.processStageId = processStageId; }
    public Integer getcommitteeMemberId() { return committeeMemberId; }
    public void setcommitteeMemberId(Integer committeeMemberId) { this.committeeMemberId = committeeMemberId; }
    public String getApplicationCandidateName() { return applicationCandidateName; }
    public void setApplicationCandidateName(String applicationCandidateName) { this.applicationCandidateName = applicationCandidateName; }
    public String getProcessStageName() { return processStageName; }
    public void setProcessStageName(String processStageName) { this.processStageName = processStageName; }
    public LocalDateTime getEvaluationDate() { return evaluationDate; }
    public void setEvaluationDate(LocalDateTime evaluationDate) { this.evaluationDate = evaluationDate; }
    public BigDecimal gettotalStageScore() { return totalStageScore; }
    public void settotalStageScore(BigDecimal totalStageScore) { this.totalStageScore = totalStageScore; }
    public Boolean getIsEliminatedInStage() { return isEliminatedInStage; }
    public void setIsEliminatedInStage(Boolean isEliminatedInStage) { this.isEliminatedInStage = isEliminatedInStage; }
}
