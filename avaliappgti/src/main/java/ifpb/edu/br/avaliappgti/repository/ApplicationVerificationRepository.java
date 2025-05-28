package ifpb.edu.br.avaliappgti.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifpb.edu.br.avaliappgti.model.Application;
import ifpb.edu.br.avaliappgti.model.ApplicationVerification;
import ifpb.edu.br.avaliappgti.model.Candidate;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;


@Repository
public interface ApplicationVerificationRepository extends JpaRepository<ApplicationVerification, Integer> {
    Optional<ApplicationVerification> findByApplication(Application application);
    List<ApplicationVerification> findByFinalStatus(Integer finalStatus); // e.g., 0 for Recusado, 1 for Homologado
    

    @Query("SELECT DISTINCT a.candidate FROM ApplicationVerification av JOIN av.application a WHERE av.finalStatus = :status AND a.selectionProcess = :selectionProcess")
    List<Candidate> findCandidatesByVerificationStatusAndSelectionProcess(@Param("status") Integer status, @Param("selectionProcess") SelectionProcess selectionProcess);
}