package fr.openwide.core.wicket.more.model.threadsafe;

import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.wicket.Session;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.business.generic.model.GenericEntityReference;
import fr.openwide.core.jpa.business.generic.model.GenericEntityReference;
import fr.openwide.core.jpa.business.generic.service.IEntityService;
import fr.openwide.core.jpa.util.HibernateUtils;
import fr.openwide.core.wicket.more.model.GenericEntityModel;

/**
 * An alternative implementation of {@link GenericEntityModel} that is thread-safe, and may thus be used in multiple request cycles at the same time.
 * <p>This class should be used when entity models are needed in a global object, such as the {@link Session wicket session}.
 * 
 * @see SessionThreadSafeDerivedSerializableStateLoadableDetachableModel
 */
public class SessionThreadSafeGenericEntityModel<K extends Serializable & Comparable<K>, E extends GenericEntity<K, ?>>
		extends SessionThreadSafeDerivedSerializableStateLoadableDetachableModel<E, SessionThreadSafeGenericEntityModel.SerializableState<K, E>> {
	
	private static final long serialVersionUID = 1L;

	@SpringBean
	private IEntityService entityService;

	public SessionThreadSafeGenericEntityModel() {
		super();
		Injector.get().inject(this);
	}

	public SessionThreadSafeGenericEntityModel(E object) {
		super(object);
		Injector.get().inject(this);
	}

	@Override
	protected E load(SerializableState<K, E> serializableState) {
		if (serializableState == null) {
			return null;
		}
		
		GenericEntityReference<K, E> persistedEntityReference = serializableState.persistedEntityReference;
		
		if (persistedEntityReference != null) {
			return entityService.getEntity(persistedEntityReference);
		} else {
			return serializableState.notYetPersistedEntity;
		}
	}

	@Override
	protected SerializableState<K, E> makeSerializable(E currentObject) {
		return new SerializableState<K, E>(currentObject);
	}

	protected static class SerializableState<K extends Serializable & Comparable<K>, E extends GenericEntity<K, ?>> implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private final GenericEntityReference<K, E> persistedEntityReference;
		/**
		 * L'objectif est ici de stocker les entités qui n'ont pas encore été persistées en base (typiquement, quand
		 * on fait la création).
		 */
		private final E notYetPersistedEntity;
		
		public SerializableState(E entity) {
			E unwrapped = HibernateUtils.unwrap(entity);
			if (unwrapped != null) {
				if (unwrapped.getId() != null) {
					persistedEntityReference = GenericEntityReference.of(unwrapped);
					notYetPersistedEntity = null;
				} else {
					persistedEntityReference = null;
					notYetPersistedEntity = unwrapped;
				}
			} else {
				persistedEntityReference = null;
				notYetPersistedEntity = null;
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof SerializableState)) {
				return false;
			}
			SerializableState<K, E> other = (SerializableState<K, E>) obj;
			
			return new EqualsBuilder()
					.append(persistedEntityReference, other.persistedEntityReference)
					.append(notYetPersistedEntity, other.notYetPersistedEntity)
					.build();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder()
					.append(persistedEntityReference)
					.append(notYetPersistedEntity)
					.toHashCode();
		}
	}

}
