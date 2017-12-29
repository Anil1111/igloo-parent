package org.iglooproject.jpa.security.config.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultJpaSecurityConfig {

	@Value("${security.runAsKey}")
	private String runAsKey;

	@Value("${security.passwordSalt}")
	private String passwordSalt;

	public String getRunAsKey() {
		return runAsKey;
	}

	public String getPasswordSalt() {
		return passwordSalt;
	}

}
