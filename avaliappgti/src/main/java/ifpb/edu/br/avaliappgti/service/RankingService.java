package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.RankedApplicationDTO;
import ifpb.edu.br.avaliappgti.dto.StageRankingDTO;
import ifpb.edu.br.avaliappgti.model.*;
import ifpb.edu.br.avaliappgti.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RankingService {

    private final ApplicationRepository applicationRepository;
    private final SelectionProcessRepository selectionProcessRepository;
    private final StageEvaluationRepository stageEvaluationRepository;
    private final ProcessStageRepository processStageRepository; 
    private final ResearchLineRepository researchLineRepository;
    private final ResearchTopicRepository researchTopicRepository;

    public RankingService(ApplicationRepository applicationRepository,
                          SelectionProcessRepository selectionProcessRepository,
                          StageEvaluationRepository stageEvaluationRepository,
                          ProcessStageRepository processStageRepository,
                          ResearchLineRepository researchLineRepository,
                          ResearchTopicRepository researchTopicRepository) {
        this.applicationRepository = applicationRepository;
        this.selectionProcessRepository = selectionProcessRepository;
        this.stageEvaluationRepository = stageEvaluationRepository;
        this.processStageRepository = processStageRepository;
        this.researchLineRepository = researchLineRepository;
        this.researchTopicRepository = researchTopicRepository;
    }

    @Transactional
    public List<RankedApplicationDTO> generateRankingForProcess(Integer processId) {
        // 1. Fetch Process and Applications
        SelectionProcess process = selectionProcessRepository.findById(processId)
                .orElseThrow(() -> new NoSuchElementException("Selection Process not found with ID: " + processId));
        List<Application> applications = applicationRepository.findBySelectionProcess(process);

        // 2. Process each application to calculate final score
        for (Application app : applications) {
            calculateFinalScoreForApplication(app, process);
        }

        // 3. Group applications by research topic for ranking
        Map<ResearchTopic, List<Application>> applicationsByTopic = applications.stream()
                .filter(app -> app.getResearchTopic() != null)
                .collect(Collectors.groupingBy(Application::getResearchTopic));

        // 4. Rank candidates within each topic
        applicationsByTopic.forEach(this::rankApplicationsForTopic);

        // 5. Save all updated applications
        applicationRepository.saveAll(applications);

        // 6. Return the results as DTOs, sorted by topic and rank
        return applications.stream()
                .sorted(Comparator.comparing((Application app) -> app.getResearchTopic().getName())
                        .thenComparing(Application::getRankingByTopic, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(RankedApplicationDTO::new)
                .collect(Collectors.toList());
    }

    private void calculateFinalScoreForApplication(Application app, SelectionProcess process) {
        List<StageEvaluation> evaluations = stageEvaluationRepository.findByApplication(app);

        StageEvaluation curriculum = evaluations.stream().filter(e -> e.getProcessStage().getStageOrder() == 1).findFirst().orElse(null);
        StageEvaluation preProject = evaluations.stream().filter(e -> e.getProcessStage().getStageOrder() == 2).findFirst().orElse(null);
        StageEvaluation interview = evaluations.stream().filter(e -> e.getProcessStage().getStageOrder() == 3).findFirst().orElse(null);

        // Check elimination rules
        if ((preProject == null || preProject.getIsEliminatedInStage()) || (interview == null || interview.getIsEliminatedInStage())) {
            app.setApplicationStatus("Desclassificado");
            app.setFinalScore(null);
            app.setRankingByTopic(null);
            app.setIsApproved(false);
            return;
        }

        BigDecimal scorePC = curriculum != null ? curriculum.getTotalStageScore() : BigDecimal.ZERO;
        BigDecimal scorePP = preProject.getTotalStageScore();
        BigDecimal scorePE = interview.getTotalStageScore();

        // Final score formula: PF = PC * 0.4 + PP * 0.3 + PE * 0.3
        BigDecimal finalScore = scorePC.multiply(process.getWeightCurriculumStep())
                .add(scorePP.multiply(process.getWeightPreProjectStep()))
                .add(scorePE.multiply(process.getWeightInterviewStep()));

        app.setFinalScore(finalScore);
        app.setApplicationStatus("Classificado");
    }

    // private void rankApplicationsForTopic(ResearchTopic topic, List<Application> applications) {
    //     // Filter out disqualified candidates before ranking
    //     List<Application> rankedCandidates = applications.stream()
    //             .filter(app -> "Ranked".equals(app.getApplicationStatus()))
    //             .sorted(Comparator.comparing(Application::getFinalScore, Comparator.nullsLast(Comparator.reverseOrder())))
    //             .collect(Collectors.toList());

    //     // Note: Tie-breaking and quota logic would be implemented here.
    //     // For now, this provides a basic ranking by score.

    //     int rank = 1;
    //     for (Application app : rankedCandidates) {
    //         app.setRankingByTopic(rank);
    //         // Approve candidates if their rank is within the number of vacancies
    //         app.setIsApproved(rank <= topic.getVacancies());
    //         rank++;
    //     }

    //     // Mark remaining (non-ranked) candidates as not approved
    //     applications.stream()
    //             .filter(app -> app.getRankingByTopic() == null)
    //             .forEach(app -> app.setIsApproved(false));
    // }
    private void rankApplicationsForTopic(ResearchTopic topic, List<Application> applications) {
        
        // This comparator implements the tie-breaking rules from section 3.14
        Comparator<Application> rankingComparator = Comparator
            // Primary sort: Final Score descending
            .comparing(Application::getFinalScore, Comparator.nullsLast(Comparator.reverseOrder()))
            // Tie-breaker 1: Pre-project score descending
            .thenComparing(app -> getScoreForStage(app, 2), Comparator.nullsLast(Comparator.reverseOrder()))
            // Tie-breaker 2: Interview score descending
            .thenComparing(app -> getScoreForStage(app, 3), Comparator.nullsLast(Comparator.reverseOrder()));
            // Note: The other tie-breaker criteria from Annex I are more complex to implement
            // as they require calculating scores for specific sub-criteria of the curriculum analysis.
            // This implementation covers the main tie-breakers.

        List<Application> rankedCandidates = applications.stream()
                .filter(app -> "Classificado".equals(app.getApplicationStatus()))
                .sorted(rankingComparator)
                .collect(Collectors.toList());

        int rank = 1;
        for (Application app : rankedCandidates) {
            app.setRankingByTopic(rank);
            app.setIsApproved(rank <= topic.getVacancies());
            rank++;
        }
        
        applications.stream()
            .filter(app -> app.getRankingByTopic() == null)
            .forEach(app -> app.setIsApproved(false));
    }
    
    // Helper method to get a specific stage evaluation from a list
    private StageEvaluation getEvaluationByStageOrder(List<StageEvaluation> evaluations, int order) {
        return evaluations.stream()
                .filter(e -> e.getProcessStage().getStageOrder() == order)
                .findFirst().orElse(null);
    }

    // Helper method to safely get a score for a specific stage for the comparator
    private BigDecimal getScoreForStage(Application app, int stageOrder) {
        return stageEvaluationRepository.findByApplication(app)
                .stream()
                .filter(e -> e.getProcessStage().getStageOrder() == stageOrder)
                .findFirst()
                .map(StageEvaluation::getTotalStageScore)
                .orElse(BigDecimal.ZERO);
    }

    @Transactional
    public List<RankedApplicationDTO> getRankingForProcess(Integer processId) {
        // 1. Fetch Process and Applications
        SelectionProcess process = selectionProcessRepository.findById(processId)
                .orElseThrow(() -> new NoSuchElementException("Selection Process not found with ID: " + processId));
        List<Application> applications = applicationRepository.findBySelectionProcess(process);

        // 2. Return the results as DTOs, sorted by topic and rank
        return applications.stream()
                .sorted(Comparator.comparing((Application app) -> app.getResearchTopic() != null ? app.getResearchTopic().getName() : "")
                                  .thenComparing(Application::getRankingByTopic, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(RankedApplicationDTO::new)
                .collect(Collectors.toList());
    }
        /**
     * Retrieves a ranked list of evaluations for a specific stage of a selection process.
     */
    @Transactional(readOnly = true)
    public List<StageRankingDTO> getRankingForStage(Integer processId, Integer stageId) {
        // 1. Verify that the stage belongs to the process for request validity
        ProcessStage stage = processStageRepository.findById(stageId)
                .orElseThrow(() -> new NoSuchElementException("Process Stage not found with ID: " + stageId));

        if (!stage.getSelectionProcess().getId().equals(processId)) {
            throw new IllegalArgumentException("Process Stage with ID " + stageId + " does not belong to Selection Process with ID " + processId);
        }

        // 2. Fetch all evaluations for this specific stage
        List<StageEvaluation> evaluations = stageEvaluationRepository.findByProcessStage(stage);

        // 3. Sort the evaluations by the total stage score in descending order
        evaluations.sort(Comparator.comparing(StageEvaluation::getTotalStageScore, Comparator.nullsLast(Comparator.reverseOrder())));

        // 4. Map the sorted list to the DTO and return
        return evaluations.stream()
                .map(StageRankingDTO::new)
                .collect(Collectors.toList());
    }

        /**
     * Retrieves a ranked list of evaluations for a specific stage, filtered by research line.
     */
    @Transactional(readOnly = true)
    public List<StageRankingDTO> getRankingForStageByResearchLine(Integer processId, Integer stageId, Integer researchLineId) {
        // 1. Verify that the stage and research line belong to the process for request validity
        ProcessStage stage = processStageRepository.findById(stageId)
                .orElseThrow(() -> new NoSuchElementException("Process Stage not found with ID: " + stageId));
        if (!stage.getSelectionProcess().getId().equals(processId)) {
            throw new IllegalArgumentException("Process Stage with ID " + stageId + " does not belong to Selection Process with ID " + processId);
        }

        ResearchLine researchLine = researchLineRepository.findById(researchLineId)
                .orElseThrow(() -> new NoSuchElementException("Research Line not found with ID: " + researchLineId));
        if (!researchLine.getSelectionProcess().getId().equals(processId)) {
            throw new IllegalArgumentException("Research Line with ID " + researchLineId + " does not belong to Selection Process with ID " + processId);
        }

        // 2. Fetch all evaluations for this specific stage
        List<StageEvaluation> evaluations = stageEvaluationRepository.findByProcessStage(stage);

        // 3. Filter the evaluations by the specified research line
        List<StageEvaluation> filteredEvaluations = evaluations.stream()
                .filter(evaluation -> evaluation.getApplication() != null &&
                                      evaluation.getApplication().getResearchLine() != null &&
                                      evaluation.getApplication().getResearchLine().getId().equals(researchLineId))
                .collect(Collectors.toList());

        // 4. Sort the filtered evaluations by the total stage score in descending order
        filteredEvaluations.sort(Comparator.comparing(StageEvaluation::getTotalStageScore, Comparator.nullsLast(Comparator.reverseOrder())));

        // 5. Map the sorted, filtered list to the DTO and return
        return filteredEvaluations.stream()
                .map(StageRankingDTO::new)
                .collect(Collectors.toList());
    }

        /**
     * Retrieves a ranked list of evaluations for a specific stage, filtered by research topic.
     */
    @Transactional(readOnly = true)
    public List<StageRankingDTO> getRankingForStageByResearchTopic(Integer processId, Integer stageId, Integer researchTopicId) {
        // 1. Verify that the stage and topic belong to the process for request validity
        ProcessStage stage = processStageRepository.findById(stageId)
                .orElseThrow(() -> new NoSuchElementException("Process Stage not found with ID: " + stageId));
        if (!stage.getSelectionProcess().getId().equals(processId)) {
            throw new IllegalArgumentException("Process Stage with ID " + stageId + " does not belong to Selection Process with ID " + processId);
        }

        ResearchTopic topic = researchTopicRepository.findById(researchTopicId)
                .orElseThrow(() -> new NoSuchElementException("Research Topic not found with ID: " + researchTopicId));
        if (!topic.getResearchLine().getSelectionProcess().getId().equals(processId)) {
            throw new IllegalArgumentException("Research Topic with ID " + researchTopicId + " does not belong to Selection Process with ID " + processId);
        }

        // 2. Fetch all evaluations for this specific stage
        List<StageEvaluation> evaluations = stageEvaluationRepository.findByProcessStage(stage);

        // 3. Filter the evaluations by the specified research topic
        List<StageEvaluation> filteredEvaluations = evaluations.stream()
                .filter(evaluation -> evaluation.getApplication() != null &&
                                      evaluation.getApplication().getResearchTopic() != null &&
                                      evaluation.getApplication().getResearchTopic().getId().equals(researchTopicId))
                .collect(Collectors.toList());

        // 4. Sort the filtered evaluations by the total stage score in descending order
        filteredEvaluations.sort(Comparator.comparing(StageEvaluation::getTotalStageScore, Comparator.nullsLast(Comparator.reverseOrder())));

        // 5. Map the sorted, filtered list to the DTO and return
        return filteredEvaluations.stream()
                .map(StageRankingDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a ranked list of evaluations for a specific stage, filtered by application status.
     */
    @Transactional(readOnly = true)
    public List<StageRankingDTO> getRankingForStageByStatus(Integer processId, Integer stageId, String status) {
        // 1. Verify that the stage belongs to the process for request validity
        ProcessStage stage = processStageRepository.findById(stageId)
                .orElseThrow(() -> new NoSuchElementException("Process Stage not found with ID: " + stageId));
        if (!stage.getSelectionProcess().getId().equals(processId)) {
            throw new IllegalArgumentException("Process Stage with ID " + stageId + " does not belong to Selection Process with ID " + processId);
        }

        // 2. Fetch all evaluations for this specific stage
        List<StageEvaluation> evaluations = stageEvaluationRepository.findByProcessStage(stage);

        // 3. Filter the evaluations by the specified application status (case-insensitive)
        List<StageEvaluation> filteredEvaluations = evaluations.stream()
                .filter(evaluation -> evaluation.getApplication() != null &&
                                      status.equalsIgnoreCase(evaluation.getApplication().getApplicationStatus()))
                .collect(Collectors.toList());

        // 4. Sort the filtered evaluations by the total stage score in descending order
        filteredEvaluations.sort(Comparator.comparing(StageEvaluation::getTotalStageScore, Comparator.nullsLast(Comparator.reverseOrder())));

        // 5. Map the sorted, filtered list to the DTO and return
        return filteredEvaluations.stream()
                .map(StageRankingDTO::new)
                .collect(Collectors.toList());
    }

}