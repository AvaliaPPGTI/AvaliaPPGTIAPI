package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import ifpb.edu.br.avaliappgti.service.StageEvaluationService;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationCreateDTO; // Import the new DTO
import jakarta.validation.Valid; // For validating the request body
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/stage-evaluations") // Base path for this controller's endpoints
public class StageEvaluationController {

    private final StageEvaluationService stageEvaluationService;

    public StageEvaluationController(StageEvaluationService stageEvaluationService) {
        this.stageEvaluationService = stageEvaluationService;
    }

    // NEW ENDPOINT: Create a new StageEvaluation
    @PostMapping
    public ResponseEntity<StageEvaluation> createStageEvaluation(@Valid @RequestBody StageEvaluationCreateDTO createDTO) {
        try {
            StageEvaluation newStageEvaluation = stageEvaluationService.createStageEvaluation(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newStageEvaluation);
        } catch (NoSuchElementException e) {
            // If Application, ProcessStage, or FacultyMember not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            System.err.println("Error creating StageEvaluation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Optional: Get a single StageEvaluation by ID
    @GetMapping("/{id}")
    public ResponseEntity<StageEvaluation> getStageEvaluationById(@PathVariable Integer id) {
        return stageEvaluationService.getStageEvaluationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // You can add other CRUD operations for StageEvaluation here
    // e.g., PUT for updates, DELETE for deletion, GET for listing
}
