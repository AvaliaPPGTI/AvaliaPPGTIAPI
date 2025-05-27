package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.service.EvaluationCriterionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/evaluation-criteria")
public class EvaluationCriterionController {

    private final EvaluationCriterionService evaluationCriterionService;

    public EvaluationCriterionController(EvaluationCriterionService evaluationCriterionService) {
        this.evaluationCriterionService = evaluationCriterionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluationCriterion> getEvaluationCriterionById(@PathVariable Integer id) {
        return evaluationCriterionService.getEvaluationCriterionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-process/{processId}/stage/{stageId}")
    public ResponseEntity<List<EvaluationCriterion>> getCriteriaByProcessStageAndSelectionProcess(
            @PathVariable Integer processId,
            @PathVariable Integer stageId) {
        try {
            List<EvaluationCriterion> criteria = evaluationCriterionService.getCriteriaByProcessStageAndSelectionProcessId(processId, stageId);
            if (criteria.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(criteria);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            // General error handling
            System.err.println("Error fetching evaluation criteria: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<EvaluationCriterion> createEvaluationCriterion(@RequestBody EvaluationCriterion evaluationCriterion) {
        EvaluationCriterion savedCriterion = evaluationCriterionService.saveEvaluationCriterion(evaluationCriterion);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCriterion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvaluationCriterion> updateEvaluationCriterion(@PathVariable Integer id, @RequestBody EvaluationCriterion evaluationCriterion) {
        evaluationCriterion.setId(id); // Ensure ID from path is used
        if (!evaluationCriterionService.getEvaluationCriterionById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        EvaluationCriterion updatedCriterion = evaluationCriterionService.saveEvaluationCriterion(evaluationCriterion);
        return ResponseEntity.ok(updatedCriterion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluationCriterion(@PathVariable Integer id) {
        if (!evaluationCriterionService.getEvaluationCriterionById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        evaluationCriterionService.deleteEvaluationCriterion(id);
        return ResponseEntity.noContent().build();
    }
}
