package ifpb.edu.br.avaliappgti.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ifpb.edu.br.avaliappgti.model.SelectionProcess;

public interface SelectionProcessRepository extends JpaRepository<SelectionProcess, Integer> {
    Optional<SelectionProcess> findByName(String name);
    List<SelectionProcess> findByStartDateBeforeAndEndDateAfter(LocalDate dateToCheckStart, LocalDate dateToCheckEnd);
    List<SelectionProcess> findByYear(String year);

        /**
     * Finds the first active selection process based on the current date.
     * An active process is one where the current date is between the start and end dates (inclusive).
     * It is ordered by the start date descending to pick the most recent one if multiple are active.
     */
    @Query("SELECT sp FROM SelectionProcess sp WHERE sp.startDate <= :currentDate AND sp.endDate >= :currentDate ORDER BY sp.startDate DESC")
    Optional<SelectionProcess> findCurrentSelectionProcess(@Param("currentDate") LocalDate currentDate);
}
