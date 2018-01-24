package org.iglooproject.wicket.bootstrap4.console.template;

import static org.iglooproject.spring.property.SpringPropertyIds.VERSION;
import static org.iglooproject.wicket.more.property.WicketMorePropertyIds.CONSOLE_GLOBAL_FEEDBACK_AUTOHIDE_DELAY_UNIT;
import static org.iglooproject.wicket.more.property.WicketMorePropertyIds.CONSOLE_GLOBAL_FEEDBACK_AUTOHIDE_DELAY_VALUE;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.iglooproject.jpa.more.business.upgrade.service.IAbstractDataUpgradeService;
import org.iglooproject.jpa.security.business.person.model.GenericUser;
import org.iglooproject.spring.property.service.IPropertyService;
import org.iglooproject.wicket.behavior.ClassAttributeAppender;
import org.iglooproject.wicket.bootstrap4.markup.html.template.js.bootstrap.collapse.BootstrapCollapseJavaScriptResourceReference;
import org.iglooproject.wicket.bootstrap4.markup.html.template.js.bootstrap.dropdown.BootstrapDropDownJavaScriptResourceReference;
import org.iglooproject.wicket.markup.html.basic.CoreLabel;
import org.iglooproject.wicket.more.AbstractCoreSession;
import org.iglooproject.wicket.more.console.common.model.ConsoleMenuItem;
import org.iglooproject.wicket.more.console.common.model.ConsoleMenuSection;
import org.iglooproject.wicket.more.markup.html.feedback.AnimatedGlobalFeedbackPanel;
import org.iglooproject.wicket.more.markup.html.template.AbstractWebPageTemplate;
import org.iglooproject.wicket.more.markup.html.template.js.bootstrap.dropdown.BootstrapDropdownBehavior;
import org.iglooproject.wicket.more.markup.html.template.js.bootstrap.tooltip.BootstrapTooltip;
import org.iglooproject.wicket.more.markup.html.template.js.bootstrap.tooltip.BootstrapTooltipDocumentBehavior;
import org.iglooproject.wicket.more.markup.html.template.js.jquery.plugins.bootstraphoverdropdown.BootstrapHoverDropdownBehavior;
import org.iglooproject.wicket.more.markup.html.template.js.jquery.plugins.scrolltotop.ScrollToTopBehavior;
import org.iglooproject.wicket.more.markup.html.template.model.BreadCrumbElement;
import org.iglooproject.wicket.more.model.ApplicationPropertyModel;
import org.iglooproject.wicket.more.security.page.LogoutPage;

public abstract class ConsoleTemplate extends AbstractWebPageTemplate {
	
	private static final long serialVersionUID = -477123413708677528L;
	
	@SpringBean
	protected IPropertyService propertyService;
	
	@SpringBean(required = false)
	protected IAbstractDataUpgradeService dataUpgradeService;
	
	public ConsoleTemplate(PageParameters parameters) {
		super(parameters);
		
		// Page title
		addHeadPageTitlePrependedElement(new BreadCrumbElement(new ResourceModel("common.rootPageTitle")));
		add(createHeadPageTitle("headPageTitle"));
		
		add(new AnimatedGlobalFeedbackPanel("animatedGlobalFeedbackPanel",
				propertyService.get(CONSOLE_GLOBAL_FEEDBACK_AUTOHIDE_DELAY_VALUE),
				propertyService.get(CONSOLE_GLOBAL_FEEDBACK_AUTOHIDE_DELAY_UNIT))
		);
		
		// Navbar
		add(new ListView<ConsoleMenuSection>("mainNav", ConsoleConfiguration.get().getMenuSections()) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<ConsoleMenuSection> item) {
				ConsoleMenuSection navItem = item.getModelObject();
				
				if (navItem.getPageClass() != null && navItem.getPageClass().equals(ConsoleTemplate.this.getFirstMenuPage())) {
					item.add(new ClassAttributeAppender("active"));
				}
				
				AbstractLink navLink = new BookmarkablePageLink<Void>("navLink", navItem.getPageClass());
				
				item.add(
						navLink
								.add(
										new CoreLabel("label", new ResourceModel(navItem.getDisplayStringKey()))
								)
				);
				
				List<ConsoleMenuItem> subMenuItems = navItem.getMenuItems();
				
				if (!subMenuItems.isEmpty()) {
					item.add(new ClassAttributeAppender("dropdown"));
					navLink.add(new ClassAttributeAppender("dropdown-toggle"));
					navLink.add(new AttributeModifier("data-toggle", "dropdown"));
					navLink.add(new AttributeModifier("data-hover", "dropdown"));
				}
				
				item.add(
						new WebMarkupContainer("subNavContainer")
								.add(
										new ListView<ConsoleMenuItem>("subNav", subMenuItems) {
											private static final long serialVersionUID = 1L;
											
											@Override
											protected void populateItem(ListItem<ConsoleMenuItem> item) {
												ConsoleMenuItem navItem = item.getModelObject();
												
												AbstractLink navLink = new BookmarkablePageLink<Void>("navLink", navItem.getPageClass());
												
												navLink.add(
														new Label("label", new ResourceModel(navItem.getDisplayStringKey()))
												);
												
												if (navItem.getPageClass() != null && navItem.getPageClass().equals(ConsoleTemplate.this.getFirstMenuPage())) {
													navLink.add(new ClassAttributeAppender("active"));
												}
												
												item.add(navLink);
											}
										}
								)
								.setVisibilityAllowed(!subMenuItems.isEmpty())
				);
			}
		});
		
		add(new CoreLabel("userFullName", new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected String load() {
				String userFullName = null;
				GenericUser<?, ?> user = AbstractCoreSession.get().getUser();
				if (user != null) {
					userFullName = user.getFullName();
				}
				return userFullName;
			}
		}).hideIfEmpty());
		
		add(new BookmarkablePageLink<Void>("logoutLink", LogoutPage.class));
		
		add(ConsoleConfiguration.get().getConsoleHeaderAdditionalContentFactory().create("headerAdditionalContent"));
		
		add(new Label("version", ApplicationPropertyModel.of(VERSION)));
		
		add(new BootstrapTooltipDocumentBehavior(getBootstrapTooltip()));
		
		add(new BootstrapDropdownBehavior());
		add(new BootstrapHoverDropdownBehavior());
		
		add(new WebMarkupContainer("scrollToTop").add(new ScrollToTopBehavior()));
	}
	
	private BootstrapTooltip getBootstrapTooltip() {
		BootstrapTooltip bootstrapTooltip = new BootstrapTooltip();
		bootstrapTooltip.setSelector("[title],[data-original-title]");
		bootstrapTooltip.setAnimation(true);
		bootstrapTooltip.setContainer("body");
		return bootstrapTooltip;
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		for (ResourceReference cssResourceReference : ConsoleConfiguration.get().getCssResourcesReferences()) {
			response.render(CssHeaderItem.forReference(cssResourceReference));
		}
		response.render(JavaScriptHeaderItem.forReference(BootstrapCollapseJavaScriptResourceReference.get()));
		response.render(JavaScriptHeaderItem.forReference(BootstrapDropDownJavaScriptResourceReference.get()));
	}

	@Override
	public String getVariation() {
		// La console en BS4 quoi qu'il arrive, il ne faut pas laisser l'application qui l'utilise choisir la version.
		return AbstractWebPageTemplate.BOOTSTRAP4_VARIATION;
	}
}
