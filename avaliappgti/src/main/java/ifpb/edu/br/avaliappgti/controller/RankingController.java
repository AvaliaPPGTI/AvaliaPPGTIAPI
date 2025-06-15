package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.dto.RankedApplicationDTO;
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
}