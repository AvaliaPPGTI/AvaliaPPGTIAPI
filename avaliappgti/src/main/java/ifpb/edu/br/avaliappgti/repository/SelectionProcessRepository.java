package ifpb.edu.br.avaliappgti.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ifpb.edu.br.avaliappgti.model.SelectionProcess;

public interface SelectionProcessRepository extends JpaRepository<SelectionProcess, Integer> {
    Optional<SelectionProcess> findByName(String name);
    List<SelectionProcess> findByStartDateBeforeAndEndDateAfter(LocalDate dateToCheckStart, LocalDate dateToCheckEnd);
    List<SelectionProcess> findByYear(String year);
}
