package org.iglooproject.wicket.more.markup.html.template.js.jquery.plugins.modal;

import org.wicketstuff.wiquery.core.options.Options;

/**
 * @see ModalJavaScriptResourceReference
 */
@Deprecated
public class ModalImage implements IModal {

	private static final long serialVersionUID = -3129899251673064005L;

	@Override
	public String chainLabel() {
		return "fancybox";
	}

	@Override
	public CharSequence[] statementArgs() {
		Options options = new Options();
		options.putLiteral("type", "image");
		return new CharSequence[] { options.getJavaScriptOptions() };
	}
}
