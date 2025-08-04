package br.com.experian.buzz.config;

import br.com.experian.buzz.domain.port.AntecipaOnboardingPort;
import br.com.experian.buzz.domain.port.OnboardingRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Classe para debug - verificar se os beans estão sendo criados corretamente.
 */
@Component
public class DebugConfig implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DebugConfig.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== DEBUG: Verificando beans registrados ===");
        
        // Verificar se OnboardingRepositoryPort está registrado
        try {
            OnboardingRepositoryPort repositoryPort = applicationContext.getBean(OnboardingRepositoryPort.class);
            log.info("✅ OnboardingRepositoryPort encontrado: {}", repositoryPort.getClass().getName());
        } catch (Exception e) {
            log.error("❌ OnboardingRepositoryPort NÃO encontrado: {}", e.getMessage());
        }

        // Verificar se AntecipaOnboardingPort está registrado
        try {
            AntecipaOnboardingPort onboardingPort = applicationContext.getBean(AntecipaOnboardingPort.class);
            log.info("✅ AntecipaOnboardingPort encontrado: {}", onboardingPort.getClass().getName());
        } catch (Exception e) {
            log.error("❌ AntecipaOnboardingPort NÃO encontrado: {}", e.getMessage());
        }

        // Listar todos os beans relacionados ao onboarding
        String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
        log.info("=== Beans relacionados ao onboarding ===");
        for (String beanName : beanNames) {
            if (beanName.toLowerCase().contains("onboarding")) {
                Object bean = applicationContext.getBean(beanName);
                log.info("Bean: {} -> {}", beanName, bean.getClass().getName());
            }
        }
        log.info("=== FIM DEBUG ===");
    }
}
