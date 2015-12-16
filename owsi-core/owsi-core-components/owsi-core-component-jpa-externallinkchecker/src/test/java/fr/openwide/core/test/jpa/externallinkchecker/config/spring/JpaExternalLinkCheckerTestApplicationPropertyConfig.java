package fr.openwide.core.test.jpa.externallinkchecker.config.spring;

import fr.openwide.core.jpa.more.business.parameter.dao.ParameterDaoImpl;
import fr.openwide.core.spring.config.spring.AbstractApplicationPropertyConfig;
import fr.openwide.core.spring.property.dao.IMutablePropertyDao;
import fr.openwide.core.spring.property.service.IPropertyRegistry;

public class JpaExternalLinkCheckerTestApplicationPropertyConfig extends AbstractApplicationPropertyConfig {

	@Override
	protected void register(IPropertyRegistry registry) {
	}

	@Override
	public IMutablePropertyDao mutablePropertyDao() {
		return new ParameterDaoImpl();
	}

}
