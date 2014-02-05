package fr.openwide.core.jpa.security.business.person.util;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;
import fr.openwide.core.jpa.business.generic.util.AbstractGenericEntityComparator;
import fr.openwide.core.jpa.security.business.person.model.GenericUserGroup;

public class AbstractPersonGroupComparator extends AbstractGenericEntityComparator<Long, GenericUserGroup<?, ?>> {

	private static final long serialVersionUID = -8477673271287138297L;
	
	public static final AbstractPersonGroupComparator INSTANCE = new AbstractPersonGroupComparator();
	
	public static AbstractPersonGroupComparator get() {
		return INSTANCE;
	}
	
	@Override
	protected int compareNotNullObjects(GenericUserGroup<?, ?> left, GenericUserGroup<?, ?> right) {
		int order = GenericEntity.DEFAULT_STRING_COLLATOR.compare(left.getName(), right.getName());
		if (order == 0) {
			order = super.compareNotNullObjects(left, right);
		}
		return order;
	}
	
}
