package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.dto.RankedApplicationDTO;
import ifpb.edu.br.avaliappgti.dto.StageRankingDTO;
import ifpb.edu.br.avaliappgti.model.*;
import ifpb.edu.br.avaliappgti.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    // @Transactional
    // public List<RankedApplicationDTO> generateRankingForProcess(Integer processId) {
    //     // 1. Fetch Process and Applications
    //     SelectionProcess process = selectionProcessRepository.findById(processId)
    //             .orElseThrow(() -> new NoSuchElementException("Selection Process not found with ID: " + processId));
    //     List<Application> applications = applicationRepository.findBySelectionProcess(process);

    //     // 2. Process each application to calculate final score
    //     for (Application app : applications) {
    //         calculateFinalScoreForApplication(app, process);
    //     }

    //     // 3. Group applications by research topic for ranking
    //     Map<ResearchTopic, List<Application>> applicationsByTopic = applications.stream()
    //             .filter(app -> app.getResearchTopic() != null)
    //             .collect(Collectors.groupingBy(Application::getResearchTopic));

    //     // 4. Rank candidates within each topic
    //     applicationsByTopic.forEach(this::rankApplicationsForTopic);

    //     // 5. Save all updated applications
    //     applicationRepository.saveAll(applications);

    //     // 6. Return the results as DTOs, sorted by topic and rank
    //     return applications.stream()
    //             .sorted(Comparator.comparing((Application app) -> app.getResearchTopic().getName())
    //                     .thenComparing(Application::getRankingByTopic, Comparator.nullsLast(Comparator.naturalOrder())))
    //             .map(RankedApplicationDTO::new)
    //             .collect(Collectors.toList());
    // }

    // private void calculateFinalScoreForApplication(Application app, SelectionProcess process) {
    //     List<StageEvaluation> evaluations = stageEvaluationRepository.findByApplication(app);

    //     StageEvaluation curriculum = evaluations.stream().filter(e -> e.getProcessStage().getStageOrder() == 1).findFirst().orElse(null);
    //     StageEvaluation preProject = evaluations.stream().filter(e -> e.getProcessStage().getStageOrder() == 2).findFirst().orElse(null);
    //     StageEvaluation interview = evaluations.stream().filter(e -> e.getProcessStage().getStageOrder() == 3).findFirst().orElse(null);

    //     // Check elimination rules
    //     if ((preProject == null || preProject.getIsEliminatedInStage()) || (interview == null || interview.getIsEliminatedInStage())) {
    //         app.setApplicationStatus("Desclassificado");
    //         app.setFinalScore(null);
    //         app.setRankingByTopic(null);
    //         app.setIsApproved(false);       
    //         return;
    //     }

    //     BigDecimal scorePC = curriculum != null ? curriculum.getTotalStageScore() : BigDecimal.ZERO;
    //     BigDecimal scorePP = preProject.getTotalStageScore();
    //     BigDecimal scorePE = interview.getTotalStageScore();

    //     // Use stageWeight from each ProcessStage
    //     BigDecimal weightPC = (curriculum != null && curriculum.getProcessStage().getStageWeight() != null)
    //         ? curriculum.getProcessStage().getStageWeight() : BigDecimal.ZERO;
    //     BigDecimal weightPP = (preProject != null && preProject.getProcessStage().getStageWeight() != null)
    //         ? preProject.getProcessStage().getStageWeight() : BigDecimal.ZERO;
    //     BigDecimal weightPE = (interview != null && interview.getProcessStage().getStageWeight() != null)
    //         ? interview.getProcessStage().getStageWeight() : BigDecimal.ZERO;

    //     // Final score formula: PF = PC * weightPC + PP * weightPP + PE * weightPE
    //     BigDecimal finalScore = scorePC.multiply(weightPC)
    //             .add(scorePP.multiply(weightPP))
    //             .add(scorePE.multiply(weightPE));

    //     app.setFinalScore(finalScore);
    //     app.setApplicationStatus("Classificado");
    // }
    @Transactional
    public List<RankedApplicationDTO> generateRankingForProcess(Integer processId) {
        // 1. Fetch Process and Applications
        SelectionProcess process = selectionProcessRepository.findById(processId)
                .orElseThrow(() -> new NoSuchElementException("Selection Process not found with ID: " + processId));
        List<Application> applications = applicationRepository.findBySelectionProcess(process);

        // 2. Calculate final score for all qualified applications
        for (Application app : applications) {
            calculateFinalScoreForApplication(app);
        }

        // 3. Initial Ranking (Phase 1)
        List<Application> preApprovedCandidates = new ArrayList<>();
        Map<ResearchTopic, List<Application>> applicationsByTopic = applications.stream()
                .filter(app -> "Ranked".equals(app.getApplicationStatus()) && app.getResearchTopic() != null)
                .collect(Collectors.groupingBy(Application::getResearchTopic));

        applicationsByTopic.forEach((topic, apps) -> {
            apps.sort(getRankingComparator());
            for (int i = 0; i < apps.size(); i++) {
                if (i < topic.getVacancies()) {
                    preApprovedCandidates.add(apps.get(i));
                }
            }
        });
        
        // 4. Quota Adjustment (Phase 2)
        List<Application> finalApprovedCandidates = adjustForQuotas(preApprovedCandidates, applicationsByTopic);

        // 5. Set final status for all applications
        finalApprovedCandidates.forEach(app -> app.setIsApproved(true));
        
        // Set ranking numbers for the final approved list
        Map<ResearchTopic, List<Application>> finalApprovedByTopic = finalApprovedCandidates.stream()
            .collect(Collectors.groupingBy(Application::getResearchTopic));
            
        finalApprovedByTopic.forEach((topic, apps) -> {
            apps.sort(getRankingComparator());
            for(int i = 0; i < apps.size(); i++){
                apps.get(i).setRankingByTopic(i + 1);
            }
        });
        
        // Ensure all other applications are marked as not approved
        applications.stream()
            .filter(app -> !finalApprovedCandidates.contains(app))
            .forEach(app -> {
                app.setIsApproved(false);
                app.setRankingByTopic(null);
            });


        applicationRepository.saveAll(applications);
        
        return applications.stream()
                .sorted(Comparator.comparing((Application app) -> app.getResearchTopic().getName())
                                  .thenComparing(Application::getRankingByTopic, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(RankedApplicationDTO::new)
                .collect(Collectors.toList());
    }

    private void calculateFinalScoreForApplication(Application app) {
        List<StageEvaluation> evaluations = stageEvaluationRepository.findByApplication(app);

        StageEvaluation curriculum = getEvaluationByStageOrder(evaluations, 1);
        StageEvaluation preProject = getEvaluationByStageOrder(evaluations, 2);
        StageEvaluation interview = getEvaluationByStageOrder(evaluations, 3);

        if ((preProject == null || preProject.getIsEliminatedInStage()) || (interview == null || interview.getIsEliminatedInStage())) {
            app.setApplicationStatus("Disqualified");
            app.setFinalScore(null);
            return;
        }

        BigDecimal scorePC = curriculum != null ? curriculum.getTotalStageScore() : BigDecimal.ZERO;
        BigDecimal scorePP = preProject.getTotalStageScore();
        BigDecimal scorePE = interview.getTotalStageScore();
        
        BigDecimal weightPC = curriculum != null ? curriculum.getProcessStage().getStageWeight() : BigDecimal.ZERO;
        BigDecimal weightPP = preProject.getProcessStage().getStageWeight();
        BigDecimal weightPE = interview.getProcessStage().getStageWeight();

        BigDecimal finalScore = scorePC.multiply(weightPC)
                                .add(scorePP.multiply(weightPP))
                                .add(scorePE.multiply(weightPE));

        app.setFinalScore(finalScore);
        app.setApplicationStatus("Ranked");
    }
    
    private List<Application> adjustForQuotas(List<Application> preApprovedCandidates, Map<ResearchTopic, List<Application>> applicationsByTopic) {
        
        // These are the defined quota targets from your rules
        final int afroIndigenousTarget = 4;
        final int pwdTarget = 1;
        final int serverTarget = 2;

        List<Application> finalApproved = new ArrayList<>(preApprovedCandidates);

        long currentAfroIndigenous = finalApproved.stream().filter(app -> isQuotaHolder(app, "Afrodescente") || isQuotaHolder(app, "Indígenas")).count();
        long currentPwd = finalApproved.stream().filter(app -> isQuotaHolder(app, "Pessoa com deficiência")).count();
        long currentServer = finalApproved.stream().filter(app -> isQuotaHolder(app, "Servidor do IFPB")).count();

        // Loop until all quota targets are met or no more swaps are possible
        while(currentAfroIndigenous < afroIndigenousTarget || currentPwd < pwdTarget || currentServer < serverTarget) {
            
            // Find the lowest-scoring, non-quota candidate from the currently approved list
            Application candidateToReplace = finalApproved.stream()
                .filter(app -> !isQuotaHolder(app))
                .min(getRankingComparator())
                .orElse(null);

            // If no non-quota candidates are left to replace, stop the process
            if (candidateToReplace == null) {
                break;
            }

            ResearchTopic topicOfReplacement = candidateToReplace.getResearchTopic();
            
            // Determine which quota type needs filling and find the best candidate from the waiting list of that topic
            Application bestQuotaCandidate = null;
            if (currentAfroIndigenous < afroIndigenousTarget) {
                bestQuotaCandidate = findBestWaitingListCandidate(applicationsByTopic.get(topicOfReplacement), finalApproved, "Afrodescente", "Indígenas");
            }
            if (currentPwd < pwdTarget && bestQuotaCandidate == null) {
                bestQuotaCandidate = findBestWaitingListCandidate(applicationsByTopic.get(topicOfReplacement), finalApproved, "Pessoa com deficiência");
            }
            if (currentServer < serverTarget && bestQuotaCandidate == null) {
                bestQuotaCandidate = findBestWaitingListCandidate(applicationsByTopic.get(topicOfReplacement), finalApproved, "Servidor do IFPB");
            }
            
            // If a suitable quota candidate is found, perform the swap
            if (bestQuotaCandidate != null) {
                finalApproved.remove(candidateToReplace);
                finalApproved.add(bestQuotaCandidate);

                // Recalculate current quota numbers
                currentAfroIndigenous = finalApproved.stream().filter(app -> isQuotaHolder(app, "Afrodescente") || isQuotaHolder(app, "Indígenas")).count();
                currentPwd = finalApproved.stream().filter(app -> isQuotaHolder(app, "Pessoa com deficiência")).count();
                currentServer = finalApproved.stream().filter(app -> isQuotaHolder(app, "Servidor do IFPB")).count();
            } else {
                // If no waiting list quota candidates can be found in that topic, we can't make a swap.
                // A more complex implementation might look in other topics, but based on rule 3.13(b), the swap is within the same topic.
                break;
            }
        }
        
        return finalApproved;
    }

    // Helper methods
    private boolean isQuotaHolder(Application app, String... quotaNames) {
        if (app.getCandidate() == null || app.getCandidate().getQuota() == null) return false;
        for (String name : quotaNames) {
            if (name.equals(app.getCandidate().getQuota().getName())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isQuotaHolder(Application app) {
        return app.getCandidate() != null && app.getCandidate().getQuota() != null;
    }

    private Application findBestWaitingListCandidate(List<Application> topicCandidates, List<Application> approved, String... quotaNames) {
        return topicCandidates.stream()
            .filter(app -> !approved.contains(app)) // Only look at waiting list
            .filter(app -> isQuotaHolder(app, quotaNames))
            .max(getRankingComparator())
            .orElse(null);
    }
    
    private Comparator<Application> getRankingComparator() {
        return Comparator
            .comparing(Application::getFinalScore, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(app -> getScoreForStage(app, 2), Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(app -> getScoreForStage(app, 3), Comparator.nullsLast(Comparator.reverseOrder()));
    }

    private StageEvaluation getEvaluationByStageOrder(List<StageEvaluation> evaluations, int order) {
        return evaluations.stream()
                .filter(e -> e.getProcessStage().getStageOrder() == order)
                .findFirst().orElse(null);
    }

    private BigDecimal getScoreForStage(Application app, int stageOrder) {
        return stageEvaluationRepository.findByApplication(app)
                .stream()
                .filter(e -> e.getProcessStage().getStageOrder() == stageOrder)
                .findFirst()
                .map(StageEvaluation::getTotalStageScore)
                .orElse(BigDecimal.ZERO);
    }

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
    
    // // Helper method to get a specific stage evaluation from a list
    // private StageEvaluation getEvaluationByStageOrder(List<StageEvaluation> evaluations, int order) {
    //     return evaluations.stream()
    //             .filter(e -> e.getProcessStage().getStageOrder() == order)
    //             .findFirst().orElse(null);
    // }

    // // Helper method to safely get a score for a specific stage for the comparator
    // private BigDecimal getScoreForStage(Application app, int stageOrder) {
    //     return stageEvaluationRepository.findByApplication(app)
    //             .stream()
    //             .filter(e -> e.getProcessStage().getStageOrder() == stageOrder)
    //             .findFirst()
    //             .map(StageEvaluation::getTotalStageScore)
    //             .orElse(BigDecimal.ZERO);
    // }

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