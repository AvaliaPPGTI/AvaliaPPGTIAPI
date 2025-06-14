package ifpb.edu.br.avaliappgti.dto;


import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class EvaluationCriterionResponseDTO {
    private Integer id;
    private String description;
    private BigDecimal maximumScore;
    private BigDecimal weight;
    private Integer processStageId; // Only for top-level criteria
    private Integer parentId;
    private List<EvaluationCriterionResponseDTO> children;
    private boolean isLeaf; // Helper for client

    // Optional: Add score obtained if you want to include it in the tree
    private BigDecimal scoreObtained; // Score for this leaf criterion (if it's a leaf)
    private BigDecimal aggregatedScore; // Aggregated score for this parent criterion (if it's a parent)


    public EvaluationCriterionResponseDTO() {}

    public EvaluationCriterionResponseDTO(EvaluationCriterion entity) {
        this.id = entity.getId();
        this.description = entity.getCriterionDescription();
        this.maximumScore = entity.getMaximumScore();
        this.weight = entity.getWeight();
        this.isLeaf = entity.isLeaf();

        if (entity.getProcessStage() != null) { // For top-level criteria
            this.processStageId = entity.getProcessStage().getId();
        }
        if (entity.getParent() != null) { // For sub-criteria
            this.parentId = entity.getParent().getId();
        }

        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            this.children = entity.getChildren().stream()
                    .map(EvaluationCriterionResponseDTO::new)
                    .collect(Collectors.toList());
        }
        // Score fields would be populated by the service after calculation
        this.scoreObtained = null; // Set in service
        this.aggregatedScore = null; // Set in service
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMaximumScore() {
        return maximumScore;
    }

    public void setMaximumScore(BigDecimal maximumScore) {
        this.maximumScore = maximumScore;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getProcessStageId() {
        return processStageId;
    }

    public void setProcessStageId(Integer processStageId) {
        this.processStageId = processStageId;
    }

    public List<EvaluationCriterionResponseDTO> getChildren() {
        return children;
    }

    public void setChildren(List<EvaluationCriterionResponseDTO> children) {
        this.children = children;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public BigDecimal getScoreObtained() {
        return scoreObtained;
    }

    public void setScoreObtained(BigDecimal scoreObtained) {
        this.scoreObtained = scoreObtained;
    }

    public BigDecimal getAggregatedScore() {
        return aggregatedScore;
    }

    public void setAggregatedScore(BigDecimal aggregatedScore) {
        this.aggregatedScore = aggregatedScore;
    }
}
