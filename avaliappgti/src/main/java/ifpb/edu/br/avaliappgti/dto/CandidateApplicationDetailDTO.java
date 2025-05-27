package ifpb.edu.br.avaliappgti.dto;

import java.io.Serializable; // Good practice for DTOs

public class CandidateApplicationDetailDTO implements Serializable { // Implement Serializable for good practice

    private String candidateName;
    private String researchTopicName;
    private String researchLineName;
    private Integer applicationId;
    private Integer candidateId;

    // Constructors
    public CandidateApplicationDetailDTO() {
    }

    public CandidateApplicationDetailDTO(String candidateName, String researchTopicName, String researchLineName, Integer applicationId, Integer candidateId) {
        this.candidateName = candidateName;
        this.researchTopicName = researchTopicName;
        this.researchLineName = researchLineName;
        this.applicationId = applicationId;
        this.candidateId = candidateId;
    }

    // Getters and Setters
    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getResearchTopicName() {
        return researchTopicName;
    }

    public void setResearchTopicName(String researchTopicName) {
        this.researchTopicName = researchTopicName;
    }

    public String getResearchLineName() {
        return researchLineName;
    }

    public void setResearchLineName(String researchLineName) {
        this.researchLineName = researchLineName;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Integer candidateId) {
        this.candidateId = candidateId;
    }

    @Override
    public String toString() {
        return "CandidateApplicationDetailDTO{" +
                "candidateName='" + candidateName + '\'' +
                ", researchTopicName='" + researchTopicName + '\'' +
                ", researchLineName='" + researchLineName + '\'' +
                ", applicationId=" + applicationId + '\'' +
                ", candidateId=" + candidateId +
                '}';
    }
}