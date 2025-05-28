package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;

@Repository
public interface ProcessStageRepository extends JpaRepository<ProcessStage, Integer> {
    Optional<ProcessStage> findById(Integer id); // JpaRepository provides this by default

    // Find all stages for a specific selection process
    List<ProcessStage> findBySelectionProcess(SelectionProcess selectionProcess);

    // Find a specific stage by its name within a given selection process
    Optional<ProcessStage> findByStageNameAndSelectionProcess(String stageName, SelectionProcess selectionProcess);

    // Find a stage by its order within a given selection process
    Optional<ProcessStage> findByStageOrderAndSelectionProcess(Integer stageOrder, SelectionProcess selectionProcess);

    // Find eliminatory stages for a process
    List<ProcessStage> findBySelectionProcessAndStageCharacterContaining(SelectionProcess selectionProcess, String character);
}