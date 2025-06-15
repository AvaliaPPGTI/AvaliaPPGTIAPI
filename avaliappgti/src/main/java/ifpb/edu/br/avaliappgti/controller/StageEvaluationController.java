package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import ifpb.edu.br.avaliappgti.service.StageEvaluationService;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationCreateDTO; 
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationUpdateTotalScoreDTO;
import jakarta.validation.Valid; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/stage-evaluations") 
public class StageEvaluationController {

    private final StageEvaluationService stageEvaluationService;

    public StageEvaluationController(StageEvaluationService stageEvaluationService) {
        this.stageEvaluationService = stageEvaluationService;
    }

    // create a new StageEvaluation
    @PostMapping
    public ResponseEntity<StageEvaluationResponseDTO> createStageEvaluation(@Valid @RequestBody StageEvaluationCreateDTO createDTO) { // Changed return type
        try {
            StageEvaluationResponseDTO newStageEvaluation = stageEvaluationService.createStageEvaluation(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newStageEvaluation);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 with no body
        } catch (Exception e) {
            System.err.println("Error creating StageEvaluation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<StageEvaluationResponseDTO> getStageEvaluationById(@PathVariable Integer id) { // Changed return type
        return stageEvaluationService.getStageEvaluationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{stageEvaluationId}/total-score")
    public ResponseEntity<StageEvaluationResponseDTO> updateStageTotalScore(
            @PathVariable Integer stageEvaluationId,
            @Valid @RequestBody StageEvaluationUpdateTotalScoreDTO updateDTO) {
        try {
            StageEvaluationResponseDTO updatedStageEvaluation = stageEvaluationService.updateStageTotalScore(stageEvaluationId, updateDTO);
            return ResponseEntity.ok(updatedStageEvaluation);
        } catch (NoSuchElementException e) {
            System.err.println("Not Found Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            System.err.println("Conflict Error: " + e.getMessage()); // e.g., ProcessStage not linked
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            System.err.println("Error updating total stage score: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Finds a StageEvaluation by applicationId, processStageId, and committeeMemberId.
     * Returns the evaluation if it exists, otherwise returns 404 Not Found with a message.
     */
    @GetMapping("/find")
    public ResponseEntity<?> findStageEvaluationByDetails(
            @RequestParam Integer applicationId,
            @RequestParam Integer processStageId,
            @RequestParam Integer committeeMemberId) {
        
        Optional<StageEvaluationResponseDTO> evaluationDTO = stageEvaluationService.findStageEvaluationByDetails(applicationId, processStageId, committeeMemberId);

        if (evaluationDTO.isPresent()) {
            return ResponseEntity.ok(evaluationDTO.get());
        } else {
            // Create a map to hold the error message for a JSON response
            Map<String, String> response = Collections.singletonMap("message", "No object found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    // add other CRUD operations for StageEvaluation here PUT for updates, DELETE for deletion, GET for listing

    /**
     * Triggers the recalculation of the total score for a given StageEvaluation.
     * This is useful to ensure the total score is consistent with all its criterion scores.
     */
    @PostMapping("/{id}/calculate-total-score")
    public ResponseEntity<StageEvaluationResponseDTO> calculateTotalScore(@PathVariable Integer id) {
        try {
            StageEvaluationResponseDTO updatedEvaluation = stageEvaluationService.calculateAndSaveTotalScore(id);
            return ResponseEntity.ok(updatedEvaluation);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            System.err.println("Error calculating total stage score: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
