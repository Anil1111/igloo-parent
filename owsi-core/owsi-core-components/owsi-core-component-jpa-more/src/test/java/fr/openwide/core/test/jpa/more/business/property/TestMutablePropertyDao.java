package fr.openwide.core.test.jpa.more.business.property;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.spring.property.dao.IMutablePropertyDao;
import fr.openwide.core.spring.property.model.MutablePropertyId;
import fr.openwide.core.spring.property.service.IConfigurablePropertyService;
import fr.openwide.core.test.jpa.more.business.AbstractJpaMoreTestCase;

public class TestMutablePropertyDao extends AbstractJpaMoreTestCase {

	@Autowired
	private IConfigurablePropertyService configurablePropertyService;

	@Autowired
	private IMutablePropertyDao mutablePropertyDao;

	@Test
	public void mutableProperty() throws ServiceException, SecurityServiceException {
		MutablePropertyId<String> mutableProperty = new MutablePropertyId<>("test.property.string.value");
		configurablePropertyService.registerString(mutableProperty, "MyDefaultValue");
		Assert.assertEquals(null, mutablePropertyDao.getInTransaction("test.property.string.value"));
		configurablePropertyService.set(mutableProperty, "MyValue");
		Assert.assertEquals("MyValue", mutablePropertyDao.getInTransaction("test.property.string.value"));
	}

}
