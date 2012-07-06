package fr.openwide.core.jpa.more.spring.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import fr.openwide.core.jpa.config.spring.DefaultJpaConfig;
import fr.openwide.core.jpa.config.spring.JpaConfigUtils;


public abstract class AbstractConfiguredJpaMoreConfig extends AbstractJpaMoreConfig {

	@Autowired
	private DefaultJpaConfig defaultJpaConfig;

	@Override
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		return JpaConfigUtils.entityManagerFactory(defaultJpaConfig.defaultJpaCoreConfigurationProvider());
	}

	@Override
	public DataSource dataSource() {
		return JpaConfigUtils.dataSource(defaultJpaConfig.defaultTomcatPoolConfigurationProvider());
	}

}
