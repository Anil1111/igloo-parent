package fr.openwide.core.showcase.web.application.links.page;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import fr.openwide.core.showcase.core.business.user.service.IUserService;
import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;

public class LinksPage1 extends LinksTemplate {

	private static final long serialVersionUID = -1538563562722555674L;

	public static final IPageLinkDescriptor linkDescriptor() {
		return new LinkDescriptorBuilder()
				.page(LinksPage1.class)
				.build();
	}
	
	@SpringBean
	private IUserService userService;
	
	public LinksPage1(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return LinksPage1.class;
	}

	@Override
	protected IModel<String> getTitleModel() {
		return new ResourceModel("links.page1.pageTitle");
	}

}
