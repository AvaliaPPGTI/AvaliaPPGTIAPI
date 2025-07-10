package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.dto.StageWeightDTO;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.service.SelectionProcessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
}
