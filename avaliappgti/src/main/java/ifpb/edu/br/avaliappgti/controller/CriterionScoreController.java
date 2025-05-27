package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import ifpb.edu.br.avaliappgti.model.CriterionScore;
import ifpb.edu.br.avaliappgti.service.CriterionScoreService;
import ifpb.edu.br.avaliappgti.dto.SaveCriterionScoresRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/criterion-scores")
public class CriterionScoreController {

    private final CriterionScoreService criterionScoreService;

    public CriterionScoreController(CriterionScoreService criterionScoreService) {
        this.criterionScoreService = criterionScoreService;
    }


    @PostMapping("/evaluate/{stageEvaluationId}")
    public ResponseEntity<StageEvaluation> evaluateStage(
            @PathVariable Integer stageEvaluationId,
            @Valid @RequestBody SaveCriterionScoresRequest request) {
        try {
            StageEvaluation updatedStageEvaluation = criterionScoreService.saveCriteriaScoresForStageEvaluation(stageEvaluationId, request);
            return ResponseEntity.ok(updatedStageEvaluation);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            System.err.println("Error saving criterion scores: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/by-stage-evaluation/{stageEvaluationId}")
    public ResponseEntity<List<CriterionScore>> getScoresByStageEvaluation(
            @PathVariable Integer stageEvaluationId) {
        try {
            List<CriterionScore> scores = criterionScoreService.getScoresByStageEvaluation(stageEvaluationId);
            if (scores.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(scores);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error fetching criterion scores: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
