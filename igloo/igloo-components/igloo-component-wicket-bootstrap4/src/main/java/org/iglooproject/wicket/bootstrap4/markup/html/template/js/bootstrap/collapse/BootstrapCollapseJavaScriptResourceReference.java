package org.iglooproject.wicket.bootstrap4.markup.html.template.js.bootstrap.collapse;

import java.util.List;

import org.apache.wicket.markup.head.HeaderItem;
import org.iglooproject.functional.SerializableSupplier2;
import org.iglooproject.wicket.bootstrap4.markup.html.template.js.bootstrap.util.BootstrapUtilJavaScriptResourceReference;
import org.iglooproject.wicket.more.webjars.WebjarUtil;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

public final class BootstrapCollapseJavaScriptResourceReference extends WebjarsJavaScriptResourceReference {

	private static final long serialVersionUID = -1442288640907214154L;

	private static final SerializableSupplier2<List<HeaderItem>> DEPENDENCIES = WebjarUtil.memoizeHeaderItemsforReferences(
			BootstrapUtilJavaScriptResourceReference.get()
	);
	
	private static final BootstrapCollapseJavaScriptResourceReference INSTANCE = new BootstrapCollapseJavaScriptResourceReference();

	private BootstrapCollapseJavaScriptResourceReference() {
		super("bootstrap/current/js/dist/collapse.js");
	}

	@Override
	public List<HeaderItem> getDependencies() {
		return DEPENDENCIES.get();
	}

	public static BootstrapCollapseJavaScriptResourceReference get() {
		return INSTANCE;
	}

}
