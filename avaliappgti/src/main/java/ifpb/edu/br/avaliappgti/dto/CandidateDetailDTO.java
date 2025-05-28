package ifpb.edu.br.avaliappgti.dto;

import java.io.Serializable;

public class CandidateDetailDTO implements Serializable {

    // Candidate properties
    private Integer id;
    private String email;
    private String cpf;
    private String name;
    private String socialName;
    private String sex;
    private String registration;
    private String registrationState;
    private String registrationPlace;
    private String address;
    private String addressNumber;
    private String addressComplement;
    private String addressNeighborhood;
    private String addressCity;
    private String addressState;
    private String addressZipcode;
    private String cellPhone;
    private String phone;
    private String otherEmail;
    private String educationLevel;
    private String graduationCourse;
    private String graduationYear;
    private String graduationInstitution;
    private String specializationCourse;
    private String specializationYear;
    private String specializationInstitution;
    private String lattesLink;
    private String quotaName; // From Quota entity

    // CandidateDocument properties
    private Integer documentId; // ID of the CandidateDocument
    private String scoreForm;
    private String diplomaCertificate;
    private String undergraduateTranscript;
    private String electoralClearance;
    private String proofOfResidence;
    private String militaryClearance;
    private String quotaDeclarationAdmission;
    private String quotaDeclarationIf;
    private String registrationClearance;

    // Constructors
    public CandidateDetailDTO() {
    }


    // Getters and Setters for all fields (omitted for brevity, but required)
    // --- Candidate Properties Getters/Setters ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSocialName() { return socialName; }
    public void setSocialName(String socialName) { this.socialName = socialName; }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    public String getRegistration() { return registration; }
    public void setRegistration(String registration) { this.registration = registration; }
    public String getRegistrationState() { return registrationState; }
    public void setRegistrationState(String registrationState) { this.registrationState = registrationState; }
    public String getRegistrationPlace() { return registrationPlace; }
    public void setRegistrationPlace(String registrationPlace) { this.registrationPlace = registrationPlace; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getAddressNumber() { return addressNumber; }
    public void setAddressNumber(String addressNumber) { this.addressNumber = addressNumber; }
    public String getAddressComplement() { return addressComplement; }
    public void setAddressComplement(String addressComplement) { this.addressComplement = addressComplement; }
    public String getAddressNeighborhood() { return addressNeighborhood; }
    public void setAddressNeighborhood(String addressNeighborhood) { this.addressNeighborhood = addressNeighborhood; }
    public String getAddressCity() { return addressCity; }
    public void setAddressCity(String addressCity) { this.addressCity = addressCity; }
    public String getAddressState() { return addressState; }
    public void setAddressState(String addressState) { this.addressState = addressState; }
    public String getAddressZipcode() { return addressZipcode; }
    public void setAddressZipcode(String addressZipcode) { this.addressZipcode = addressZipcode; }
    public String getCellPhone() { return cellPhone; }
    public void setCellPhone(String cellPhone) { this.cellPhone = cellPhone; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getOtherEmail() { return otherEmail; }
    public void setOtherEmail(String otherEmail) { this.otherEmail = otherEmail; }
    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }
    public String getGraduationCourse() { return graduationCourse; }
    public void setGraduationCourse(String graduationCourse) { this.graduationCourse = graduationCourse; }
    public String getGraduationYear() { return graduationYear; }
    public void setGraduationYear(String graduationYear) { this.graduationYear = graduationYear; }
    public String getGraduationInstitution() { return graduationInstitution; }
    public void setGraduationInstitution(String graduationInstitution) { this.graduationInstitution = graduationInstitution; }
    public String getSpecializationCourse() { return specializationCourse; }
    public void setSpecializationCourse(String specializationCourse) { this.specializationCourse = specializationCourse; }
    public String getSpecializationYear() { return specializationYear; }
    public void setSpecializationYear(String specializationYear) { this.specializationYear = specializationYear; }
    public String getSpecializationInstitution() { return specializationInstitution; }
    public void setSpecializationInstitution(String specializationInstitution) { this.specializationInstitution = specializationInstitution; }
    public String getLattesLink() { return lattesLink; }
    public void setLattesLink(String lattesLink) { this.lattesLink = lattesLink; }
    public String getQuotaName() { return quotaName; }
    public void setQuotaName(String quotaName) { this.quotaName = quotaName; }

    // --- CandidateDocument Properties Getters/Setters ---
    public Integer getDocumentId() { return documentId; }
    public void setDocumentId(Integer documentId) { this.documentId = documentId; }
    public String getScoreForm() { return scoreForm; }
    public void setScoreForm(String scoreForm) { this.scoreForm = scoreForm; }
    public String getDiplomaCertificate() { return diplomaCertificate; }
    public void setDiplomaCertificate(String diplomaCertificate) { this.diplomaCertificate = diplomaCertificate; }
    public String getUndergraduateTranscript() { return undergraduateTranscript; }
    public void setUndergraduateTranscript(String undergraduateTranscript) { this.undergraduateTranscript = undergraduateTranscript; }
    public String getElectoralClearance() { return electoralClearance; }
    public void setElectoralClearance(String electoralClearance) { this.electoralClearance = electoralClearance; }
    public String getProofOfResidence() { return proofOfResidence; }
    public void setProofOfResidence(String proofOfResidence) { this.proofOfResidence = proofOfResidence; }
    public String getMilitaryClearance() { return militaryClearance; }
    public void setMilitaryClearance(String militaryClearance) { this.militaryClearance = militaryClearance; }
    public String getQuotaDeclarationAdmission() { return quotaDeclarationAdmission; }
    public void setQuotaDeclarationAdmission(String quotaDeclarationAdmission) { this.quotaDeclarationAdmission = quotaDeclarationAdmission; }
    public String getQuotaDeclarationIf() { return quotaDeclarationIf; }
    public void setQuotaDeclarationIf(String quotaDeclarationIf) { this.quotaDeclarationIf = quotaDeclarationIf; }
    public String getRegistrationClearance() { return registrationClearance; }
    public void setRegistrationClearance(String registrationClearance) { this.registrationClearance = registrationClearance; }

    @Override
    public String toString() {
        return "CandidateDetailDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cpf='" + cpf + '\'' +
                ", documentId=" + documentId +
                '}';
    }
}
