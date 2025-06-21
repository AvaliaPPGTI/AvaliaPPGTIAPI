package ifpb.edu.br.avaliappgti.dto;

import ifpb.edu.br.avaliappgti.model.Application;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RankedApplicationDTO {

    private Integer applicationId;
    private Integer candidateId; // Field added
    private String candidateName;
    private Integer researchLineId; // Field added
    private String researchLineName;
    private Integer researchTopicId; // Field added
    private String researchTopicName;
    private String quotaName;
    private BigDecimal finalScore;
    private Integer rankingByTopic;
    private boolean isApproved;
    private String applicationStatus;

    public RankedApplicationDTO() {
        // Empty constructor for frameworks and tests
    }

    public RankedApplicationDTO(Application application) {
        this.applicationId = application.getId();
        this.finalScore = application.getFinalScore();
        this.rankingByTopic = application.getRankingByTopic();
        this.isApproved = application.getIsApproved();
        this.applicationStatus = application.getApplicationStatus();

        if (application.getCandidate() != null) {
            this.candidateId = application.getCandidate().getId();
            this.candidateName = application.getCandidate().getName();
            if (application.getCandidate().getQuota() != null) {
                this.quotaName = application.getCandidate().getQuota().getName();
            }
        }

        if (application.getResearchLine() != null) {
            this.researchLineId = application.getResearchLine().getId();
            this.researchLineName = application.getResearchLine().getName();
        }

        if (application.getResearchTopic() != null) {
            this.researchTopicId = application.getResearchTopic().getId();
            this.researchTopicName = application.getResearchTopic().getName();
        }
    }
}