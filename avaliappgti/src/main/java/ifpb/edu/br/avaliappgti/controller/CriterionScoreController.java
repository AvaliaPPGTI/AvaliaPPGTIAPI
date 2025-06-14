package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import ifpb.edu.br.avaliappgti.model.CriterionScore;
import ifpb.edu.br.avaliappgti.service.CriterionScoreService;
import ifpb.edu.br.avaliappgti.dto.CriterionScoreResponseDTO;
import ifpb.edu.br.avaliappgti.dto.SaveCriterionScoresRequest;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO;
import ifpb.edu.br.avaliappgti.dto.UpdateCriterionScoreDTO;

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


    // @PostMapping("/evaluate/{stageEvaluationId}")
    // public ResponseEntity<StageEvaluationResponseDTO> evaluateStage( // CHANGE RETURN TYPE HERE
    //                                                                  @PathVariable Integer stageEvaluationId,
    //                                                                  @Valid @RequestBody SaveCriterionScoresRequest request) {
    //     try {
    //         StageEvaluationResponseDTO updatedStageEvaluationDto = criterionScoreService.saveCriteriaScoresForStageEvaluation(stageEvaluationId, request); // Service now returns DTO
    //         return ResponseEntity.ok(updatedStageEvaluationDto);
    //     } catch (NoSuchElementException e) {
    //         System.err.println("Not Found Error: " + e.getMessage()); // Log specific error message
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    //     } catch (IllegalArgumentException e) {
    //         System.err.println("Bad Request Error: " + e.getMessage()); // Log specific error message
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    //     } catch (IllegalStateException e) {
    //         System.err.println("Conflict Error: " + e.getMessage()); // Log specific error message
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.CONFLICT).build();
    //     } catch (Exception e) {
    //         System.err.println("Error saving criterion scores: " + e.getMessage());
    //         e.printStackTrace(); // Always print stack trace for debugging
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    //     }
    // }

    @GetMapping("/by-stage-evaluation/{stageEvaluationId}")
    public ResponseEntity<List<CriterionScoreResponseDTO>> getScoresByStageEvaluation(
            @PathVariable Integer stageEvaluationId) {
        try {
            List<CriterionScoreResponseDTO> scores = criterionScoreService.getScoresByStageEvaluation(stageEvaluationId);
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

    /**
     * Saves or updates a list of criterion scores for a specific stage evaluation.
     * The service layer will ensure that scores are only applied to leaf criteria
     * and will handle the aggregation of scores up the hierarchy.
     */
    @PostMapping("/evaluate/{stageEvaluationId}")
    public ResponseEntity<StageEvaluationResponseDTO> saveCriteriaScoresForStageEvaluation(
            @PathVariable Integer stageEvaluationId,
            @Valid @RequestBody SaveCriterionScoresRequest request) {
        try {
            StageEvaluationResponseDTO response = criterionScoreService.saveCriteriaScoresForStageEvaluation(stageEvaluationId, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            System.err.println("Not Found Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // StageEvaluation or Criterion not found
        } catch (IllegalArgumentException e) {
            System.err.println("Bad Request Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // e.g., trying to score a parent criterion, or criterion belongs to wrong stage
        } catch (IllegalStateException e) {
            System.err.println("Conflict/Invalid State Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // e.g., StageEvaluation not linked to ProcessStage
        } catch (Exception e) {
            System.err.println("Error saving criterion scores: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Other methods here for retrieving specific criterion scores,
    // though often the aggregated view via StageEvaluationController is more common.
    // Example:
    // @GetMapping("/by-stage-evaluation/{stageEvaluationId}")
    // public ResponseEntity<List<CriterionScoreResponseDTO>> getScoresByStageEvaluation(@PathVariable Integer stageEvaluationId) {
    //     try {
    //         List<CriterionScoreResponseDTO> scores = criterionScoreService.getScoresByStageEvaluation(stageEvaluationId);
    //         return ResponseEntity.ok(scores);
    //     } catch (NoSuchElementException e) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    //     }
    // }

    /**
     * Updates an existing criterion score by its ID.
     * This will also trigger a recalculation of the total score for the stage evaluation.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StageEvaluationResponseDTO> updateCriterionScore(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateCriterionScoreDTO updateDTO) {
        try {
            StageEvaluationResponseDTO updatedEvaluation = criterionScoreService.updateCriterionScore(id, updateDTO);
            return ResponseEntity.ok(updatedEvaluation);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("Error updating criterion score: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
