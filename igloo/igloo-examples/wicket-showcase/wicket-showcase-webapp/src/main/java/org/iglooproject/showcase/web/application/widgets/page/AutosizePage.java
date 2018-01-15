package org.iglooproject.showcase.web.application.widgets.page;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.iglooproject.wicket.more.link.descriptor.IPageLinkDescriptor;
import org.iglooproject.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import org.iglooproject.wicket.more.markup.html.template.js.jquery.plugins.autosize.AutosizeBehavior;
import org.iglooproject.wicket.more.markup.html.template.js.jquery.plugins.more.MoreBehavior;
import org.iglooproject.wicket.more.markup.html.template.model.BreadCrumbElement;

public class AutosizePage extends WidgetsTemplate {

	private static final long serialVersionUID = -4802009584951257187L;

	public static final IPageLinkDescriptor linkDescriptor() {
		return LinkDescriptorBuilder.start()
				.page(AutosizePage.class);
	}

	public AutosizePage(PageParameters parameters) {
		super(parameters);
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("widgets.menu.autosize"), AutosizePage.linkDescriptor()));
		
		TextArea<String> defaultBehavior = new TextArea<String>("defaultBehavior");
		defaultBehavior.add(new AutosizeBehavior());
		add(defaultBehavior);
		
		TextArea<String> withMaxHeight = new TextArea<String>("withMaxHeight");
		withMaxHeight.add(new AutosizeBehavior());
		add(withMaxHeight);
		
		MultiLineLabel multiLineLabel = new MultiLineLabel("multiLineLabel", new ResourceModel("widgets.autosize.more.text"));
		multiLineLabel.add(new MoreBehavior());
		add(multiLineLabel);
	}
	
	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return AutosizePage.class;
	}
}
