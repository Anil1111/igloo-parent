package fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.listfilter;

import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.util.JQueryJavaScriptResourceReference;

public final class ListFilterJavaScriptResourceReference extends JQueryJavaScriptResourceReference {
	private static final long serialVersionUID = 5822555821488465667L;

	private static final ListFilterJavaScriptResourceReference INSTANCE = new ListFilterJavaScriptResourceReference();

	private ListFilterJavaScriptResourceReference() {
		super(ListFilterJavaScriptResourceReference.class, "jquery.listFilter.js");
	}

	public static ListFilterJavaScriptResourceReference get() {
		return INSTANCE;
	}
}
