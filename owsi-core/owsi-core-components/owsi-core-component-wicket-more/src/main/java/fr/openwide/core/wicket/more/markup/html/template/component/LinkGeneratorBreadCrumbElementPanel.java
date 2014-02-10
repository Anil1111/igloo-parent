package fr.openwide.core.wicket.more.markup.html.template.component;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;

import fr.openwide.core.wicket.more.markup.html.basic.ComponentBooleanProperty;
import fr.openwide.core.wicket.more.markup.html.basic.EnclosureBehavior;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbMarkupTagRenderingBehavior;

public class LinkGeneratorBreadCrumbElementPanel extends GenericPanel<String> {

	private static final long serialVersionUID = 5385792712763242343L;
	
	public LinkGeneratorBreadCrumbElementPanel(String id, BreadCrumbElement breadCrumbElement, BreadCrumbMarkupTagRenderingBehavior renderingBehavior) {
		super(id, breadCrumbElement.getLabelModel());
		
		Link<Void> breadCrumbLink = breadCrumbElement.getLinkGenerator().link("breadCrumbElementLink").setAutoHideIfInvalid(true);
		breadCrumbLink.setBody(getModel());
		breadCrumbLink.add(renderingBehavior);
		add(breadCrumbLink);
		
		add(
				new EnclosureBehavior(ComponentBooleanProperty.VISIBLE).component(breadCrumbLink)
		);
	}
	
}