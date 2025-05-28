package ifpb.edu.br.avaliappgti.service;


import org.springframework.stereotype.Service;


import ifpb.edu.br.avaliappgti.model.StageEvaluation;
import ifpb.edu.br.avaliappgti.model.Application;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.CommitteeMember;
import ifpb.edu.br.avaliappgti.repository.StageEvaluationRepository;
import ifpb.edu.br.avaliappgti.repository.ApplicationRepository;
import ifpb.edu.br.avaliappgti.repository.ProcessStageRepository;
import ifpb.edu.br.avaliappgti.repository.CommitteeMemberRepository;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationCreateDTO;
import ifpb.edu.br.avaliappgti.dto.StageEvaluationResponseDTO;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; 

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class StageEvaluationService {

    private final StageEvaluationRepository stageEvaluationRepository;
    private final ApplicationRepository applicationRepository;
    private final ProcessStageRepository processStageRepository;
    private final CommitteeMemberRepository committeeMemberRepository;

    public StageEvaluationService(StageEvaluationRepository stageEvaluationRepository,
                                  ApplicationRepository applicationRepository,
                                  ProcessStageRepository processStageRepository,
                                  CommitteeMemberRepository committeeMemberRepository) {
        this.stageEvaluationRepository = stageEvaluationRepository;
        this.applicationRepository = applicationRepository;
        this.processStageRepository = processStageRepository;
        this.committeeMemberRepository = committeeMemberRepository;
    }


    // create and save a new StageEvaluation
    @Transactional
    public StageEvaluationResponseDTO createStageEvaluation(StageEvaluationCreateDTO createDTO) {
        // fetch dependent entities
        Application application = applicationRepository.findById(createDTO.getApplicationId())
                .orElseThrow(() -> new NoSuchElementException("Application not found with ID: " + createDTO.getApplicationId()));

        ProcessStage processStage = processStageRepository.findById(createDTO.getProcessStageId())
                .orElseThrow(() -> new NoSuchElementException("Process Stage not found with ID: " + createDTO.getProcessStageId()));

        CommitteeMember committeeMember = null;
        if (createDTO.getCommitteeMemberId() != null) {
            committeeMember = committeeMemberRepository.findById(createDTO.getCommitteeMemberId())
                    .orElseThrow(() -> new NoSuchElementException("Evaluating Faculty not found with ID: " + createDTO.getCommitteeMemberId()));
        }

        // create the StageEvaluation entity
        StageEvaluation stageEvaluation = new StageEvaluation();
        stageEvaluation.setApplication(application);
        stageEvaluation.setProcessStage(processStage);
        stageEvaluation.setCommitteeMember(committeeMember);
        stageEvaluation.setEvaluationDate(createDTO.getEvaluationDate() != null ? createDTO.getEvaluationDate() : LocalDateTime.now());
        // stageEvaluation.setIsEliminatedInStage(createDTO.getIsEliminatedInStage() != null ? createDTO.getIsEliminatedInStage() : false);


        // Initialize finalScore and isEliminatedInStage to default values
        stageEvaluation.setTotalStageScore(null); // Or BigDecimal.ZERO, depending on your default
        stageEvaluation.setIsEliminatedInStage(false); // Default to not eliminated

        // Optional: Check for existing evaluation for the same application and stage
        // If you only allow one evaluation per app/stage, add a unique constraint in DB
        // and/or a check here: stageEvaluationRepository.findByApplicationAndProcessStage(...)
        StageEvaluation savedStageEvaluation = stageEvaluationRepository.save(stageEvaluation);

        // save the StageEvaluation
        return new StageEvaluationResponseDTO(savedStageEvaluation);
    }

    @Transactional(readOnly = true)
    public Optional<StageEvaluationResponseDTO> getStageEvaluationById(Integer id) {
        return stageEvaluationRepository.findById(id)
                .map(StageEvaluationResponseDTO::new);
    }
}