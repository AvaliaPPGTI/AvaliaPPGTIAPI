package ifpb.edu.br.avaliappgti.service;

import ifpb.edu.br.avaliappgti.model.Candidate;
import ifpb.edu.br.avaliappgti.model.CandidateDocument;
import ifpb.edu.br.avaliappgti.repository.CandidateRepository;
import ifpb.edu.br.avaliappgti.repository.CandidateDocumentRepository;
import ifpb.edu.br.avaliappgti.dto.CandidateDetailDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CandidateService {

    public final CandidateRepository candidateRepository;
    public final CandidateDocumentRepository candidateDocumentRepository;

    public CandidateService(CandidateRepository candidateRepository, CandidateDocumentRepository candidateDocumentRepository) {
        this.candidateRepository = candidateRepository;
        this.candidateDocumentRepository = candidateDocumentRepository;
    }

    @Transactional(readOnly = true)
    public Optional<CandidateDetailDTO> getCandidateDetails(Integer candidateId) {
        // Fetch the Candidate (now only with 'quota' eagerly loaded, if specified in @EntityGraph)
        Optional<Candidate> candidateOptional = candidateRepository.findById(candidateId);

        if (candidateOptional.isEmpty()) {
            return Optional.empty();
        }

        Candidate candidate = candidateOptional.get();

        // explicitly fetch the CandidateDocument using the CandidateDocumentRepository
        Optional<CandidateDocument> documentOptional = candidateDocumentRepository.findByCandidate(candidate);
        CandidateDocument document = documentOptional.orElse(null);

        CandidateDetailDTO dto = new CandidateDetailDTO();

        // Populate Candidate properties
        dto.setId(candidate.getId());
        dto.setEmail(candidate.getEmail());
        dto.setCpf(candidate.getCpf());
        dto.setName(candidate.getName());
        dto.setSocialName(candidate.getSocialName());
        dto.setSex(candidate.getSex());
        dto.setRegistration(candidate.getRegistration());
        dto.setRegistrationState(candidate.getRegistrationState());
        dto.setRegistrationPlace(candidate.getRegistrationPlace());
        dto.setAddress(candidate.getAddress());
        dto.setAddressNumber(candidate.getAddressNumber());
        dto.setAddressComplement(candidate.getAddressComplement());
        dto.setAddressNeighborhood(candidate.getAddressNeighborhood());
        dto.setAddressCity(candidate.getAddressCity());
        dto.setAddressState(candidate.getAddressState());
        dto.setAddressZipcode(candidate.getAddressZipcode());
        dto.setCellPhone(candidate.getCellPhone());
        dto.setPhone(candidate.getPhone());
        dto.setOtherEmail(candidate.getOtherEmail());
        dto.setEducationLevel(candidate.getEducationLevel());
        dto.setGraduationCourse(candidate.getGraduationCourse());
        dto.setGraduationYear(candidate.getGraduationYear());
        dto.setGraduationInstitution(candidate.getGraduationInstitution());
        dto.setSpecializationCourse(candidate.getSpecializationCourse());
        dto.setSpecializationYear(candidate.getSpecializationYear());
        dto.setSpecializationInstitution(candidate.getSpecializationInstitution());
        dto.setLattesLink(candidate.getLattesLink());
        dto.setQuotaName(candidate.getQuota() != null ? candidate.getQuota().getName() : null);


        // Populate CandidateDocument properties (if document exists)
        if (document != null) {
            dto.setDocumentId(document.getId());
            dto.setScoreForm(document.getScoreForm());
            dto.setDiplomaCertificate(document.getDiplomaCertificate());
            dto.setUndergraduateTranscript(document.getUndergraduateTranscript());
            dto.setElectoralClearance(document.getElectoralClearance());
            dto.setProofOfResidence(document.getProofOfResidence());
            dto.setMilitaryClearance(document.getMilitaryClearance());
            dto.setQuotaDeclarationAdmission(document.getQuotaDeclarationAdmission());
            dto.setQuotaDeclarationIf(document.getQuotaDeclarationIf());
            dto.setRegistrationClearance(document.getRegistrationClearance());
        }

        return Optional.of(dto);
    }

    @Transactional
    public Candidate saveCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    @Transactional
    public void deleteCandidate(Integer id) {
        candidateRepository.deleteById(id);
    }
}
