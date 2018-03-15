package org.iglooproject.basicapp.web.application.navigation.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.iglooproject.basicapp.core.business.user.model.User;
import org.iglooproject.basicapp.web.application.common.template.MainTemplate;
import org.iglooproject.basicapp.web.application.common.typedescriptor.user.AdministrationUserTypeDescriptor;
import org.iglooproject.basicapp.web.application.profile.page.ProfilePage;
import org.iglooproject.wicket.markup.html.basic.CoreLabel;
import org.iglooproject.wicket.more.condition.Condition;
import org.iglooproject.wicket.more.link.descriptor.IPageLinkDescriptor;
import org.iglooproject.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import org.iglooproject.wicket.more.markup.html.template.model.BreadCrumbElement;
import org.iglooproject.wicket.more.model.GenericEntityModel;

public class HomePage extends MainTemplate {

	private static final long serialVersionUID = -6767518941118385548L;

	public static final IPageLinkDescriptor linkDescriptor() {
		return LinkDescriptorBuilder.start()
				.page(HomePage.class);
	}

	public HomePage(PageParameters parameters) {
		super(parameters);
		
		new AttributeModifier("css", () -> "test");
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("home.pageTitle")));
		
		add(new CoreLabel("pageTitle", new ResourceModel("home.pageTitle")));
		
		add(
				AdministrationUserTypeDescriptor.BASIC_USER.list()
						.link("administration")
						.hideIfInvalid(),
				ProfilePage.linkDescriptor()
						.link("profile")
						.hideIfInvalid()
		);
		
		Condition.predicate(GenericEntityModel.of(new User()), (u) -> u.isActive());
	}

	@Override
	protected Condition displayBreadcrumb() {
		return Condition.alwaysFalse();
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return HomePage.class;
	}
}
