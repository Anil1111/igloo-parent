package org.iglooproject.test.jpa.externallinkchecker.business.rest.server.config.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.iglooproject.spring.config.spring.AbstractApplicationConfig;
import org.iglooproject.config.bootstrap.spring.annotations.ApplicationDescription;
import org.iglooproject.config.bootstrap.spring.annotations.ConfigurationLocations;
import org.iglooproject.test.config.spring.ConfigurationPropertiesUrlConstants;
import org.iglooproject.test.jpa.externallinkchecker.business.rest.server.RestServerPackage;

/**
 * This configuration relies on bootstrap-configuration.properties enforcing igloo.profile=test
 */
@Configuration
@ApplicationDescription(name = "rest-test-server")
@ConfigurationLocations(locations = {
		ConfigurationPropertiesUrlConstants.JPA_COMMON,
		"classpath:rest-server.properties"
})
@Import({
	RestServerTestApplicationPropertyConfig.class
})
@ComponentScan(
		basePackageClasses = { RestServerPackage.class },
		excludeFilters = @Filter(Configuration.class)
)
// fonctionnement de l'annotation @Transactional
@EnableTransactionManagement
public class RestServerTestCoreCommonConfig extends AbstractApplicationConfig {
}