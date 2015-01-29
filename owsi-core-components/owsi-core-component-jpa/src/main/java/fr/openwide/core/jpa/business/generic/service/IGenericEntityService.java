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

package fr.openwide.core.jpa.business.generic.service;

import java.io.Serializable;
import java.util.List;

import fr.openwide.core.jpa.business.generic.annotation.PermissionObject;
import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.business.generic.model.GenericEntityReference;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.exception.ServiceException;

/**
 * <p>Service racine pour la gestion des entités.</p>
 *
 * @author Open Wide
 *
 * @param <T> type d'entité
 */
public interface IGenericEntityService<K extends Serializable & Comparable<K>, E extends GenericEntity<K, ?>>
		extends ITransactionalAspectAwareService {

	/**
	 * @deprecated Si vous avez besoin de faire des sauvegardes sans passer dans le createEntity, implémenter une
	 * méthode spécifique avec un nommage adapté. La création sans passer par le createEntity doit être l'exception.
	 * 
	 * Crée l'entité dans la base de données. Mis à part dans les tests pour faire des sauvegardes simples, utiliser
	 * create() car il est possible qu'il y ait des listeners sur la création d'une entité.
	 * 
	 * @param entity entité
	 */
	@Deprecated
	void save(@PermissionObject E entity) throws ServiceException, SecurityServiceException;
	
	/**
	 * Met à jour l'entité dans la base de données.
	 * 
	 * @param entity entité
	 */
	void update(@PermissionObject E entity) throws ServiceException, SecurityServiceException;
	
	/**
	 * Crée l'entité dans la base de données.
	 * 
	 * @param entity entité
	 */
	void create(@PermissionObject E entity) throws ServiceException, SecurityServiceException;

	/**
	 * Supprime l'entité de la base de données
	 * 
	 * @param entity entité
	 */
	void delete(@PermissionObject E entity) throws ServiceException, SecurityServiceException;
	
	/**
	 * Rafraîchit l'entité depuis la base de données
	 * 
	 * @param entity entité
	 */
	E refresh(@PermissionObject E entity);
	
	/**
	 * Retourne une entité à partir de sa classe et son id.
	 * 
	 * @deprecated Privilégier {@link #getById(Class, Serializable)}, qui renvoie le type demandé.
	 * 
	 * @param clazz classe
	 * @param id identifiant
	 * @return entité
	 */
	@Deprecated
	E getEntity(Class<? extends E> clazz, K id);
	
	
	/**
	 * Retourne une entité à partir de son id.
	 * 
	 * @param id identifiant
	 * @return entité
	 */
	E getById(@PermissionObject K id);
	
	/**
	 * Retourne une entité à partir de sa classe (dérivée de {@link E}) et de son id.
	 * 
	 * @param id identifiant
	 * @return entité
	 */
	<T extends E> T getById(Class<T> clazz, K id);
	
	/**
	 * Retourne une entité à partir d'une référence.
	 * 
	 * @param reference
	 * @return entité
	 */
	<T extends E> T getById(GenericEntityReference<K, T> reference);
	
	/**
	 * Renvoie la liste de l'ensemble des entités de ce type.
	 * 
	 * @return liste d'entités
	 */
	List<E> list();
	
	/**
	 * Compte le nombre d'entités de ce type présentes dans la base.
	 * 
	 * @return nombre d'entités
	 */
	Long count();
	
	/**
	 * Flushe la session.
	 */
	void flush();

	void clear();
}
