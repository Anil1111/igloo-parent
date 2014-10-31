package fr.openwide.core.basicapp.web.application.administration.template;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.common.collect.Lists;

import fr.openwide.core.basicapp.web.application.administration.page.AdministrationBasicUserPortfolioPage;
import fr.openwide.core.basicapp.web.application.administration.page.AdministrationUserGroupPortfolioPage;
import fr.openwide.core.basicapp.web.application.administration.util.AdministrationTypeUser;
import fr.openwide.core.basicapp.web.application.common.template.MainTemplate;
import fr.openwide.core.jpa.security.business.authority.util.CoreAuthorityConstants;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.core.wicket.more.markup.html.template.model.NavigationMenuItem;

@AuthorizeInstantiation(CoreAuthorityConstants.ROLE_ADMIN)
public abstract class AdministrationTemplate extends MainTemplate {

	private static final long serialVersionUID = -5571981353426833725L;

	public AdministrationTemplate(PageParameters parameters) {
		super(parameters);
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("navigation.administration")));
	}

	@Override
	protected List<NavigationMenuItem> getSubNav() {
		return Lists.newArrayList(
				AdministrationTypeUser.BASIC_USER.liste().navigationMenuItem(new ResourceModel("navigation.administration.user.basic")),
				AdministrationTypeUser.TECHNICAL_USER.liste().navigationMenuItem(new ResourceModel("navigation.administration.user.technical")),
				AdministrationUserGroupPortfolioPage.linkDescriptor().navigationMenuItem(new ResourceModel("navigation.administration.usergroup"))
		);
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return AdministrationBasicUserPortfolioPage.class;
	}

	@Override
	protected abstract Class<? extends WebPage> getSecondMenuPage();
}
