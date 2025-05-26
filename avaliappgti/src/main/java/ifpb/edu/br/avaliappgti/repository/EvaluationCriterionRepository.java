package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.ProcessStage;

@Repository
public interface EvaluationCriterionRepository extends JpaRepository<EvaluationCriterion, Integer> {
    // Find all criteria for a specific process stage
    List<EvaluationCriterion> findByProcessStage(ProcessStage processStage);

    // Find a specific criterion by its description within a given process stage
    Optional<EvaluationCriterion> findByCriterionDescriptionAndProcessStage(String description, ProcessStage processStage);
}