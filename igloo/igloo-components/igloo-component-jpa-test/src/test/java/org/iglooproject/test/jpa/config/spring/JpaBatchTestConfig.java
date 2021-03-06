package org.iglooproject.test.jpa.config.spring;

import org.iglooproject.config.bootstrap.spring.annotations.ConfigurationLocations;
import org.springframework.context.annotation.Configuration;

/**
 * Added configuration to override some properties
 */
@Configuration
@ConfigurationLocations(locations = {
		"classpath:/igloo-jpa-batch.properties"
}, order = Integer.MAX_VALUE)
public class JpaBatchTestConfig {

}
