/*
 * Copyright (C) 2009-2011 Open Wide
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

package fr.openwide.core.jpa.more.business.generic.service;

import java.util.Comparator;
import java.util.List;

import javax.persistence.NonUniqueResultException;

import fr.openwide.core.jpa.business.generic.service.ITransactionalAspectAwareService;
import fr.openwide.core.jpa.more.business.generic.model.GenericListItem;

public interface IGenericListItemService extends ITransactionalAspectAwareService {
	
	<E extends GenericListItem<?>> E getById(Class<E> clazz, Long id);

	<E extends GenericListItem<?>> void create(E entity);

	<E extends GenericListItem<?>> void update(E entity);

	<E extends GenericListItem<?>> void delete(E entity);

	<E extends GenericListItem<?>> List<E> list(Class<E> clazz);

	<E extends GenericListItem<?>> List<E> list(Class<E> clazz, Comparator<? super E> comparator);

	<E extends GenericListItem<?>> long count(Class<E> clazz);

	<E extends GenericListItem<?>> List<E> listEnabled(Class<E> clazz);

	<E extends GenericListItem<?>> List<E> listEnabled(Class<E> clazz, Comparator<? super E> comparator);

	/**
	 * WARNING: only use this if unique constraints were set on the label column of {@code source}.
	 */
	<E extends GenericListItem<?>> E getByLabel(Class<E> clazz, String label) throws NonUniqueResultException;
	
	/**
	 * WARNING: only use this if unique constraints were set on the label column of {@code source}.
	 */
	<E extends GenericListItem<?>> E getByShortLabel(Class<E> clazz, String shortLabel) throws NonUniqueResultException;

}
