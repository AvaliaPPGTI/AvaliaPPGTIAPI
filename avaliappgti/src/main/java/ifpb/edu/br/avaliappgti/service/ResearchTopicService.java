package ifpb.edu.br.avaliappgti.service;


import ifpb.edu.br.avaliappgti.model.ResearchLine;
import ifpb.edu.br.avaliappgti.model.ResearchTopic;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.repository.ResearchLineRepository;
import ifpb.edu.br.avaliappgti.repository.ResearchTopicRepository;
import ifpb.edu.br.avaliappgti.repository.SelectionProcessRepository; // Need to fetch SelectionProcess
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException; // For handling not found errors
import java.util.Optional;

@Service
public class ResearchTopicService {

    private final ResearchTopicRepository researchTopicRepository;
    private final ResearchLineRepository researchLineRepository;
    private final SelectionProcessRepository selectionProcessRepository;

    public ResearchTopicService(ResearchTopicRepository researchTopicRepository,
                                ResearchLineRepository researchLineRepository,
                                SelectionProcessRepository selectionProcessRepository) {
        this.researchTopicRepository = researchTopicRepository;
        this.researchLineRepository = researchLineRepository;
        this.selectionProcessRepository = selectionProcessRepository;
    }

    @Transactional(readOnly = true)
    public List<ResearchTopic> getAllResearchTopics() {
        return researchTopicRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ResearchTopic> getResearchTopicById(Integer id) {
        return researchTopicRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ResearchTopic> getResearchTopicsBySelectionProcessId(Integer processId) {
        // find the SelectionProcess
        SelectionProcess selectionProcess = selectionProcessRepository.findById(processId)
                .orElseThrow(() -> new NoSuchElementException("Selection Process not found with ID: " + processId));

        // find all ResearchLines associated with that SelectionProcess
        List<ResearchLine> researchLines = researchLineRepository.findBySelectionProcess(selectionProcess);

        // collect all ResearchTopics from these ResearchLines
        List<ResearchTopic> allTopics = new ArrayList<>();
        for (ResearchLine line : researchLines) {
            // Assuming ResearchTopicRepository has a findByResearchLine method
            allTopics.addAll(researchTopicRepository.findByResearchLine(line));
        }
        return allTopics;
    }

    @Transactional
    public ResearchTopic saveResearchTopic(ResearchTopic researchTopic) {
        return researchTopicRepository.save(researchTopic);
    }

    @Transactional
    public void deleteResearchTopic(Integer id) {
        researchTopicRepository.deleteById(id);
    }
}