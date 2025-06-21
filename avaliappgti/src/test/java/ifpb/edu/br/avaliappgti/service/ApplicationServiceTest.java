package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.model.*;
import ifpb.edu.br.avaliappgti.repository.*;
import ifpb.edu.br.avaliappgti.dto.CandidateApplicationDetailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationServiceTest {

    @Mock private ApplicationRepository applicationRepository;
    @Mock private SelectionProcessRepository selectionProcessRepository;
    @Mock private ApplicationVerificationRepository applicationVerificationRepository;
    @Mock private ResearchTopicRepository researchTopicRepository;
    @Mock private CandidateRepository candidateRepository;

    @InjectMocks private ApplicationService applicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetApplicationByCandidateId_Found() {
        Application app = new Application();
        when(applicationRepository.findByCandidateId(1)).thenReturn(Optional.of(app));

        Optional<Application> result = applicationService.getApplicationByCandidateId(1);
        assertTrue(result.isPresent());
        assertEquals(app, result.get());
    }

    @Test
    void testGetAllApplications() {
        List<Application> apps = List.of(new Application(), new Application());
        when(applicationRepository.findAll()).thenReturn(apps);

        List<Application> result = applicationService.getAllApplications();
        assertEquals(2, result.size());
    }

    @Test
    void testGetHomologatedCandidates() {
        Candidate candidate = new Candidate();
        Application app = new Application();
        app.setCandidate(candidate);
        ApplicationVerification verification = new ApplicationVerification();
        verification.setApplication(app);
        when(applicationVerificationRepository.findByFinalStatus(1))
                .thenReturn(List.of(verification));

        List<Candidate> result = applicationService.getHomologatedCandidates();
        assertEquals(1, result.size());
        assertEquals(candidate, result.get(0));
    }

    @Test
    void testGetHomologatedCandidatesBySelectionProcessId_Found() {
        SelectionProcess process = new SelectionProcess();
        when(selectionProcessRepository.findById(1)).thenReturn(Optional.of(process));

        List<Candidate> candidates = List.of(new Candidate(), new Candidate());
        when(applicationVerificationRepository.findCandidatesByVerificationStatusAndSelectionProcess(1, process))
                .thenReturn(candidates);

        List<Candidate> result = applicationService.getHomologatedCandidatesBySelectionProcessId(1);
        assertEquals(2, result.size());
    }

    @Test
    void testGetHomologatedCandidatesBySelectionProcessId_NotFound() {
        when(selectionProcessRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> applicationService.getHomologatedCandidatesBySelectionProcessId(1));
    }

    @Test
    void testGetCandidateDetailsBySelectionProcessId() {
        SelectionProcess process = new SelectionProcess();
        Candidate candidate = new Candidate();
        candidate.setId(1);
        candidate.setName("Ana");

        ResearchLine line = new ResearchLine();
        line.setName("IA");

        ResearchTopic topic = new ResearchTopic();
        topic.setName("Detecção de Objetos");
        topic.setResearchLine(line);

        Application app = new Application();
        app.setCandidate(candidate);
        app.setResearchTopic(topic);
        app.setId(100);

        when(selectionProcessRepository.findById(1)).thenReturn(Optional.of(process));
        when(applicationRepository.findBySelectionProcess(process)).thenReturn(List.of(app));

        List<CandidateApplicationDetailDTO> result = applicationService.getCandidateDetailsBySelectionProcessId(1);

        assertEquals(1, result.size());
        CandidateApplicationDetailDTO dto = result.get(0);
        assertEquals("Ana", dto.getCandidateName());
        assertEquals("Detecção de Objetos", dto.getResearchTopicName());
        assertEquals("IA", dto.getResearchLineName());
        assertEquals(100, dto.getApplicationId());
    }

    @Test
    void testGetHomologatedCandidatesByResearchTopic_Found() {
        when(researchTopicRepository.existsById(1)).thenReturn(true);

        List<Candidate> candidates = List.of(new Candidate());
        when(applicationRepository.findHomologatedCandidatesByResearchTopicId(1)).thenReturn(candidates);

        List<Candidate> result = applicationService.getHomologatedCandidatesByResearchTopic(1);
        assertEquals(1, result.size());
    }

    @Test
    void testGetHomologatedCandidatesByResearchTopic_NotFound() {
        when(researchTopicRepository.existsById(1)).thenReturn(false);
        assertThrows(NoSuchElementException.class,
                () -> applicationService.getHomologatedCandidatesByResearchTopic(1));
    }

    @Test
    void testGetApplicationByCandidateAndResearchTopic_Found() {
        Candidate candidate = new Candidate();
        ResearchTopic topic = new ResearchTopic();
        Application app = new Application();

        when(candidateRepository.findById(1)).thenReturn(Optional.of(candidate));
        when(researchTopicRepository.findById(2)).thenReturn(Optional.of(topic));
        when(applicationRepository.findByCandidateAndResearchTopic(candidate, topic)).thenReturn(Optional.of(app));

        Optional<Application> result = applicationService.getApplicationByCandidateAndResearchTopic(1, 2);
        assertTrue(result.isPresent());
        assertEquals(app, result.get());
    }

    @Test
    void testGetApplicationByCandidateAndResearchTopic_CandidateNotFound() {
        when(candidateRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> applicationService.getApplicationByCandidateAndResearchTopic(1, 2));
    }

    @Test
    void testGetApplicationByCandidateAndResearchTopic_TopicNotFound() {
        Candidate candidate = new Candidate();
        when(candidateRepository.findById(1)).thenReturn(Optional.of(candidate));
        when(researchTopicRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> applicationService.getApplicationByCandidateAndResearchTopic(1, 2));
    }
}
