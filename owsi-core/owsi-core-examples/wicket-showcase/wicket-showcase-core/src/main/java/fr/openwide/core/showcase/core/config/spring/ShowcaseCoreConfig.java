package fr.openwide.core.showcase.core.config.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import fr.openwide.core.showcase.core.ShowcaseCorePackage;
import fr.openwide.core.showcase.core.init.BootstrapApplicationServiceImpl;
import fr.openwide.core.showcase.core.util.spring.ShowcaseConfigurer;
import fr.openwide.core.spring.config.spring.AbstractApplicationConfig;
import fr.openwide.core.spring.config.spring.annotation.ApplicationDescription;
import fr.openwide.core.spring.config.spring.annotation.ConfigurationLocations;

@Configuration
@ApplicationDescription(name = ShowcaseCoreConfig.APPLICATION_NAME)
@ConfigurationLocations
@Import({
	ShowcaseCoreJpaConfig.class,						// configuration de la persistence
	ShowcaseCoreSecurityConfig.class,					// configuration de la sécurité
	ShowcaseCoreJpaMoreTaskManagementConfig.class,		// configuration des tâches
	ShowcaseCoreApplicationPropertyConfig.class,		// configuration des propriétés de l'application
})
@ComponentScan(
		basePackageClasses = {
				ShowcaseCorePackage.class
		},
		excludeFilters = @Filter(Configuration.class)
)
// fonctionnement de l'annotation @Transactional
@EnableTransactionManagement
public class ShowcaseCoreConfig extends AbstractApplicationConfig {

	public static final String APPLICATION_NAME = "showcase";
	
	public static final String PROFILE_TEST = "test";
	
	@Bean(name = {"showcaseConfigurer", "configurer"})
	public static ShowcaseConfigurer configurer() {
		return new ShowcaseConfigurer();
	}
	
	@Bean
	public BootstrapApplicationServiceImpl bootstrapApplicationService() {
		return new BootstrapApplicationServiceImpl();
	}

}
