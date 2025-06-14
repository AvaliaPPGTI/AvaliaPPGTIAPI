package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.ProcessStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.ProcessStage;


@Repository
public interface EvaluationCriterionRepository extends JpaRepository<EvaluationCriterion, Integer> {
    List<EvaluationCriterion> findByProcessStage(ProcessStage processStage);
    Optional<EvaluationCriterion> findByCriterionDescriptionAndProcessStage(String description, ProcessStage processStage);

    // Find top-level criteria for a specific process stage (where parent is null)
    // Add EntityGraph to eagerly fetch children if you need the full tree structure
    @EntityGraph(attributePaths = {"children"}) // Fetch direct children
    List<EvaluationCriterion> findByProcessStageAndParentIsNull(ProcessStage processStage);

    // EntityGraph for findById if you need the full tree
    @Override
    @EntityGraph(attributePaths = {"parent", "children"}) // Eagerly load parent and children for context
    Optional<EvaluationCriterion> findById(Integer id);
}