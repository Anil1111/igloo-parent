package org.iglooproject.showcase.web.application.widgets.page;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import org.iglooproject.wicket.behavior.ClassAttributeAppender;
import org.iglooproject.wicket.more.link.descriptor.IPageLinkDescriptor;
import org.iglooproject.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import org.iglooproject.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.popover.BootstrapPopoverBehavior;
import org.iglooproject.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.popover.BootstrapPopoverOptions;
import org.iglooproject.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.tab.BootstrapTabBehavior;
import org.iglooproject.wicket.more.markup.html.template.model.BreadCrumbElement;

public class BootstrapJsPage extends WidgetsTemplate {

	private static final long serialVersionUID = -187415297020105589L;

	public static final IPageLinkDescriptor linkDescriptor() {
		return LinkDescriptorBuilder.start()
				.page(BootstrapJsPage.class);
	}
	
	public BootstrapJsPage(PageParameters parameters) {
		super(parameters);
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("widgets.menu.bootstrapJs"), BootstrapJsPage.linkDescriptor()));
		
		// Popover
		WebMarkupContainer someInformation = new WebMarkupContainer("someInfomration");
		someInformation.setOutputMarkupId(true);
		add(someInformation);
		
		Label someLabelDefault = new Label("someLabelDefault", new ResourceModel("widgets.popover.someLabel.default"));
		BootstrapPopoverOptions popoverOptions = new BootstrapPopoverOptions();
		popoverOptions.setTitleModel(new ResourceModel("widgets.popover.someInformation.title"));
		popoverOptions.setContentComponent(someInformation);
		popoverOptions.setHtml(true);
		someLabelDefault.add(new BootstrapPopoverBehavior(popoverOptions));
		someLabelDefault.add(new ClassAttributeAppender(Model.of("popover-btn")));
		add(someLabelDefault);
		
		Label someLabelLeft = new Label("someLabelLeft", new ResourceModel("widgets.popover.someLabel.left"));
		someLabelLeft.add(new BootstrapPopoverBehavior(popoverOptions));
		someLabelLeft.add(new ClassAttributeAppender(Model.of("popover-btn")));
		add(someLabelLeft);
		
		Label someLabelTop = new Label("someLabelTop", new ResourceModel("widgets.popover.someLabel.top"));
		someLabelTop.add(new BootstrapPopoverBehavior(popoverOptions));
		someLabelTop.add(new ClassAttributeAppender(Model.of("popover-btn")));
		add(someLabelTop);
		
		Label someLabelBottom = new Label("someLabelBottom", new ResourceModel("widgets.popover.someLabel.bottom"));
		someLabelBottom.add(new BootstrapPopoverBehavior(popoverOptions));
		someLabelBottom.add(new ClassAttributeAppender(Model.of("popover-btn")));
		add(someLabelBottom);
		
		Label someLabelWithoutTitle = new Label("someLabelWithoutTitle", new ResourceModel("widgets.popover.someLabel.withoutTitle"));
		BootstrapPopoverOptions popoverWithoutTitleOptions = new BootstrapPopoverOptions();
		popoverWithoutTitleOptions.setContentComponent(someInformation);
		popoverWithoutTitleOptions.setHtml(true);
		someLabelWithoutTitle.add(new BootstrapPopoverBehavior(popoverWithoutTitleOptions));
		someLabelWithoutTitle.add(new ClassAttributeAppender(Model.of("popover-btn")));
		add(someLabelWithoutTitle);
		
		// Tabs
		WebMarkupContainer tabContainer = new WebMarkupContainer("tabContainer");
		add(tabContainer);
		tabContainer.add(new BootstrapTabBehavior());
	}
	
	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return BootstrapJsPage.class;
	}
}
