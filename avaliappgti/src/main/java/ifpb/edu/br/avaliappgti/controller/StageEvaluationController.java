package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import ifpb.edu.br.avaliappgti.service.StageEvaluationService;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationCreateDTO; 
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO; 
import jakarta.validation.Valid; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

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

    // add other CRUD operations for StageEvaluation here PUT for updates, DELETE for deletion, GET for listing
}
