package fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.autosize;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.wicketstuff.wiquery.core.javascript.JsStatement;

public class AutosizeBehavior extends Behavior {

	private static final long serialVersionUID = 6155882407495564466L;
	
	private static final String NEW_LINE = "\\n";
	
	public AutosizeBehavior() {
		super();
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		response.render(JavaScriptHeaderItem.forReference(AutosizeJavaScriptResourceReference.get()));
		response.render(OnDomReadyHeaderItem.forScript(new JsStatement().$(component).chain(new Autosize().append(NEW_LINE)).render()));
	}

}
