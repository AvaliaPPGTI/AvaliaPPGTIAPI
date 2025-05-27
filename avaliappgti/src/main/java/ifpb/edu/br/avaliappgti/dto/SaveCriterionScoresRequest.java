package ifpb.edu.br.avaliappgti.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SaveCriterionScoresRequest {

    @NotNull(message = "List of scores cannot be null")
    @NotEmpty(message = "At least one score must be provided")
    @Valid // This ensures validation is applied to each item in the list
    private List<CriterionScoreInputDTO> scores;

    public SaveCriterionScoresRequest() {}

    public SaveCriterionScoresRequest(List<CriterionScoreInputDTO> scores) {
        this.scores = scores;
    }

    public List<CriterionScoreInputDTO> getScores() {
        return scores;
    }

    public void setScores(List<CriterionScoreInputDTO> scores) {
        this.scores = scores;
    }
}
