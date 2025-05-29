package ifpb.edu.br.avaliappgti.config;

import ifpb.edu.br.avaliappgti.model.*;
import ifpb.edu.br.avaliappgti.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DatabaseSeeder {

    // @Bean
    // CommandLineRunner initDatabase(ResearchLineRepository researchLineRepository,
    //                                ResearchTopicRepository researchTopicRepository,
    //                                SelectionProcessRepository selectionProcessRepository,
    //                                ProcessStageRepository processStageRepository,
    //                                EvaluationCriterionRepository evaluationCriterionRepository,
    //                                CommitteeMemberRepository repository,
    //                                QuotaRepository quotaRepository,
    //                                CandidateRepository candidateRepository, 
    //                                CandidateDocumentRepository candidateDocumentRepository,
    //                                ApplicationRepository applicationRepository,
    //                                ApplicationVerificationRepository applicationVerificationRepository,
    //                                StageEvaluationRepository stageEvaluationRepository,
    //                                CriterionScoreRepository criterionScoreRepository) {
    //     return args -> {

    //         // Create Committee Member
    //         CommitteeMember newMember = new CommitteeMember(
    //                 "Ana Paula Souza",
    //                 "ana.souza@example.com",
    //                 "987.654.321-01",
    //                 "IFPB-1234567",
    //                 "61ce46c310554ee912b5aa06da9a0cea8e08cdd4db96006e0cdcab258618bf42" // hashed_password_here SHA256
    //         );
    //         repository.save(newMember);
                    
            
    //         // Create Quotas
    //         List<Quota> quotas = List.of(
    //             new Quota(
    //                 "Afrodescente",
    //                 "Destinado a candidatos que se autodeclaram pretos ou pardos, de acordo com o critério étnico-racial utilizado pelo IBGE."
    //             ),
    //             new Quota(
    //                 "Pessoa com deficiência",
    //                 "Cotas específicas para candidatos com algum tipo de deficiência, visando a inclusão e acessibilidade no ensino superior."
    //             ),
    //             new Quota(
    //                 "Servidor do IFPB",
    //                 "Para candidatos que sejam servidores do IFPB."
    //             ),
    //             new Quota(
    //                 "Indígenas",
    //                 "Cotas destinadas a candidatos que pertencem a comunidades indígenas ou quilombolas, com o objetivo de promover a diversidade e o acesso à educação."
    //             )
    //         );

    //         for (Quota quota : quotas) {
    //             quotaRepository.findByName(quota.getName()).ifPresentOrElse(
    //                 existing -> System.out.println("Quota já existe: " + quota.getName()),
    //                 () -> {
    //                     quotaRepository.save(quota);
    //                     System.out.println("Quota salva: " + quota.getName());
    //                 }
    //             );
    //         };

    //         // Create Selection Process
    //         SelectionProcess process = new SelectionProcess();
    //         process.setName("Processo Seletivo PPGTI 2025.1");
    //         process.setProgram("PPGTI");
    //         process.setYear("2025");
    //         process.setSemester("01");
    //         process.setStartDate(LocalDate.of(2024, 8, 16));
    //         process.setEndDate(LocalDate.of(2024, 11, 24));
    //         process.setWeightCurriculumStep(new BigDecimal("0.4"));
    //         process.setWeightPreProjectStep(new BigDecimal("0.3"));
    //         process.setWeightInterviewStep(new BigDecimal("0.3"));

    //         SelectionProcess savedProcess = selectionProcessRepository.save(process);

    //         // Create Process Stages
    //         ProcessStage stage1 = new ProcessStage();
    //         stage1.setStageName("Análise Curricular");
    //         stage1.setStageOrder(1);
    //         stage1.setSelectionProcess(savedProcess);
    //         stage1.setMinimumPassingScore(new BigDecimal("1.0"));
    //         stage1.setStageCharacter("Classificatório");
    //         processStageRepository.save(stage1);

    //         ProcessStage stage2 = new ProcessStage();
    //         stage2.setStageName("Análise do pré-projeto de pesquisa");
    //         stage2.setStageOrder(2);
    //         stage2.setSelectionProcess(savedProcess);
    //         stage2.setMinimumPassingScore(new BigDecimal("60.0"));
    //         stage2.setStageCharacter("Classificatório e Eliminatório");
    //         processStageRepository.save(stage2);

    //         ProcessStage stage3 = new ProcessStage();
    //         stage3.setStageName("Entrevista Individual");
    //         stage3.setStageOrder(3);
    //         stage3.setSelectionProcess(savedProcess);
    //         stage3.setMinimumPassingScore(new BigDecimal("70.0"));
    //         stage3.setStageCharacter("Classificatório e Eliminatório");
    //         processStageRepository.save(stage3);

    //         // CreateEvaluationCriterion de pré-projeto (stage2)
    //         EvaluationCriterion criterion1 = new EvaluationCriterion();
    //         criterion1.setCriterionDescription("Grau de aderência do projeto de pesquisa com o tema proposto pelo docente indicada no ato da inscrição");
    //         criterion1.setMaximumScore(new BigDecimal("10"));
    //         criterion1.setProcessStage(stage2);
    //         evaluationCriterionRepository.save(criterion1);

    //         createEvaluationCriterion(
    //             "Clareza e delimitação do problema de pesquisa",
    //             new BigDecimal("15"), stage2, evaluationCriterionRepository);

    //         createEvaluationCriterion(
    //             "Clareza e relevância da justificativa do projeto",
    //             new BigDecimal("10"), stage2, evaluationCriterionRepository);

    //         createEvaluationCriterion(
    //             "Atualidade e clareza da fundamentação teórica e descrição/análise de trabalhos relacionados",
    //             new BigDecimal("30"), stage2, evaluationCriterionRepository);

    //         createEvaluationCriterion(
    //             "Clareza e precisão da proposta e objetivos",
    //             new BigDecimal("20"), stage2, evaluationCriterionRepository);

    //         createEvaluationCriterion(
    //             "Adequação dos procedimentos metodológicos à problemática de pesquisa",
    //             new BigDecimal("15"), stage2, evaluationCriterionRepository);

    //         // Ceate Evaluation Criterion de entrevista (stage3)
    //         createEvaluationCriterion(
    //             "Aferição/defesa da proposta de pré-projeto de pesquisa de mestrado.",
    //             new BigDecimal("30"), stage3, evaluationCriterionRepository);

    //         createEvaluationCriterion(
    //             "Demonstração de domínio de conhecimentos no tema pretendido.",
    //             new BigDecimal("30"), stage3, evaluationCriterionRepository);

    //         createEvaluationCriterion(
    //             "Adequação do candidato para a execução da pesquisa de mestrado.",
    //             new BigDecimal("40"), stage3, evaluationCriterionRepository);

    //         // Create Research Lines
    //         ResearchLine cdi = new ResearchLine();
    //         cdi.setName("Ciência de Dados e Inteligência Artificial (CDI)");
    //         cdi.setSelectionProcess(savedProcess);
    //         cdi = researchLineRepository.save(cdi);

    //         ResearchLine gds = new ResearchLine();
    //         gds.setName("Gestão e Desenvolvimento de Sistemas (GDS)");
    //         gds.setSelectionProcess(savedProcess);
    //         gds = researchLineRepository.save(gds);

    //         ResearchLine rsd = new ResearchLine();
    //         rsd.setName("Redes e Sistemas Distribuídos (RSD)");;
    //         rsd.setSelectionProcess(savedProcess);
    //         rsd = researchLineRepository.save(rsd);

    //         // Create Research Topics for CDI
    //         ResearchTopic topic1 = new ResearchTopic();
    //         topic1.setName("Ciência de Dados e Inteligência Artificial em Domínios");
    //         topic1.setVacancies(5);
    //         topic1.setResearchLine(cdi);
    //         researchTopicRepository.save(topic1);
    //         createResearchTopic("Matching de Dados e Inteligência Artificial para Streaming de Dados", 2, cdi, researchTopicRepository);
    //         createResearchTopic("Métodos de Otimização ou de Aprendizagem de Máquina Aplicados a Problemas das Áreas de Logística, Segurança, Educação, Saúde ou Jogos", 3, cdi, researchTopicRepository);
    //         createResearchTopic("Arquiteturas e Modelos de Inteligência Artificial Aplicados à Educação, Saúde e Cidades", 1, cdi, researchTopicRepository);

    //         // Create Research Topics for GDS
    //         createResearchTopic("Inteligência Artificial na Educação", 2, gds, researchTopicRepository);
    //         createResearchTopic("Aplicações da Inteligência Artificial na Indústria 4.0", 1, gds, researchTopicRepository);
    //         createResearchTopic("Soluções em Plataformas de Sensoriamento Inteligente para Indústria", 2, gds, researchTopicRepository);
    //         createResearchTopic("Aplicação de Técnicas Inteligentes no Contexto de Engenharia de Software", 2, gds, researchTopicRepository);
    //         createResearchTopic("Boas Práticas em Gerenciamento de Projetos de Software: Otimizando a Colaboração e Produtividade em Equipes Virtuais ou Híbridos", 1, gds, researchTopicRepository);
    //         createResearchTopic("Gestão, Desenvolvimento e Testes em Projetos Ágeis de software", 1, gds, researchTopicRepository);
    //         createResearchTopic("Abordagens Multidisciplinares com Gamificação, Metodologias Ativas e Interação Humano-Computador", 2, gds, researchTopicRepository);
    //         createResearchTopic("Uso de Blockchain e Inteligência Artificial na Transformação Digital do Sistema Único de Saúde (SUS)", 1, gds, researchTopicRepository);
    //         createResearchTopic("Desenvolvimento de Sistemas Blockchain Apoiados pela Inteligência Artificial", 1, gds, researchTopicRepository);

    //         // Create Research Topics for RSD
    //         createResearchTopic("Redes 5G Privadas e Computação na Borda para Indústria 4.0", 3, rsd, researchTopicRepository);
    //         createResearchTopic("Desenvolvimento ou avaliação de solução computacional no contexto de redes", 3, rsd, researchTopicRepository);
    //         createResearchTopic("Sistemas embarcados e distribuídos para aplicações biomédicas", 2, rsd, researchTopicRepository);

    //         //Create Candidates
    //         Candidate candidate1 = new Candidate();
    //         candidate1.setName("Alice Santos");
    //         candidate1.setEmail("alice@example.com");
    //         candidate1.setCpf("11122233344");
    //         candidate1.setSocialName("Alice");
    //         candidate1.setSex("F");
    //         candidate1.setRegistration("12345");
    //         candidate1.setRegistrationState("PB");
    //         candidate1.setRegistrationPlace("Campina Grande");
    //         candidate1.setAddress("Rua das Flores");
    //         candidate1.setAddressNumber("100");
    //         candidate1.setAddressComplement("Apt 101");
    //         candidate1.setAddressNeighborhood("Centro");
    //         candidate1.setAddressCity("João Pessoa");
    //         candidate1.setAddressState("PB");
    //         candidate1.setAddressZipcode("58000-000");
    //         candidate1.setCellPhone("(83) 99999-1234");
    //         candidate1.setPhone("(83) 3333-1234");
    //         candidate1.setOtherEmail("alice.alt@example.com");
    //         candidate1.setEducationLevel("Graduação");
    //         candidate1.setGraduationCourse("Ciência da Computação");
    //         candidate1.setGraduationYear("2020");
    //         candidate1.setGraduationInstitution("IFPB");
    //         candidate1.setSpecializationCourse("IA Aplicada");
    //         candidate1.setSpecializationYear("2022");
    //         candidate1.setSpecializationInstitution("IFPB");
    //         candidate1.setLattesLink("http://lattes.cnpq.br/alice");

    //         Candidate candidate2 = new Candidate();
    //         candidate2.setName("Bruno Oliveira");
    //         candidate2.setEmail("bruno@example.com");
    //         candidate2.setCpf("55566677788");
    //         candidate2.setSocialName("Bruno");
    //         candidate2.setSex("M");
    //         candidate2.setRegistration("67890");
    //         candidate2.setRegistrationState("RN");
    //         candidate2.setRegistrationPlace("Natal");
    //         candidate2.setAddress("Avenida Principal");
    //         candidate2.setAddressNumber("200");
    //         candidate2.setAddressComplement("Casa");
    //         candidate2.setAddressNeighborhood("Bairro Novo");
    //         candidate2.setAddressCity("Natal");
    //         candidate2.setAddressState("RN");
    //         candidate2.setAddressZipcode("59000-000");
    //         candidate2.setCellPhone("(84) 98888-4321");
    //         candidate2.setPhone("(84) 3222-4321");
    //         candidate2.setOtherEmail("bruno.alt@example.com");
    //         candidate2.setEducationLevel("Mestrado");
    //         candidate2.setGraduationCourse("Engenharia Elétrica");
    //         candidate2.setGraduationYear("2018");
    //         candidate2.setGraduationInstitution("UFRN");
    //         candidate2.setSpecializationCourse("Sistemas de Energia");
    //         candidate2.setSpecializationYear("2019");
    //         candidate2.setSpecializationYear("2019");
    //         candidate2.setSpecializationInstitution("UFRN");
    //         candidate2.setLattesLink("http://lattes.cnpq.br/bruno");
    //         candidateRepository.saveAll(List.of(candidate1, candidate2));

    //          // Create Candidate Documents
    //         CandidateDocument doc1 = new CandidateDocument();
    //         doc1.setCandidate(candidate1); // assumes setter exists
    //         doc1.setScoreForm("score_form_1.pdf");
    //         doc1.setDiplomaCertificate("diploma_cert_1.pdf");
    //         doc1.setUndergraduateTranscript("transcript_1.pdf");
    //         doc1.setElectoralClearance("electoral_clearance_1.pdf");
    //         doc1.setProofOfResidence("residence_proof_1.pdf");
    //         doc1.setMilitaryClearance("military_clearance_1.pdf");
    //         doc1.setQuotaDeclarationAdmission("quota_admission_1.pdf");
    //         doc1.setQuotaDeclarationIf("quota_if_1.pdf");
    //         doc1.setRegistrationClearance("registration_clearance_1.pdf");

    //         CandidateDocument doc2 = new CandidateDocument();
    //         doc2.setCandidate(candidate2); // assumes setter exists
    //         doc2.setScoreForm("score_form_2.pdf");
    //         doc2.setDiplomaCertificate("diploma_cert_2.pdf");
    //         doc2.setUndergraduateTranscript("transcript_2.pdf");
    //         doc2.setElectoralClearance("electoral_clearance_2.pdf");
    //         doc2.setProofOfResidence("residence_proof_2.pdf");
    //         doc2.setMilitaryClearance("military_clearance_2.pdf");
    //         doc2.setQuotaDeclarationAdmission("quota_admission_2.pdf");
    //         doc2.setQuotaDeclarationIf("quota_if_2.pdf");
    //         doc2.setRegistrationClearance("registration_clearance_2.pdf");

    //         candidateDocumentRepository.saveAll(List.of(doc1, doc2));

    //         // Create Applications
    //         Application application = new Application();
    //         application.setCandidate(candidate1); 
    //         application.setSelectionProcess(process); 
    //         application.setResearchLine(cdi); 
    //         application.setResearchTopic(topic1);
    //         application.setProjectTitle("Aplicações de IA na Educação");
    //         application.setProjectPath("/documents/projects/ia_educacao.pdf");
    //         application.setApplicationDate(LocalDateTime.now());
    //         application.setApplicationStatus("Pending");
    //         application.setIsApproved(false);
    //         application.setFinalScore(null); // can be set later
    //         application.setOverallRanking(null);
    //         application.setRankingByTopic(null);
    //         applicationRepository.save(application);

    //         // Create Application Verification
    //         ApplicationVerification verification = new ApplicationVerification();
    //         verification.setApplication(application); 
    //         verification.setSelectionProcess(process);
    //         verification.setAcademicDataVerified(true);
    //         verification.setScoreFormVerified(true);
    //         verification.setDiplomaCertificateVerified(true);
    //         verification.setUndergraduateTranscriptVerified(true);
    //         verification.setElectoralClearanceVerified(true);
    //         verification.setProofOfResidenceVerified(true);
    //         verification.setMilitaryClearanceVerified(true);
    //         verification.setQuotaDeclarationAdmissionVerified(false);
    //         verification.setQuotaDeclarationIfVerified(false);
    //         verification.setRegistrationClearanceVerified(true);
    //         verification.setFinalStatus(1); // e.g., 1 = Homologado
    //         applicationVerificationRepository.save(verification);

    //         StageEvaluation stageEvaluation1 = new StageEvaluation();
    //         stageEvaluation1.setApplication(application); 
    //         stageEvaluation1.setProcessStage(stage1);
    //         stageEvaluation1.setTotalStageScore(BigDecimal.valueOf(85.5));
    //         stageEvaluation1.setIsEliminatedInStage(false);
    //         stageEvaluation1.setEvaluationDate(LocalDateTime.now());
    //         stageEvaluation1.setCommitteeMember(newMember);
    //         stageEvaluation1.setObservations("Good performance in interview.");
    //         stageEvaluationRepository.save(stageEvaluation1);

    //         StageEvaluation stageEvaluation2 = new StageEvaluation();
    //         stageEvaluation2.setApplication(application);
    //         stageEvaluation2.setProcessStage(stage2);
    //         // stageEvaluation2.setTotalStageScore(BigDecimal.valueOf(90.0));
    //         stageEvaluation2.setIsEliminatedInStage(false);
    //         stageEvaluation2.setEvaluationDate(LocalDateTime.now());
    //         stageEvaluation2.setCommitteeMember(newMember); 
    //         stageEvaluation2.setObservations("Good discuss in pre project.");
    //         stageEvaluationRepository.save(stageEvaluation2);

    //         CriterionScore score = new CriterionScore();
    //         score.setStageEvaluation(stageEvaluation2);
    //         score.setEvaluationCriterion(criterion1);
    //         score.setScoreObtained(BigDecimal.valueOf(8.75));
    //         criterionScoreRepository.save(score);

    //     };
    // }

    private void createResearchTopic(String name, int vacancies, ResearchLine researchLine, ResearchTopicRepository repository) {
        ResearchTopic topic = new ResearchTopic();
        topic.setName(name);
        topic.setVacancies(vacancies);
        topic.setResearchLine(researchLine);
        repository.save(topic);
    }

    private void createEvaluationCriterion(String description, BigDecimal maxScore,
                                       ProcessStage stage, EvaluationCriterionRepository repository) {
    EvaluationCriterion criterion = new EvaluationCriterion();
    criterion.setCriterionDescription(description);
    criterion.setMaximumScore(maxScore);
    criterion.setProcessStage(stage);
    repository.save(criterion);
    }
}