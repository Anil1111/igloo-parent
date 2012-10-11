package fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.json;

import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.util.JQueryJavaScriptResourceReference;


public final class JsonJavascriptResourceReference extends JQueryJavaScriptResourceReference {
	private static final long serialVersionUID = 1L;

	private static final JsonJavascriptResourceReference INSTANCE = new JsonJavascriptResourceReference();

	private JsonJavascriptResourceReference() {
		super(JsonJavascriptResourceReference.class, "jquery.json-2.3.js");
	}

	public static JsonJavascriptResourceReference get() {
		return INSTANCE;
	}

}
