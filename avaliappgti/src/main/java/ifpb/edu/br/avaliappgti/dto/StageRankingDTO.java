package ifpb.edu.br.avaliappgti.dto;

import ifpb.edu.br.avaliappgti.model.Application;
import ifpb.edu.br.avaliappgti.model.Candidate;
import ifpb.edu.br.avaliappgti.model.StageEvaluation;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class StageRankingDTO {

    // Stage Evaluation Info
    private Integer stageEvaluationId;
    private BigDecimal totalStageScore;
    private Boolean isEliminatedInStage;

    // Process Stage Info
    private Integer processStageId;
    private String processStageName;

    // Application Info
    private Integer applicationId;

    // Candidate Info
    private Integer candidateId;
    private String candidateName;

    // Research Info
    private Integer researchLineId;
    private String researchLineName;
    private Integer researchTopicId;
    private String researchTopicName;

    // Quota Info
    private Integer quotaId;
    private String quotaName;

    public StageRankingDTO() {
        // Empty constructor for frameworks and tests
    }

    public StageRankingDTO(StageEvaluation stageEvaluation) {
        this.stageEvaluationId = stageEvaluation.getId();
        this.totalStageScore = stageEvaluation.getTotalStageScore();
        this.isEliminatedInStage = stageEvaluation.getIsEliminatedInStage();

        if (stageEvaluation.getProcessStage() != null) {
            this.processStageId = stageEvaluation.getProcessStage().getId();
            this.processStageName = stageEvaluation.getProcessStage().getStageName();
        }

        Application application = stageEvaluation.getApplication();
        if (application != null) {
            this.applicationId = application.getId();

            if (application.getResearchLine() != null) {
                this.researchLineId = application.getResearchLine().getId();
                this.researchLineName = application.getResearchLine().getName();
            }

            if (application.getResearchTopic() != null) {
                this.researchTopicId = application.getResearchTopic().getId();
                this.researchTopicName = application.getResearchTopic().getName();
            }

            Candidate candidate = application.getCandidate();
            if (candidate != null) {
                this.candidateId = candidate.getId();
                this.candidateName = candidate.getName();

                if (candidate.getQuota() != null) {
                    this.quotaId = candidate.getQuota().getId();
                    this.quotaName = candidate.getQuota().getName();
                }
            }
        }
    }
}