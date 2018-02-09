package org.iglooproject.test.rest.jersey2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.iglooproject.jpa.exception.SecurityServiceException;
import org.iglooproject.jpa.exception.ServiceException;
import org.iglooproject.test.jpa.junit.AbstractTestCase;
import org.iglooproject.test.rest.jersey2.business.person.service.IPersonService;
import org.iglooproject.test.rest.jersey2.client.config.spring.RestClientTestCoreCommonConfig;
import org.iglooproject.test.rest.jersey2.context.MockJpaRestServlet;
import org.iglooproject.test.web.context.MockRestServer;
import org.iglooproject.test.web.context.MockServletTestExecutionListener;

@TestExecutionListeners(listeners = MockServletTestExecutionListener.class)
@ContextConfiguration(classes = RestClientTestCoreCommonConfig.class)
@MockRestServer
public abstract class AbstractRestServiceTestCase extends AbstractTestCase {
	
	@Autowired
	private IPersonService personService;
	
	@Autowired
	private MockJpaRestServlet serverResource;

	@Override
	protected void cleanAll() throws ServiceException, SecurityServiceException {
		cleanEntities(personService);
	}

	public MockJpaRestServlet getServerResource() {
		return serverResource;
	}

}