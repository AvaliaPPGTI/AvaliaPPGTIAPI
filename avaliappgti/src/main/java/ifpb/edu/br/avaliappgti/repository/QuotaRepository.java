package ifpb.edu.br.avaliappgti.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifpb.edu.br.avaliappgti.model.Quota;

@Repository
public interface QuotaRepository extends JpaRepository<Quota, Integer> {
    Optional<Quota> findByName(String name);
}
