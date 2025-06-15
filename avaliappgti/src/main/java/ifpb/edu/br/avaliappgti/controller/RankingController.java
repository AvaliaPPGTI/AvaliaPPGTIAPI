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
}