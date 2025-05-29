package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import ifpb.edu.br.avaliappgti.model.CriterionScore;
import ifpb.edu.br.avaliappgti.service.CriterionScoreService;
import ifpb.edu.br.avaliappgti.dto.SaveCriterionScoresRequest;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO;
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
    public ResponseEntity<StageEvaluationResponseDTO> evaluateStage( // CHANGE RETURN TYPE HERE
                                                                     @PathVariable Integer stageEvaluationId,
                                                                     @Valid @RequestBody SaveCriterionScoresRequest request) {
        try {
            StageEvaluationResponseDTO updatedStageEvaluationDto = criterionScoreService.saveCriteriaScoresForStageEvaluation(stageEvaluationId, request); // Service now returns DTO
            return ResponseEntity.ok(updatedStageEvaluationDto);
        } catch (NoSuchElementException e) {
            System.err.println("Not Found Error: " + e.getMessage()); // Log specific error message
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            System.err.println("Bad Request Error: " + e.getMessage()); // Log specific error message
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IllegalStateException e) {
            System.err.println("Conflict Error: " + e.getMessage()); // Log specific error message
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            System.err.println("Error saving criterion scores: " + e.getMessage());
            e.printStackTrace(); // Always print stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
