package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.dto.RankedApplicationDTO;
import ifpb.edu.br.avaliappgti.dto.StageRankingDTO;
import ifpb.edu.br.avaliappgti.service.RankingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    /**
     * Retrieves the last generated ranking for a given selection process.
     */
    @GetMapping("/process/{processId}")
    public ResponseEntity<List<RankedApplicationDTO>> getRanking(@PathVariable Integer processId) {
        try {
            List<RankedApplicationDTO> ranking = rankingService.getRankingForProcess(processId);
            if (ranking.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(ranking);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PostMapping("/generate/process/{processId}")
    public ResponseEntity<List<RankedApplicationDTO>> generateRanking(@PathVariable Integer processId) {
        try {
            List<RankedApplicationDTO> ranking = rankingService.generateRankingForProcess(processId);
            return ResponseEntity.ok(ranking);
        } catch (Exception e) {
            // Basic error handling
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retrieves the ranking for a specific stage within a selection process.
     */
    @GetMapping("/process/{processId}/stage/{stageId}")
    public ResponseEntity<List<StageRankingDTO>> getStageRanking(
            @PathVariable Integer processId,
            @PathVariable Integer stageId) {
        try {
            List<StageRankingDTO> ranking = rankingService.getRankingForStage(processId, stageId);
            if (ranking.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(ranking);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            // This catches the error if the stage does not belong to the process
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Retrieves the ranking for a specific stage within a selection process, filtered by research line.
     */
    @GetMapping("/process/{processId}/stage/{stageId}/line/{researchLineId}")
    public ResponseEntity<List<StageRankingDTO>> getStageRankingByResearchLine(
            @PathVariable Integer processId,
            @PathVariable Integer stageId,
            @PathVariable Integer researchLineId) {
        try {
            List<StageRankingDTO> ranking = rankingService.getRankingForStageByResearchLine(processId, stageId, researchLineId);
            if (ranking.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(ranking);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            // This catches errors if the stage or line does not belong to the process
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Retrieves the ranking for a specific stage within a selection process, filtered by research topic.
     */
    @GetMapping("/process/{processId}/stage/{stageId}/topic/{researchTopicId}")
    public ResponseEntity<List<StageRankingDTO>> getStageRankingByResearchTopic(
            @PathVariable Integer processId,
            @PathVariable Integer stageId,
            @PathVariable Integer researchTopicId) {
        try {
            List<StageRankingDTO> ranking = rankingService.getRankingForStageByResearchTopic(processId, stageId, researchTopicId);
            if (ranking.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(ranking);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            // This catches errors if the stage or topic does not belong to the process
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Retrieves the ranking for a specific stage within a selection process, filtered by application status.
     * Possible statuses: "Classificado", "Desclassificado", "Pendente".
     */
    @GetMapping("/process/{processId}/stage/{stageId}/status/{status}")
    public ResponseEntity<List<StageRankingDTO>> getStageRankingByStatus(
            @PathVariable Integer processId,
            @PathVariable Integer stageId,
            @PathVariable String status) {
        try {
            List<StageRankingDTO> ranking = rankingService.getRankingForStageByStatus(processId, stageId, status);
            if (ranking.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(ranking);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            // This catches errors if the stage does not belong to the process
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}