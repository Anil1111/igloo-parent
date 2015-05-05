package fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.scrolltotop;

import java.util.List;

import org.apache.wicket.markup.head.HeaderItem;

import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.easing.EasingJavaScriptResourceReference;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.util.AbstractCoreJQueryPluginResourceReference;

public final class ScrollToTopJavaScriptResourceReference extends AbstractCoreJQueryPluginResourceReference {

	private static final long serialVersionUID = 4300908585408220863L;

	private static final ScrollToTopJavaScriptResourceReference INSTANCE = new ScrollToTopJavaScriptResourceReference();

	private ScrollToTopJavaScriptResourceReference() {
		super(ScrollToTopJavaScriptResourceReference.class, "jquery.scrollToTop.js");
	}

	@Override
	protected List<HeaderItem> getPluginDependencies() {
		return forReferences(EasingJavaScriptResourceReference.get());
	}

	public static ScrollToTopJavaScriptResourceReference get() {
		return INSTANCE;
	}

}
