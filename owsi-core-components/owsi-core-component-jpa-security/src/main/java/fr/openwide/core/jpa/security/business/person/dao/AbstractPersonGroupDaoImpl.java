package fr.openwide.core.jpa.security.business.person.dao;

import fr.openwide.core.jpa.business.generic.dao.GenericEntityDaoImpl;
import fr.openwide.core.jpa.security.business.person.model.AbstractPerson;
import fr.openwide.core.jpa.security.business.person.model.AbstractPersonGroup;

public abstract class AbstractPersonGroupDaoImpl<G extends AbstractPersonGroup<G, P>, P extends AbstractPerson<P, G>>
		extends GenericEntityDaoImpl<Long, G> implements IPersonGroupDao<G, P> {

	public AbstractPersonGroupDaoImpl() {
		super();
	}
	

}