package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifpb.edu.br.avaliappgti.model.ResearchLine;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;

@Repository
public interface ResearchLineRepository extends JpaRepository<ResearchLine, Integer> {
    List<ResearchLine> findBySelectionProcess(SelectionProcess selectionProcess);
    Optional<ResearchLine> findByNameAndSelectionProcess(String name, SelectionProcess selectionProcess);
}