package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.dto.CandidateApplicationDetailDTO;
import ifpb.edu.br.avaliappgti.model.Application; // You might still want to return full Applications for other endpoints
import ifpb.edu.br.avaliappgti.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<List<Application>> getAllApplications() {
        List<Application> applications = applicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/details-by-process/{processId}")
    public ResponseEntity<List<CandidateApplicationDetailDTO>> getCandidateApplicationDetailsByProcess(
            @PathVariable Integer processId) {
        try {
            List<CandidateApplicationDetailDTO> details = applicationService.getCandidateDetailsBySelectionProcessId(processId);
            if (details.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(details);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}