package ifpb.edu.br.avaliappgti.service;


import org.springframework.stereotype.Service;


import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import ifpb.edu.br.avaliappgti.model.Application;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.CommitteeMember; // Import FacultyMember
import ifpb.edu.br.avaliappgti.repository.StageEvaluationRepository;
import ifpb.edu.br.avaliappgti.repository.ApplicationRepository; // Import ApplicationRepository
import ifpb.edu.br.avaliappgti.repository.ProcessStageRepository; // Import ProcessStageRepository
import ifpb.edu.br.avaliappgti.repository.CommitteeMemberRepository; // Import FacultyMemberRepository
import ifpb.edu.br.avaliappgti.dto.StageEvaluationCreateDTO; // Import the new DTO
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate; // For default evaluationDate
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class StageEvaluationService {

    private final StageEvaluationRepository stageEvaluationRepository;
    private final ApplicationRepository applicationRepository; // Inject
    private final ProcessStageRepository processStageRepository; // Inject
    private final CommitteeMemberRepository committeeMemberRepository; // Inject

    public StageEvaluationService(StageEvaluationRepository stageEvaluationRepository,
                                  ApplicationRepository applicationRepository,
                                  ProcessStageRepository processStageRepository,
                                  CommitteeMemberRepository committeeMemberRepository) {
        this.stageEvaluationRepository = stageEvaluationRepository;
        this.applicationRepository = applicationRepository;
        this.processStageRepository = processStageRepository;
        this.committeeMemberRepository = committeeMemberRepository;
    }

    // Existing methods (e.g., getScoresByStageEvaluation)

    // NEW METHOD: Create and save a new StageEvaluation
    @Transactional
    public StageEvaluation createStageEvaluation(StageEvaluationCreateDTO createDTO) {
        // 1. Fetch dependent entities
        Application application = applicationRepository.findById(createDTO.getApplicationId())
                .orElseThrow(() -> new NoSuchElementException("Application not found with ID: " + createDTO.getApplicationId()));

        ProcessStage processStage = processStageRepository.findById(createDTO.getProcessStageId())
                .orElseThrow(() -> new NoSuchElementException("Process Stage not found with ID: " + createDTO.getProcessStageId()));

        CommitteeMember committeeMember = null;
        if (createDTO.getCommitteeMemberId() != null) {
            committeeMember = committeeMemberRepository.findById(createDTO.getCommitteeMemberId())
                    .orElseThrow(() -> new NoSuchElementException("Evaluating Faculty not found with ID: " + createDTO.getCommitteeMemberId()));
        }

        // 2. Create the StageEvaluation entity
        StageEvaluation stageEvaluation = new StageEvaluation();
        stageEvaluation.setApplication(application);
        stageEvaluation.setProcessStage(processStage);
        stageEvaluation.setCommitteeMember(committeeMember);

        // Initialize finalScore and isEliminatedInStage to default values
        stageEvaluation.setTotalStageScore(null); // Or BigDecimal.ZERO, depending on your default
        stageEvaluation.setIsEliminatedInStage(false); // Default to not eliminated

        // Optional: Check for existing evaluation for the same application and stage
        // If you only allow one evaluation per app/stage, add a unique constraint in DB
        // and/or a check here: stageEvaluationRepository.findByApplicationAndProcessStage(...)

        // 3. Save the StageEvaluation
        return stageEvaluationRepository.save(stageEvaluation);
    }

    // You might also want methods to get a single stage evaluation or update it.
    @Transactional(readOnly = true)
    public Optional<StageEvaluation> getStageEvaluationById(Integer id) {
        return stageEvaluationRepository.findById(id);
    }
}