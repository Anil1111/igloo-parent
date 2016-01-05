package fr.openwide.core.wicket.more.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IComponentAssignedModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;

import fr.openwide.core.wicket.more.util.model.Detachables;

public abstract class LocaleAwareReadOnlyModel<T> extends AbstractReadOnlyModel<T> implements IComponentAssignedModel<T> {

	private static final long serialVersionUID = 5634025837567024511L;

	@Override
	public T getObject() {
		return getObject(Session.get().getLocale());
	}

	public abstract T getObject(Locale locale);

	@Override
	public IWrapModel<T> wrapOnAssignment(Component component) {
		return new WrapModel(component);
	}

	private class WrapModel extends AbstractReadOnlyModel<T> implements IWrapModel<T> {
		private static final long serialVersionUID = -1080017215611311157L;
		
		private final Component component;
		
		public WrapModel(Component component) {
			super();
			this.component = checkNotNull(component);
		}
		
		@Override
		public T getObject() {
			return LocaleAwareReadOnlyModel.this.getObject(component.getLocale());
		}
		
		@Override
		public IModel<?> getWrappedModel() {
			return LocaleAwareReadOnlyModel.this;
		}
		
		@Override
		public void detach() {
			super.detach();
			Detachables.detach(getWrappedModel());
		}
	}

}
