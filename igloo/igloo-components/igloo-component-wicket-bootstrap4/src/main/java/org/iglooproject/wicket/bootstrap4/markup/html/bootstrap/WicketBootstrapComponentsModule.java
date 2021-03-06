package org.iglooproject.wicket.bootstrap4.markup.html.bootstrap;

import org.apache.wicket.model.IModel;
import org.iglooproject.functional.SerializableSupplier2;
import org.iglooproject.wicket.bootstrap4.markup.html.bootstrap.component.BootstrapBadge;
import org.iglooproject.wicket.more.application.IWicketBootstrapComponentsModule;
import org.iglooproject.wicket.more.markup.html.bootstrap.common.renderer.BootstrapRenderer;

@org.springframework.stereotype.Component
public class WicketBootstrapComponentsModule implements IWicketBootstrapComponentsModule {

	@Override
	public <T> SerializableSupplier2<BootstrapBadge<T>> badgeSupplier(String id, IModel<T> model, final BootstrapRenderer<? super T> renderer) {
		return () -> new BootstrapBadge<>(id, model, renderer);
	}

	@Deprecated
	@Override
	public <T> SerializableSupplier2<BootstrapBadge<T>> labelSupplier(String id, IModel<T> model, BootstrapRenderer<? super T> renderer) {
		return () -> new BootstrapBadge<>(id, model, renderer);
	}

}
