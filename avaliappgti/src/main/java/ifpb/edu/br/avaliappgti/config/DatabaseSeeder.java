package ifpb.edu.br.avaliappgti.config;

import ifpb.edu.br.avaliappgti.model.ProcessStage;
import ifpb.edu.br.avaliappgti.model.ResearchLine;
import ifpb.edu.br.avaliappgti.model.ResearchTopic;
import ifpb.edu.br.avaliappgti.model.SelectionProcess;
import ifpb.edu.br.avaliappgti.repository.ResearchLineRepository;
import ifpb.edu.br.avaliappgti.repository.ResearchTopicRepository;
import ifpb.edu.br.avaliappgti.repository.SelectionProcessRepository;
import ifpb.edu.br.avaliappgti.repository.ProcessStageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(ResearchLineRepository researchLineRepository,
                                   ResearchTopicRepository researchTopicRepository,
                                   SelectionProcessRepository selectionProcessRepository,
                                   ProcessStageRepository processStageRepository) {
        return args -> {

            // Create Selection Process
            SelectionProcess process = new SelectionProcess();
            process.setName("Processo Seletivo PPGTI 2025.1");
            process.setProgram("PPGTI");
            process.setYear("2025");
            process.setSemester("01");
            process.setStartDate(LocalDate.of(2024, 8, 16));
            process.setEndDate(LocalDate.of(2024, 11, 24));
            process.setWeightCurriculumStep(new BigDecimal("0.4"));
            process.setWeightPreProjectStep(new BigDecimal("0.3"));
            process.setWeightInterviewStep(new BigDecimal("0.3"));

            SelectionProcess savedProcess = selectionProcessRepository.save(process);

            // Create Process Stages
            ProcessStage stage1 = new ProcessStage();
            stage1.setStageName("Analise Curricular");
            stage1.setStageOrder(1);
            stage1.setSelectionProcess(savedProcess);
            stage1.setMinimumPassingScore(new BigDecimal("1"));
            stage1.setStageCharacter("Classificatório");
            processStageRepository.save(stage1);

            ProcessStage stage2 = new ProcessStage();
            stage2.setStageName("Análise do pré-projeto de pesquisa");
            stage2.setStageOrder(2);
            stage2.setSelectionProcess(savedProcess);
            stage2.setMinimumPassingScore(new BigDecimal("60"));
            stage2.setStageCharacter("Classificatório e Eliminatório");
            processStageRepository.save(stage2);

            ProcessStage stage3 = new ProcessStage();
            stage3.setStageName("Entrevista Individual");
            stage3.setStageOrder(3);
            stage3.setSelectionProcess(savedProcess);
            stage3.setMinimumPassingScore(new BigDecimal("70"));
            stage3.setStageCharacter("Classificatório e Eliminatório");
            processStageRepository.save(stage3);

            // Create Research Lines
            ResearchLine cdi = new ResearchLine();
            cdi.setName("Ciência de Dados e Inteligência Artificial (CDI)");
            cdi.setSelectionProcess(savedProcess);
            cdi = researchLineRepository.save(cdi);

            ResearchLine gds = new ResearchLine();
            gds.setName("Gestão e Desenvolvimento de Sistemas (GDS)");
            cdi.setSelectionProcess(savedProcess);
            gds = researchLineRepository.save(gds);

            ResearchLine rsd = new ResearchLine();
            rsd.setName("Redes e Sistemas Distribuídos (RSD)");;
            cdi.setSelectionProcess(savedProcess);
            rsd = researchLineRepository.save(rsd);

            // Create Research Topics for CDI
            createResearchTopic("Ciência de Dados e Inteligência Artificial em Domínios", 5, cdi, researchTopicRepository);
            createResearchTopic("Matching de Dados e Inteligência Artificial para Streaming de Dados", 2, cdi, researchTopicRepository);
            createResearchTopic("Métodos de Otimização ou de Aprendizagem de Máquina Aplicados a Problemas das Áreas de Logística, Segurança, Educação, Saúde ou Jogos", 3, cdi, researchTopicRepository);
            createResearchTopic("Arquiteturas e Modelos de Inteligência Artificial Aplicados à Educação, Saúde e Cidades", 1, cdi, researchTopicRepository);

            // Create Research Topics for GDS
            createResearchTopic("Inteligência Artificial na Educação", 2, gds, researchTopicRepository);
            createResearchTopic("Aplicações da Inteligência Artificial na Indústria 4.0", 1, gds, researchTopicRepository);
            createResearchTopic("Soluções em Plataformas de Sensoriamento Inteligente para Indústria", 2, gds, researchTopicRepository);
            createResearchTopic("Aplicação de Técnicas Inteligentes no Contexto de Engenharia de Software", 2, gds, researchTopicRepository);
            createResearchTopic("Boas Práticas em Gerenciamento de Projetos de Software: Otimizando a Colaboração e Produtividade em Equipes Virtuais ou Híbridos", 1, gds, researchTopicRepository);
            createResearchTopic("Gestão, Desenvolvimento e Testes em Projetos Ágeis de software", 1, gds, researchTopicRepository);
            createResearchTopic("Abordagens Multidisciplinares com Gamificação, Metodologias Ativas e Interação Humano-Computador", 2, gds, researchTopicRepository);
            createResearchTopic("Uso de Blockchain e Inteligência Artificial na Transformação Digital do Sistema Único de Saúde (SUS)", 1, gds, researchTopicRepository);
            createResearchTopic("Desenvolvimento de Sistemas Blockchain Apoiados pela Inteligência Artificial", 1, gds, researchTopicRepository);

            // Create Research Topics for RSD
            createResearchTopic("Redes 5G Privadas e Computação na Borda para Indústria 4.0", 3, rsd, researchTopicRepository);
            createResearchTopic("Desenvolvimento ou avaliação de solução computacional no contexto de redes", 3, rsd, researchTopicRepository);
            createResearchTopic("Sistemas embarcados e distribuídos para aplicações biomédicas", 2, rsd, researchTopicRepository);
        };
    }

    private void createResearchTopic(String name, int vacancies, ResearchLine researchLine, ResearchTopicRepository repository) {
        ResearchTopic topic = new ResearchTopic();
        topic.setName(name);
        topic.setVacancies(vacancies);
        topic.setResearchLine(researchLine);
        repository.save(topic);
    }
}