/*
 * Copyright (C) 2009-2010 Open Wide
 * Contact: contact@openwide.fr
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.openwide.core.test.person;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;
import fr.openwide.core.test.AbstractJpaCoreTestCase;
import fr.openwide.core.test.jpa.example.business.person.model.Person;
import fr.openwide.core.test.jpa.example.business.person.service.PersonService;

public class TestGenericEntity extends AbstractJpaCoreTestCase {

	@Autowired
	protected PersonService personService;

	@Test
	public void testGenericEntity() throws ServiceException, SecurityServiceException {
		Person person = new Person("FirstName", "LastName");
		
		Assert.assertNull(person.getId());
		Assert.assertTrue(person.isNew());

		personService.create(person);

		Assert.assertFalse(person.isNew());
		
		Long oldId = person.getId();
		
		person.setId(2L);
		Assert.assertEquals(2, person.getId().intValue());

		person.setId(oldId);

		Person person1 = new Person("FirstName1", "LastName1");
		personService.create(person1);

		Person person2 = personService.getById(person.getId());

		Assert.assertFalse(person.equals(person1));
		Assert.assertTrue(person.equals(person2));

		Person person4 = person;

		Assert.assertFalse(person.compareTo(person1) == 0);
		Assert.assertTrue(person.compareTo(person4) == 0);

		Assert.assertEquals("LastName FirstName", person.getDisplayName());
		Assert.assertEquals("LastName FirstName", person.getNameForToString());
	}
	
	@Before
	@Override
	public void init() throws ServiceException, SecurityServiceException {
		super.init();
	}
	
	@After
	@Override
	public void close() throws ServiceException, SecurityServiceException {
		super.close();
	}
}
