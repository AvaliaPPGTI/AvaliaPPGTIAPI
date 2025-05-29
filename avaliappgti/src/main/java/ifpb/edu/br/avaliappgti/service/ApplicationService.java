package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.model.Application;
import ifpb.edu.br.avaliappgti.model.Candidate;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.model.ApplicationVerification;
import ifpb.edu.br.avaliappgti.model.ResearchTopic;

import ifpb.edu.br.avaliappgti.repository.ApplicationRepository;
import ifpb.edu.br.avaliappgti.repository.SelectionProcessRepository;
import ifpb.edu.br.avaliappgti.repository.ApplicationVerificationRepository;
import ifpb.edu.br.avaliappgti.repository.ResearchTopicRepository;
import ifpb.edu.br.avaliappgti.repository.CandidateRepository;

import ifpb.edu.br.avaliappgti.dto.CandidateApplicationDetailDTO;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final SelectionProcessRepository selectionProcessRepository;
    private final ApplicationVerificationRepository applicationVerificationRepository;
    private final ResearchTopicRepository researchTopicRepository;
    private final CandidateRepository candidateRepository;


    public ApplicationService(ApplicationRepository applicationRepository,
                              SelectionProcessRepository selectionProcessRepository, 
                              ApplicationVerificationRepository applicationVerificationRepository,
                              ResearchTopicRepository researchTopicRepository,
                              CandidateRepository candidateRepository) {
        this.applicationRepository = applicationRepository;
        this.selectionProcessRepository = selectionProcessRepository;
        this.applicationVerificationRepository = applicationVerificationRepository;
        this.researchTopicRepository = researchTopicRepository;
        this.candidateRepository = candidateRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Application> getApplicationByCandidateId(Integer candidateId) {
        return applicationRepository.findByCandidateId(candidateId);
    }

    @Transactional(readOnly = true)
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Candidate> getHomologatedCandidates() {
        // find all ApplicationVerification records with finalStatus = 1
        List<ApplicationVerification> homologatedVerifications = applicationVerificationRepository.findByFinalStatus(1);

        // extract the unique Candidates from these verifications
        // Use a Set to automatically handle uniqueness, as a candidate might theoretically
        // have multiple applications/verifications with status 1 (though less common).
        Set<Candidate> uniqueCandidates = new HashSet<>();
        for (ApplicationVerification verification : homologatedVerifications) {
            if (verification.getApplication() != null && verification.getApplication().getCandidate() != null) {
                uniqueCandidates.add(verification.getApplication().getCandidate());
            }
        }
        return uniqueCandidates.stream().collect(Collectors.toList());
    }

    // @Transactional(readOnly = true)
    // public List<Candidate> getHomologatedCandidatesBySelectionProcessId(Integer processId) {
    //     SelectionProcess selectionProcess = selectionProcessRepository.findById(processId)
    //         .orElseThrow(() -> new NoSuchElementException("Selection Process not found with ID: " + processId));

    //     List<Application> applicationsInProcess = applicationRepository.findBySelectionProcess(selectionProcess);

    //     Set<Candidate> homologatedCandidates = new HashSet<>();
    //     for (Application app : applicationsInProcess) {
    //         // Fetch verification for each application (N+1 query issue if not careful with fetch types)
    //         // Or ideally, create a custom query in ApplicationVerificationRepository with joins
    //         // e.g., @Query("SELECT av FROM ApplicationVerification av JOIN FETCH av.application a JOIN FETCH a.candidate c WHERE av.finalStatus = 1 AND a.selectionProcess = :selectionProcess")
    //         // List<ApplicationVerification> verifications = applicationVerificationRepository.findByApplicationInAndFinalStatus(applicationsInProcess, 1);
    //         // For now, assuming default EAGER fetch or that ApplicationVerification is fetched on demand
    //         ApplicationVerification verification = applicationVerificationRepository.findByApplication(app).orElse(null);
    //         if (verification != null && verification.getFinalStatus() != null && verification.getFinalStatus() == 1) {
    //             if (app.getCandidate() != null) {
    //                 homologatedCandidates.add(app.getCandidate());
    //             }
    //         }
    //     }
    //     return homologatedCandidates.stream().collect(Collectors.toList());
    // }

    @Transactional(readOnly = true)
    public List<Candidate> getHomologatedCandidatesBySelectionProcessId(Integer processId) {
        SelectionProcess selectionProcess = selectionProcessRepository.findById(processId)
            .orElseThrow(() -> new NoSuchElementException("Selection Process not found with ID: " + processId));

        return applicationVerificationRepository.findCandidatesByVerificationStatusAndSelectionProcess(1, selectionProcess);
    }

    @Transactional(readOnly = true)
    public List<CandidateApplicationDetailDTO> getCandidateDetailsBySelectionProcessId(Integer processId) {
        // 1. Find the SelectionProcess first
        SelectionProcess selectionProcess = selectionProcessRepository.findById(processId)
                .orElseThrow(() -> new NoSuchElementException("Selection Process not found with ID: " + processId));

        // 2. Get all Applications for this SelectionProcess
        List<Application> applications = applicationRepository.findBySelectionProcess(selectionProcess);

        // 3. Map Applications to CandidateApplicationDetailDTOs
        return applications.stream()
                .map(app -> {
                    String candidateName = (app.getCandidate() != null) ? app.getCandidate().getName() : "N/A Candidate";
                    Integer candidateId = (app.getCandidate() != null) ? app.getCandidate().getId() : 0;
                    String researchTopicName = (app.getResearchTopic() != null) ? app.getResearchTopic().getName() : "N/A Topic";
                    String researchLineName = (app.getResearchTopic() != null && app.getResearchTopic().getResearchLine() != null)
                            ? app.getResearchTopic().getResearchLine().getName() : "N/A Line";

                    return new CandidateApplicationDetailDTO(
                            candidateName,
                            researchTopicName,
                            researchLineName,
                            app.getId(),
                            candidateId
                    );
                })
                .collect(Collectors.toList());
    }

    // Get candidates whose applications are homologated for a given research topic
    @Transactional(readOnly = true)
    public List<Candidate> getHomologatedCandidatesByResearchTopic(Integer researchTopicId) {
        // Optional: Verify if research topic exists, though the query will return empty if it doesn't
        boolean researchTopicExists = researchTopicRepository.existsById(researchTopicId);
        if (!researchTopicExists) {
            throw new NoSuchElementException("Research Topic not found with ID: " + researchTopicId);
        }

        // Call the custom repository method
        return applicationRepository.findHomologatedCandidatesByResearchTopicId(researchTopicId);
    }

    // NEW METHOD: Get a specific application by Candidate ID and Research Topic ID
    @Transactional(readOnly = true)
    public Optional<Application> getApplicationByCandidateAndResearchTopic(
            Integer candidateId,
            Integer researchTopicId) {

        // fetch the Candidate entity
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new NoSuchElementException("Candidate not found with ID: " + candidateId));

        // fetch the ResearchTopic entity
        ResearchTopic researchTopic = researchTopicRepository.findById(researchTopicId)
                .orElseThrow(() -> new NoSuchElementException("Research Topic not found with ID: " + researchTopicId));

        // ue the derived query to find the application
        return applicationRepository.findByCandidateAndResearchTopic(candidate, researchTopic);
    }
}