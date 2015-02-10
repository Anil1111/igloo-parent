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

package fr.openwide.core.wicket.more.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.business.generic.model.GenericEntityReference;
import fr.openwide.core.jpa.business.generic.model.GenericEntityReference;
import fr.openwide.core.jpa.business.generic.service.IEntityService;
import fr.openwide.core.jpa.util.HibernateUtils;

public class GenericEntityModel<K extends Serializable & Comparable<K>, E extends GenericEntity<K, ?>>
		extends LoadableDetachableModel<E> {
	
	private static final long serialVersionUID = 1L;

	@SpringBean
	private IEntityService entityService;
	
	private transient boolean attached = false;

	private GenericEntityReference<K, E> persistedEntityReference;

	/**
	 * L'objectif est ici de stocker les entités qui n'ont pas encore été persistées en base (typiquement, quand
	 * on fait la création).
	 */
	private E notYetPersistedEntity;
	
	public static <K extends Serializable & Comparable<K>, E extends GenericEntity<K, ?>> GenericEntityModel<K, E> of(E entity) {
		return new GenericEntityModel<K, E>(entity);
	}
	
	/**
	 * Construit un GenericEntityModel dont l'objet est <code>null</code> et qui n'est pas attaché.
	 */
	public GenericEntityModel() {
		Injector.get().inject(this);
	}
	
	public GenericEntityModel(E entity) {
		this();
		
		setObject(entity);
	}

	@Override
	protected E load() {
		E result = null;
		if (persistedEntityReference != null) {
			result = HibernateUtils.unwrap(entityService.getEntity(persistedEntityReference));
		} else {
			result = notYetPersistedEntity;
		}
		attached = true;
		return result;
	}
	
	@Override
	public void setObject(E entity) {
		E persistentObject = HibernateUtils.unwrap(entity);
		super.setObject(persistentObject);
		attached = true;
		updateSerializableData(); // Useless ; for compatibility with old applications only
	}
	
	protected K getId() {
		return persistedEntityReference != null ? persistedEntityReference.getEntityId() : null;
	}

	@Override
	public void detach() {
		if (!attached) {
			return;
		}
		updateSerializableData();
		super.detach();
		attached = false;
	}
	
	/**
	 * Updates the serializable data (id, clazz, notYetPersistedEntity) according to the attached object's value.
	 * <p>Only called when the model is attached.
	 */
	private void updateSerializableData() {
		E entity = super.getObject();
		if (entity != null) {
			if (entity.getId() != null) {
				persistedEntityReference = GenericEntityReference.of(entity);
				notYetPersistedEntity = null;
			} else {
				persistedEntityReference = null;
				notYetPersistedEntity = entity;
			}
		} else {
			persistedEntityReference = null;
			notYetPersistedEntity = null;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof GenericEntityModel)) {
			return false;
		}
		GenericEntityModel<?, ?> other = (GenericEntityModel<?, ?>) obj;
		return new EqualsBuilder()
				.append(persistedEntityReference, other.persistedEntityReference)
				.append(notYetPersistedEntity, other.notYetPersistedEntity)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(persistedEntityReference)
				.append(notYetPersistedEntity)
				.toHashCode();
	}

}
