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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/evaluation-criteria")
public class EvaluationCriterionController {

    private final EvaluationCriterionService evaluationCriterionService;

    public EvaluationCriterionController(EvaluationCriterionService evaluationCriterionService) {
        this.evaluationCriterionService = evaluationCriterionService;
    }

    // @GetMapping("/{id}")
    // public ResponseEntity<EvaluationCriterion> getEvaluationCriterionById(@PathVariable Integer id) {
    //     return evaluationCriterionService.getEvaluationCriterionById(id)
    //             .map(ResponseEntity::ok)
    //             .orElse(ResponseEntity.notFound().build());
    // }

    // @GetMapping("/by-process/{processId}/stage/{stageId}")
    // public ResponseEntity<List<EvaluationCriterion>> getCriteriaByProcessStageAndSelectionProcess(
    //         @PathVariable Integer processId,
    //         @PathVariable Integer stageId) {
    //     try {
    //         List<EvaluationCriterion> criteria = evaluationCriterionService.getCriteriaByProcessStageAndSelectionProcessId(processId, stageId);
    //         if (criteria.isEmpty()) {
    //             return ResponseEntity.noContent().build();
    //         }
    //         return ResponseEntity.ok(criteria);
    //     } catch (NoSuchElementException e) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    //     } catch (IllegalArgumentException e) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    //     } catch (Exception e) {
    //         // General error handling
    //         System.err.println("Error fetching evaluation criteria: " + e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    //     }
    // }

    // @PostMapping
    // public ResponseEntity<EvaluationCriterion> createEvaluationCriterion(@RequestBody EvaluationCriterion evaluationCriterion) {
    //     EvaluationCriterion savedCriterion = evaluationCriterionService.saveEvaluationCriterion(evaluationCriterion);
    //     return ResponseEntity.status(HttpStatus.CREATED).body(savedCriterion);
    // }



    // @PutMapping("/{id}")
    // public ResponseEntity<EvaluationCriterion> updateEvaluationCriterion(@PathVariable Integer id, @RequestBody EvaluationCriterion evaluationCriterion) {
    //     evaluationCriterion.setId(id); // Ensure ID from path is used
    //     if (evaluationCriterionService.getEvaluationCriterionById(id) == null) {
    //         return ResponseEntity.notFound().build();
    //     }
    //     EvaluationCriterion updatedCriterion = evaluationCriterionService.saveEvaluationCriterion(evaluationCriterion);
    //     return ResponseEntity.ok(updatedCriterion);
    // }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteEvaluationCriterion(@PathVariable Integer id) {
    //     if (evaluationCriterionService.getEvaluationCriterionById(id) == null) {
    //         return ResponseEntity.notFound().build();
    //     }
    //     evaluationCriterionService.deleteEvaluationCriterion(id);
    //     return ResponseEntity.noContent().build();
    // }




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
                requestDTO.getWeight(), // Can be null for top-level if not used
                requestDTO.getNote() // Optional note for the criterion
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
                    parentId,
                    requestDTO.getNote() // Optional note for the sub-criterion
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

    /**
     * Deletes an evaluation criterion by its ID.
     * WARNING: This can cascade delete child criteria and will fail if CriterionScores are associated
     * and database constraints prevent deletion.
     */
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteEvaluationCriterion(@PathVariable Integer id) {
    //     try {
    //         evaluationCriterionService.deleteEvaluationCriterion(id);
    //         return ResponseEntity.noContent().build(); // 204 No Content
    //     } catch (NoSuchElementException e) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    //     } catch (Exception e) {
    //         // This might catch DataIntegrityViolationException if scores prevent deletion
    //         e.printStackTrace();
    //         // Customize error response based on the exception type
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    //     }
    // }

}
