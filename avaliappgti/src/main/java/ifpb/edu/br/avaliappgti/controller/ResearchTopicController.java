package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.model.ResearchTopic;
import ifpb.edu.br.avaliappgti.service.ResearchTopicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/research-topics")
public class ResearchTopicController {

    private final ResearchTopicService researchTopicService;

    public ResearchTopicController(ResearchTopicService researchTopicService) {
        this.researchTopicService = researchTopicService;
    }

    @GetMapping
    public ResponseEntity<List<ResearchTopic>> getAllResearchTopics() {
        List<ResearchTopic> topics = researchTopicService.getAllResearchTopics();
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResearchTopic> getResearchTopicById(@PathVariable Integer id) {
        return researchTopicService.getResearchTopicById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-process/{processId}")
    public ResponseEntity<List<ResearchTopic>> getResearchTopicsBySelectionProcess(@PathVariable Integer processId) {
        try {
            List<ResearchTopic> topics = researchTopicService.getResearchTopicsBySelectionProcessId(processId);
            if (topics.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(topics);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<ResearchTopic> createResearchTopic(@RequestBody ResearchTopic researchTopic) {
        ResearchTopic savedTopic = researchTopicService.saveResearchTopic(researchTopic);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTopic);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResearchTopic> updateResearchTopic(@PathVariable Integer id, @RequestBody ResearchTopic researchTopic) {
        researchTopic.setId(id);
        if (!researchTopicService.getResearchTopicById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        ResearchTopic updatedTopic = researchTopicService.saveResearchTopic(researchTopic);
        return ResponseEntity.ok(updatedTopic);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResearchTopic(@PathVariable Integer id) {
        if (!researchTopicService.getResearchTopicById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        researchTopicService.deleteResearchTopic(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}