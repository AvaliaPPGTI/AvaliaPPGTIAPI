package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.service.ProcessStageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/process-stages")
public class ProcessStageController {

    private final ProcessStageService processStageService;

    public ProcessStageController(ProcessStageService processStageService) {
        this.processStageService = processStageService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessStage> getProcessStageById(@PathVariable Integer id) {
        return processStageService.getProcessStageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-process/{processId}")
    public ResponseEntity<List<ProcessStage>> getStagesBySelectionProcess(@PathVariable Integer processId) {
        try {
            List<ProcessStage> stages = processStageService.getStagesBySelectionProcessId(processId);
            if (stages.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(stages);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            System.err.println("Error fetching process stages: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<ProcessStage> createProcessStage(@RequestBody ProcessStage processStage) {
        ProcessStage savedStage = processStageService.saveProcessStage(processStage);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcessStage> updateProcessStage(@PathVariable Integer id, @RequestBody ProcessStage processStage) {
        processStage.setId(id);
        if (!processStageService.getProcessStageById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        ProcessStage updatedStage = processStageService.saveProcessStage(processStage);
        return ResponseEntity.ok(updatedStage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProcessStage(@PathVariable Integer id) {
        if (!processStageService.getProcessStageById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        processStageService.deleteProcessStage(id);
        return ResponseEntity.noContent().build();
    }
}