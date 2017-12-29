package org.iglooproject.wicket.more.markup.html.template.js.jquery.plugins.json;

import org.iglooproject.wicket.more.markup.html.template.js.jquery.plugins.util.AbstractCoreJQueryPluginResourceReference;


public final class JsonJavascriptResourceReference extends AbstractCoreJQueryPluginResourceReference {
	private static final long serialVersionUID = 1L;

	private static final JsonJavascriptResourceReference INSTANCE = new JsonJavascriptResourceReference();

	private JsonJavascriptResourceReference() {
		super(JsonJavascriptResourceReference.class, "jquery.json-2.3.js");
	}

	public static JsonJavascriptResourceReference get() {
		return INSTANCE;
	}

}
