package ifpb.edu.br.avaliappgti.controller;

import ifpb.edu.br.avaliappgti.dto.RankedApplicationDTO;
import ifpb.edu.br.avaliappgti.service.RankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
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