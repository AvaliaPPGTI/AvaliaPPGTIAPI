package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.model.Application;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.repository.ApplicationRepository;
import ifpb.edu.br.avaliappgti.repository.SelectionProcessRepository;
import ifpb.edu.br.avaliappgti.dto.CandidateApplicationDetailDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final SelectionProcessRepository selectionProcessRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              SelectionProcessRepository selectionProcessRepository) {
        this.applicationRepository = applicationRepository;
        this.selectionProcessRepository = selectionProcessRepository;
    }

    @Transactional(readOnly = true)
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
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
                    Integer candidateId = (app.getCandidate() != null) ? app.getCandidate().getId() : "N/A Candidate ID";
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
}