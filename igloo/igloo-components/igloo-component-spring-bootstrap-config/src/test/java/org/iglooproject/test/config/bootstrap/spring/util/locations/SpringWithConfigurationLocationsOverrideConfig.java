package org.iglooproject.test.config.bootstrap.spring.util.locations;

import org.iglooproject.config.bootstrap.spring.annotations.ConfigurationLocations;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationLocations(locations = "classpath:configuration-app-override.properties", order = 1)
public class SpringWithConfigurationLocationsOverrideConfig {

}
