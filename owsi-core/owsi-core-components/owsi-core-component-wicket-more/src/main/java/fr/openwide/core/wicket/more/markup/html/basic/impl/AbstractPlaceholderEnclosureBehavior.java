package fr.openwide.core.wicket.more.markup.html.basic.impl;

import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import com.google.common.base.Predicate;

import fr.openwide.core.wicket.more.markup.html.basic.AbstractComponentBooleanPropertyBehavior;
import fr.openwide.core.wicket.more.markup.html.basic.ComponentBooleanProperty;
import fr.openwide.core.wicket.more.markup.html.basic.EnclosureBehavior;
import fr.openwide.core.wicket.more.markup.html.basic.IPlaceholderEnclosureBuilder;
import fr.openwide.core.wicket.more.markup.html.basic.PlaceholderBehavior;
import fr.openwide.core.wicket.more.markup.html.basic.impl.PlaceholderEnclosureVisibilityBuilder.Visibility;

public abstract class AbstractPlaceholderEnclosureBehavior<T extends AbstractPlaceholderEnclosureBehavior<T>>
		extends AbstractComponentBooleanPropertyBehavior
		implements IPlaceholderEnclosureBuilder<T> {

	private static final long serialVersionUID = 5054905572454226562L;
	
	private final PlaceholderEnclosureVisibilityBuilder visibilityBuilder;
	
	protected AbstractPlaceholderEnclosureBehavior(ComponentBooleanProperty property, Visibility visibility) {
		super(property);
		this.visibilityBuilder = new PlaceholderEnclosureVisibilityBuilder(visibility);
	}
	
	@Override
	protected boolean generatePropertyValue(Component component) {
		return visibilityBuilder.isVisible();
	}
	
	/**
	 * @return this as an object of type T
	 * @see PlaceholderBehavior
	 * @see EnclosureBehavior
	 */
	protected abstract T thisAsT();
	
	@Override
	public T collectionModel(IModel<? extends Collection<?>> model) {
		visibilityBuilder.collectionModel(model);
		return thisAsT();
	}
	
	@Override
	public T model(IModel<?> model) {
		visibilityBuilder.model(model);
		return thisAsT();
	}
	
	@Override
	public <T2> T model(Predicate<? super T2> predicate, IModel<? extends T2> model) {
		visibilityBuilder.model(predicate, model);
		return thisAsT();
	}
	
	@Override
	public T models(IModel<?> firstModel, IModel<?>... otherModels) {
		visibilityBuilder.models(firstModel, otherModels);
		return thisAsT();
	}
	
	@Override
	@SafeVarargs
	public final <T2> T models(Predicate<? super T2> predicate, IModel<? extends T2> firstModel, IModel<? extends T2>... otherModels) {
		visibilityBuilder.models(predicate, firstModel, otherModels);
		return thisAsT();
	}
	
	@Override
	public T component(Component component) {
		visibilityBuilder.component(component);
		return thisAsT();
	}
	
	@Override
	public T components(Component firstComponent, Component... otherComponents) {
		visibilityBuilder.components(firstComponent, otherComponents);
		return thisAsT();
	}
	
	@Override
	public T components(Collection<Component> components) {
		visibilityBuilder.components(components);
		return thisAsT();
	}
	
	@Override
	public void detach(Component component) {
		super.detach(component);
		
		visibilityBuilder.detach();
	}

}
