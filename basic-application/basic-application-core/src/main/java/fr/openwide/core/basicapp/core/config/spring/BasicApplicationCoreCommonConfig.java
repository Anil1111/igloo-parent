package fr.openwide.core.basicapp.core.config.spring;

import java.net.MalformedURLException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import fr.openwide.core.basicapp.core.BasicApplicationCorePackage;
import fr.openwide.core.basicapp.core.config.application.BasicApplicationConfigurer;
import fr.openwide.core.spring.config.spring.AbstractApplicationConfig;
import fr.openwide.core.spring.config.spring.annotation.ApplicationDescription;
import fr.openwide.core.spring.config.spring.annotation.ConfigurationLocations;

@Configuration
@ApplicationDescription(name = BasicApplicationCoreCommonConfig.APPLICATION_NAME)
@ConfigurationLocations
@Import({
	BasicApplicationCoreCommonJpaConfig.class,			// configuration de la persistence
	BasicApplicationCoreSecurityConfig.class,			// configuration de la sécurité
	BasicApplicationTaskManagementConfig.class,			// configuration de la gestion des tâches
	BasicApplicationCoreNotificationConfig.class,		// configuration des notifications
	BasicApplicationCoreSchedulingConfig.class,			// configuration des tâches planifiées
	BasicApplicationCoreApplicationPropertyConfig.class	// configuration des propriétés de l'application
})
@ComponentScan(
	basePackageClasses = {
		BasicApplicationCorePackage.class
	},
	// https://jira.springsource.org/browse/SPR-8808
	// on veut charger de manière explicite le contexte ; de ce fait,
	// on ignore l'annotation @Configuration sur le scan de package.
	excludeFilters = @Filter(Configuration.class)
)
// fonctionnement de l'annotation @Transactional
@EnableTransactionManagement
public class BasicApplicationCoreCommonConfig extends AbstractApplicationConfig {

	public static final String APPLICATION_NAME = "basic-application";

	public static final String PROFILE_TEST = "test";
	
	/**
	 * L'obtention du configurer doit être statique.
	 */
	@Bean(name = { "configurer" })
	public static BasicApplicationConfigurer environment(ConfigurableApplicationContext context) throws MalformedURLException {
		return new BasicApplicationConfigurer();
	}
}
