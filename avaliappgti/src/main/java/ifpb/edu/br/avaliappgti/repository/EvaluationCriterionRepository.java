package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifpb.edu.br.avaliappgti.model.EvaluationCriterion;
import ifpb.edu.br.avaliappgti.model.ProcessStage;

@Repository
public interface EvaluationCriterionRepository extends JpaRepository<EvaluationCriterion, Integer> {
    List<EvaluationCriterion> findByProcessStage(ProcessStage processStage);
    Optional<EvaluationCriterion> findByCriterionDescriptionAndProcessStage(String description, ProcessStage processStage);
}