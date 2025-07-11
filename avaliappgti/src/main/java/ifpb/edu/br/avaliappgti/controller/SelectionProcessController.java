package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.dto.StageWeightDTO;
import ifpb.edu.br.avaliappgti.dto.UpdateStageWeightDTO;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.service.SelectionProcessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/selection-processes")
public class SelectionProcessController {
    private final SelectionProcessService selectionProcessService;

    public SelectionProcessController(SelectionProcessService selectionProcessService) {
        this.selectionProcessService = selectionProcessService;
    }

    /**
     * Gets the currently active selection process.
     * An active process is defined as one where today's date is between its start and end dates.
     * If multiple processes are active, the one that started most recently is returned.
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentSelectionProcess() {
        return selectionProcessService.getCurrentSelectionProcess()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Map<String, String> response = Collections.singletonMap("message", "Nenhum processo de seleção ativo encontrado no momento.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @GetMapping("/current/weights")
    public ResponseEntity<List<StageWeightDTO>> getCurrentProcessStageWeights() {
        List<StageWeightDTO> weights = selectionProcessService.getCurrentProcessStageWeights();
        if (weights.isEmpty()) {
            // This can mean either no active process or an active process with no stages defined
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(weights);
    }

    /**
     * Updates the weights for each stage of the currently active selection process.
     * The request body must be a list of all stages and their weights, and the sum of weights must be 1.
     */
    @PutMapping("/current/weights")
    public ResponseEntity<Map<String, String>> updateCurrentProcessStageWeights(@Valid @RequestBody List<UpdateStageWeightDTO> weightsToUpdate) {
        try {
            selectionProcessService.updateCurrentProcessStageWeights(weightsToUpdate);
            Map<String, String> response = Collections.singletonMap("message", "Stage weights updated successfully.");
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            Map<String, String> error = Collections.singletonMap("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = Collections.singletonMap("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = Collections.singletonMap("error", "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
