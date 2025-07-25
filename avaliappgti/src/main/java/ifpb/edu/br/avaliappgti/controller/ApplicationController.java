package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.dto.CandidateApplicationDetailDTO;
import ifpb.edu.br.avaliappgti.model.Application;
import ifpb.edu.br.avaliappgti.model.Candidate;
import ifpb.edu.br.avaliappgti.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/applications")
@PreAuthorize("hasRole('ROLE_COMMITTEE')")
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

    @GetMapping("/by-candidate/{candidateId}")
    public ResponseEntity<Application> getApplicationByCandidateId(@PathVariable Integer candidateId) {
        try {
            Optional<Application> applicationOptional = applicationService.getApplicationByCandidateId(candidateId);
            return applicationOptional
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error fetching application for candidate " + candidateId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

    @GetMapping("/homologated-candidates")
    public ResponseEntity<List<Candidate>> getHomologatedCandidates() {
        try {
            List<Candidate> candidates = applicationService.getHomologatedCandidates();
            if (candidates.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            System.err.println("Error fetching homologated candidates: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // returns all candidates homologated for a specific selection process
    @GetMapping("/by-process/{processId}/homologated-candidates")
    public ResponseEntity<List<Candidate>> getHomologatedCandidatesByProcess(@PathVariable Integer processId) {
        try {
            List<Candidate> candidates = applicationService.getHomologatedCandidatesBySelectionProcessId(processId);
            if (candidates.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }
            return ResponseEntity.ok(candidates);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            System.err.println("Error fetching homologated candidates for process " + processId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Returns candidates whose applications are homologated for a specific research topic
    @GetMapping("/homologated-candidates/by-research-topic/{researchTopicId}")
    public ResponseEntity<List<Candidate>> getHomologatedCandidatesByResearchTopic(
            @PathVariable Integer researchTopicId) {
        try {
            List<Candidate> candidates = applicationService.getHomologatedCandidatesByResearchTopic(researchTopicId);
            if (candidates.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }
            return ResponseEntity.ok(candidates);
        } catch (NoSuchElementException e) {
            // If the research topic itself was not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            System.err.println("Error fetching homologated candidates by research topic " + researchTopicId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // returns a specific application based on candidate ID and research topic ID
    // Example path: /api/applications/by-candidate/1/research-topic/101
    @GetMapping("/by-candidate/{candidateId}/research-topic/{researchTopicId}")
    public ResponseEntity<Application> getApplicationByCandidateAndResearchTopic(
            @PathVariable Integer candidateId,
            @PathVariable Integer researchTopicId) {
        try {
            Optional<Application> applicationOptional = applicationService.getApplicationByCandidateAndResearchTopic(
                    candidateId, researchTopicId);

            return applicationOptional
                    .map(ResponseEntity::ok) // If found, return 200 OK with the Application
                    .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found
        } catch (NoSuchElementException e) {
            // If Candidate or ResearchTopic itself was not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            System.err.println("Error fetching application for candidate " + candidateId +
                    " and research topic " + researchTopicId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}