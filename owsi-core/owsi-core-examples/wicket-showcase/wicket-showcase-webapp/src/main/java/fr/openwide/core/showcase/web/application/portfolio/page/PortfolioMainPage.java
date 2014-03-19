package fr.openwide.core.showcase.web.application.portfolio.page;

import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.common.collect.Lists;

import fr.openwide.core.showcase.core.business.user.service.IUserService;
import fr.openwide.core.showcase.core.util.spring.ShowcaseConfigurer;
import fr.openwide.core.showcase.web.application.portfolio.component.UserPortfolioPanel;
import fr.openwide.core.showcase.web.application.portfolio.component.UserSearchPanel;
import fr.openwide.core.showcase.web.application.portfolio.model.UserDataProvider;
import fr.openwide.core.showcase.web.application.util.binding.WebappBindings;
import fr.openwide.core.showcase.web.application.util.template.MainTemplate;
import fr.openwide.core.wicket.markup.html.basic.CountLabel;
import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.core.wicket.more.markup.html.template.model.NavigationMenuItem;
import fr.openwide.core.wicket.more.model.BindingModel;

public class PortfolioMainPage extends MainTemplate {
	private static final long serialVersionUID = 6572019030268485555L;
	
	@SpringBean
	private ShowcaseConfigurer showcaseConfigurer;
	
	@SpringBean
	private IUserService userService;
	
	public static IPageLinkDescriptor linkDescriptor() {
		return new LinkDescriptorBuilder()
				.page(PortfolioMainPage.class)
				.build();
	}
	
	public PortfolioMainPage(PageParameters parameters) {
		super(parameters);
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("portfolio.pageTitle"), PortfolioMainPage.linkDescriptor()));
		
		IModel<String> searchTermModel = Model.of("");
		IModel<Boolean> activeModel = Model.of(true);
		
		UserDataProvider userDataProvider = new UserDataProvider(searchTermModel, activeModel);
		
		add(new CountLabel("countLabel", "user.portfolio.userCount", BindingModel.of(userDataProvider, WebappBindings.iBindableDataProvider().size())));
		
		// Porfolio
		UserPortfolioPanel portfolioPanel = new UserPortfolioPanel("userPortfolio", userDataProvider, showcaseConfigurer.getPortfolioItemsPerPage());
		add(portfolioPanel);
		
		// Search
		add(new UserSearchPanel("userSearchPanel", portfolioPanel.getPageable(), searchTermModel, activeModel));
	}
	
	@Override
	protected List<NavigationMenuItem> getSubNav() {
		return Lists.newArrayList();
	}
	
	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return PortfolioMainPage.class;
	}
	
	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return null;
	}
}
