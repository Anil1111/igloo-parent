package fr.openwide.core.showcase.web.application.links.page;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;

public class LinksPage3 extends LinksTemplate {

	private static final long serialVersionUID = 789639308894032436L;

	public static final IPageLinkDescriptor linkDescriptor() {
		return LinkDescriptorBuilder.start()
				.page(LinksPage3.class);
	}
	
	public LinksPage3(PageParameters parameters) {
		super(parameters);
	}

	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return LinksPage3.class;
	}

	@Override
	protected IModel<String> getTitleModel() {
		return new ResourceModel("links.page3.pageTitle");
	}

}
