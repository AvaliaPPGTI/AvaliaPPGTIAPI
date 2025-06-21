package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.model.*;
import ifpb.edu.br.avaliappgti.dto.CandidateDetailDTO;
import ifpb.edu.br.avaliappgti.repository.CandidateDocumentRepository;
import ifpb.edu.br.avaliappgti.repository.CandidateRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CandidateServiceTest {

    @Mock private CandidateRepository candidateRepository;
    @Mock private CandidateDocumentRepository candidateDocumentRepository;

    @InjectMocks private CandidateService candidateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCandidateDetails_WhenCandidateAndDocumentExist() {
        Candidate candidate = new Candidate();
        candidate.setId(1);
        candidate.setName("Alice");
        candidate.setEmail("alice@example.com");
        candidate.setQuota(new Quota());
        candidate.getQuota().setName("Cota Teste");

        CandidateDocument document = new CandidateDocument();
        document.setId(100);
        document.setScoreForm("score_form.pdf");

        when(candidateRepository.findById(1)).thenReturn(Optional.of(candidate));
        when(candidateDocumentRepository.findByCandidate(candidate)).thenReturn(Optional.of(document));

        Optional<CandidateDetailDTO> result = candidateService.getCandidateDetails(1);

        assertTrue(result.isPresent());
        CandidateDetailDTO dto = result.get();
        assertEquals("Alice", dto.getName());
        assertEquals("score_form.pdf", dto.getScoreForm());
        assertEquals("Cota Teste", dto.getQuotaName());
        assertEquals(100, dto.getDocumentId());
    }

    @Test
    void testGetCandidateDetails_WhenCandidateExistsButNoDocument() {
        Candidate candidate = new Candidate();
        candidate.setId(2);
        candidate.setName("Bob");

        when(candidateRepository.findById(2)).thenReturn(Optional.of(candidate));
        when(candidateDocumentRepository.findByCandidate(candidate)).thenReturn(Optional.empty());

        Optional<CandidateDetailDTO> result = candidateService.getCandidateDetails(2);

        assertTrue(result.isPresent());
        CandidateDetailDTO dto = result.get();
        assertEquals("Bob", dto.getName());
        assertNull(dto.getScoreForm());
        assertNull(dto.getDocumentId());
    }

    @Test
    void testGetCandidateDetails_WhenCandidateDoesNotExist() {
        when(candidateRepository.findById(999)).thenReturn(Optional.empty());

        Optional<CandidateDetailDTO> result = candidateService.getCandidateDetails(999);
        assertFalse(result.isPresent());
    }

    @Test
    void testSaveCandidate() {
        Candidate candidate = new Candidate();
        candidate.setName("Carlos");

        when(candidateRepository.save(candidate)).thenReturn(candidate);

        Candidate result = candidateService.saveCandidate(candidate);
        assertNotNull(result);
        assertEquals("Carlos", result.getName());
        verify(candidateRepository, times(1)).save(candidate);
    }

    @Test
    void testDeleteCandidate() {
        Integer candidateId = 3;

        doNothing().when(candidateRepository).deleteById(candidateId);
        candidateService.deleteCandidate(candidateId);

        verify(candidateRepository, times(1)).deleteById(candidateId);
    }
}
