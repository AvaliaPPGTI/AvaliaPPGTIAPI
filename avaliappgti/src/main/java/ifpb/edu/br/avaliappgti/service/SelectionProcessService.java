package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.repository.SelectionProcessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SelectionProcessService {
    private final SelectionProcessRepository selectionProcessRepository;

    public SelectionProcessService(SelectionProcessRepository selectionProcessRepository) {
        this.selectionProcessRepository = selectionProcessRepository;
    }

    @Transactional(readOnly = true)
    public Optional<SelectionProcess> getCurrentSelectionProcess() {
        LocalDate today = LocalDate.now();
        // Use the custom query to find the active process
        return selectionProcessRepository.findCurrentSelectionProcess(today);
    }
}
