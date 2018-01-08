package org.iglooproject.wicket.more.markup.html.template.js.jquery.plugins.bootstrap4.popover;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.wicketstuff.wiquery.core.javascript.JsStatement;

public class BootstrapPopoverBehavior extends Behavior {

	private static final long serialVersionUID = -4381194681563091269L;

	private static final String BOOTSTRAP_POPOVER = "popover";

	private BootstrapPopoverOptions options;

	public BootstrapPopoverBehavior(BootstrapPopoverOptions options) {
		super();
		this.options = options;
	}

	public JsStatement statement(Component component) {
		return new JsStatement().$(component).chain(BOOTSTRAP_POPOVER, options.getJavaScriptOptions(component));
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		response.render(JavaScriptHeaderItem.forReference(BootstrapPopoverJavascriptResourceReference.get()));
		response.render(OnDomReadyHeaderItem.forScript(statement(component).render()));
	}
	
	@Override
	public void detach(Component component) {
		super.detach(component);
		
		if (options != null) {
			options.detach();
		}
	}

}
