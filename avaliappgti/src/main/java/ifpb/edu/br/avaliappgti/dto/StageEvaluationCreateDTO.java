package ifpb.edu.br.avaliappgti.dto;


import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class StageEvaluationCreateDTO {

    @NotNull(message = "Application ID is required")
    private Integer applicationId;

    @NotNull(message = "Process Stage ID is required")
    private Integer processStageId;

    // Optional: Evaluating Faculty ID
    private Integer committeeMemberId;

    // Constructors
    public StageEvaluationCreateDTO() {}

    public StageEvaluationCreateDTO(Integer applicationId, Integer processStageId, Integer committeeMemberId) {
        this.applicationId = applicationId;
        this.processStageId = processStageId;
        this.committeeMemberId = committeeMemberId;
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
}