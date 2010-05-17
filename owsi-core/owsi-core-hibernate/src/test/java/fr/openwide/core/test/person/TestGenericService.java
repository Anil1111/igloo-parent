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

import java.util.List;

import org.hibernate.AssertionFailure;
import org.hibernate.TransientObjectException;
import org.hibernate.UnresolvableObjectException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.core.hibernate.exception.SecurityServiceException;
import fr.openwide.core.hibernate.exception.ServiceException;
import fr.openwide.core.test.AbstractHibernateCoreTestCase;
import fr.openwide.core.test.hibernate.example.business.person.model.Person;
import fr.openwide.core.test.hibernate.example.business.person.service.PersonService;

public class TestGenericService extends AbstractHibernateCoreTestCase {

	@Autowired
	PersonService personService;

	@Test
	public void testSaveCreate() throws ServiceException, SecurityServiceException {
		Person person = new Person("Firstname", "Lastname");
		personService.save(person);
		assertNotNull(person.getId());

		Person person1 = new Person("Firstname1", "Lastname1");
		personService.create(person1);
		assertNotNull(person1.getId());

		Person person2 = new Person("Firstname2", "Lastname2");
		personService.create(person2, true);
		assertNotNull(person2.getId());

		try {
			personService.create(person2, true);
			fail("Créer deux fois la même entité doit lever une exception");
		} catch (ServiceException e) {
		}
	}

	@Test
	public void testDelete() throws ServiceException, SecurityServiceException {
		Person person = new Person("Firstname", "Lastname");
		personService.create(person);
		assertTrue(personService.list().contains(person));

		personService.delete(person);
		assertFalse(personService.list().contains(person));

		/*
		 * La suppression d'un objet non persisté ne lève aucune exception
		 */
		Person person1 = new Person("Firstname1", "Lastname1");
		personService.delete(person1);
	}

	@Test
	public void testUpdate() throws ServiceException, SecurityServiceException {
		Person person = new Person("Firstname", "Lastname");
		personService.create(person);
		assertEquals("Firstname", personService.getById(person.getId()).getFirstName());

		person.setFirstName("NewFirstname");
		personService.update(person);
		assertEquals("NewFirstname", personService.getById(person.getId()).getFirstName());
		
		Person person1 = new Person("Firstname1", "Lastname1");
		try {
			personService.update(person1);
			fail("Faire un update sur un objet non persisté doit lever une exception");
		} catch (TransientObjectException e) {	
		}
	}

	@Test
	public void testRefresh() throws ServiceException, SecurityServiceException {
		Person person = new Person("Firstname", "Lastname");
		personService.create(person);

		person.setFirstName("AAAAA");
		assertEquals("AAAAA", person.getFirstName());

		personService.refresh(person);
		assertEquals("Firstname", person.getFirstName());

		Person person1 = new Person("Firstname", "Lastname");

		try {
			personService.refresh(person1);
			fail("Faire un refresh sur un objet avec un identifiant null doit lever une exception");
		} catch (AssertionFailure e) {
		}

		personService.create(person1);
		personService.delete(person1);

		try {
			personService.refresh(person1);
			fail("Faire un refresh sur un objet non persisté doit lever une exception");
		} catch (UnresolvableObjectException e) {
		}
	}

	@Test
	public void testGet() throws ServiceException, SecurityServiceException {
		Person person = new Person("Firstname", "Lastname");
		personService.create(person);

		Person person1 = personService.getEntity(Person.class, person.getId());
		Person person2 = personService.getById(person.getId());

		assertTrue(person.equals(person1));
		assertTrue(person.equals(person2));
	}
	
	@Test
	public void testLists() throws ServiceException, SecurityServiceException {
		cleanAll();
		
		List<Person> emptyList = personService.list();
		
		assertEquals(0, emptyList.size());

		Person person = new Person("Firstname", "Lastname");
		personService.create(person);
		Person person1 = new Person("Firstname1", "Lastname1");
		personService.create(person1);
		
		List<Person> list = personService.list();
		
		assertTrue(list.contains(person));
		assertTrue(list.contains(person1));
	}
	
	@Test
	public void testCount() throws ServiceException, SecurityServiceException {
		cleanAll();
		
		assertEquals(new Long(0), personService.count());
		
		Person person = new Person("Firstname", "Lastname");
		personService.create(person);
		Person person1 = new Person("Firstname1", "Lastname1");
		personService.create(person1);
		
		assertEquals(new Long(2), personService.count());
	}
	
	@Before
	public void init() throws ServiceException, SecurityServiceException {
		super.init();
	}
	
	@After
	public void close() {
		super.close();
	}
}
