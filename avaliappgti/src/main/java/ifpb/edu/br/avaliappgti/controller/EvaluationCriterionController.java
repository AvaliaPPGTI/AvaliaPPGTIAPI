package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.service.EvaluationCriterionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ifpb.edu.br.avaliappgti.dto.CreateSubCriterionRequestDTO;
import ifpb.edu.br.avaliappgti.dto.CreateTopLevelCriterionRequestDTO;
import ifpb.edu.br.avaliappgti.dto.EvaluationCriterionResponseDTO;
import ifpb.edu.br.avaliappgti.dto.UpdateEvaluationCriterionRequestDTO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/evaluation-criteria")
@PreAuthorize("hasRole('ROLE_COMMITTEE')")
public class EvaluationCriterionController {

    private final EvaluationCriterionService evaluationCriterionService;

    public EvaluationCriterionController(EvaluationCriterionService evaluationCriterionService) {
        this.evaluationCriterionService = evaluationCriterionService;
    }

     /**
     * Creates a new top-level evaluation criterion.
     * A top-level criterion is directly associated with a ProcessStage.
     */
    @PostMapping("/top-level")
    public ResponseEntity<EvaluationCriterionResponseDTO> createTopLevelCriterion(
            @Valid @RequestBody CreateTopLevelCriterionRequestDTO requestDTO) {
        try {
            EvaluationCriterion newCriterion = evaluationCriterionService.createTopLevelCriterion(
                requestDTO.getProcessStageId(),
                requestDTO.getDescription(),
                requestDTO.getMaximumScore(),
                requestDTO.getWeight() // Can be null for top-level if not used
            );
            return new ResponseEntity<>(new EvaluationCriterionResponseDTO(newCriterion), HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // ProcessStage not found
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Creates a new sub-criterion under an existing parent criterion.
     */
    @PostMapping("/{parentId}/sub-criterion")
    public ResponseEntity<EvaluationCriterionResponseDTO> createSubCriterion(
            @PathVariable Integer parentId,
            @Valid @RequestBody CreateSubCriterionRequestDTO requestDTO) {
        try {
            EvaluationCriterion newCriterion = evaluationCriterionService.createSubCriterion(
                    requestDTO.getDescription(),
                    requestDTO.getMaximumScore(),
                    requestDTO.getWeight(), // Weight is usually required for sub-criteria
                    parentId
            );
            return new ResponseEntity<>(new EvaluationCriterionResponseDTO(newCriterion), HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Parent criterion not found
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // e.g., Parent has no ProcessStage
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

     /**
     * Retrieves all top-level criteria for a given process stage,
     * including their children (sub-criteria) in a hierarchical structure.
     */
    @GetMapping("/by-process-stage/{processStageId}")
    public ResponseEntity<List<EvaluationCriterionResponseDTO>> getEvaluationCriteriaTreeByProcessStage(
            @PathVariable Integer processStageId) {
        try {
            List<EvaluationCriterion> topLevelCriteria = evaluationCriterionService.getTopLevelCriteriaByProcessStage(processStageId);
            List<EvaluationCriterionResponseDTO> responseDTOs = topLevelCriteria.stream()
                    .map(EvaluationCriterionResponseDTO::new) // Uses the recursive DTO constructor
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseDTOs);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // ProcessStage not found
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

        // --- New Endpoints ---

    /**
     * Retrieves a single evaluation criterion by its ID, with its direct children.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationCriterionResponseDTO> getEvaluationCriterionById(@PathVariable Integer id) {
        try {
            EvaluationCriterion criterion = evaluationCriterionService.getEvaluationCriterionById(id);
            return ResponseEntity.ok(new EvaluationCriterionResponseDTO(criterion));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

        /**
     * Updates an existing evaluation criterion by its ID.
     * This endpoint performs a full replacement (PUT semantics).
     * All non-null fields in the request body will be applied.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EvaluationCriterionResponseDTO> updateEvaluationCriterion(@PathVariable Integer id,
                                                                                 @Valid @RequestBody UpdateEvaluationCriterionRequestDTO requestDTO) {
        try {
            // For PUT, usually all fields are expected, but here we reuse PATCH DTO.
            // Service will update non-null fields. Client should send all fields for PUT.
            EvaluationCriterion updatedCriterion = evaluationCriterionService.updateEvaluationCriterion(id, requestDTO);
            return ResponseEntity.ok(new EvaluationCriterionResponseDTO(updatedCriterion));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // e.g., trying to set weight on top-level
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Partially updates an existing evaluation criterion by its ID (PATCH semantics).
     * Only the fields provided in the request body will be updated.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<EvaluationCriterionResponseDTO> patchEvaluationCriterion(@PathVariable Integer id,
                                                                                  @RequestBody UpdateEvaluationCriterionRequestDTO requestDTO) {
        // @Valid is generally not used directly with @RequestBody for PATCH
        // if fields are truly optional (i.e., null indicates no change).
        // Validation for non-null fields can still apply if @NotNull is used on DTO fields.
        try {
            EvaluationCriterion updatedCriterion = evaluationCriterionService.updateEvaluationCriterion(id, requestDTO);
            return ResponseEntity.ok(new EvaluationCriterionResponseDTO(updatedCriterion));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
