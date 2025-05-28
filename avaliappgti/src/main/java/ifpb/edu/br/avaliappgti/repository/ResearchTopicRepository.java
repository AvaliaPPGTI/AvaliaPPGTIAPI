package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifpb.edu.br.avaliappgti.model.ResearchLine;
import ifpb.edu.br.avaliappgti.model.ResearchTopic;

@Repository
public interface ResearchTopicRepository extends JpaRepository<ResearchTopic, Integer> {
    List<ResearchTopic> findByResearchLine(ResearchLine researchLine);
    Optional<ResearchTopic> findByNameAndResearchLine(String name, ResearchLine researchLine);
}