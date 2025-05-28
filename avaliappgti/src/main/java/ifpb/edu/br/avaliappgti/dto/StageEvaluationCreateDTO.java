package ifpb.edu.br.avaliappgti.dto;


import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class StageEvaluationCreateDTO {

    @NotNull(message = "Application ID is required")
    private Integer applicationId;

    @NotNull(message = "Process Stage ID is required")
    private Integer processStageId;

    // Optional: Evaluating Faculty ID
    private Integer committeeMemberId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime evaluationDate;

    // @NotNull(message = "Eliminated in Stage field required")
    // private Boolean isEliminatedInStage = false;


    // Constructors
    public StageEvaluationCreateDTO() {}

    public StageEvaluationCreateDTO(Integer applicationId, Integer processStageId, Integer committeeMemberId, LocalDateTime evaluationDate) { //, boolean isEliminatedInStage
        this.applicationId = applicationId;
        this.processStageId = processStageId;
        this.committeeMemberId = committeeMemberId;
        this.evaluationDate = evaluationDate;
        // this.isEliminatedInStage = false;
    }

    // Getters and Setters
    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getProcessStageId() {
        return processStageId;
    }

    public void setProcessStageId(Integer processStageId) {
        this.processStageId = processStageId;
    }

    public Integer getCommitteeMemberId() {
        return committeeMemberId;
    }

    public void setCommitteeMemberId(Integer committeeMemberId) {
        this.committeeMemberId = committeeMemberId;
    }

    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    // public Boolean getIsEliminatedInStage() {
    //     return isEliminatedInStage;
    // }

    // public void setIsEliminatedInStage(Boolean isEliminatedInStage) {
    //     this.isEliminatedInStage = isEliminatedInStage;
    // }


}