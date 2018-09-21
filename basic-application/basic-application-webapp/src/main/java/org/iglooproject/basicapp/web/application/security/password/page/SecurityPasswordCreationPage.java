package org.iglooproject.basicapp.web.application.security.password.page;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.iglooproject.basicapp.core.business.user.model.User;
import org.iglooproject.basicapp.web.application.BasicApplicationApplication;
import org.iglooproject.basicapp.web.application.BasicApplicationSession;
import org.iglooproject.basicapp.web.application.security.password.component.SecurityPasswordCreationContentPanel;
import org.iglooproject.basicapp.web.application.security.password.template.SecurityPasswordTemplate;
import org.iglooproject.wicket.more.link.descriptor.IPageLinkDescriptor;
import org.iglooproject.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import org.iglooproject.wicket.more.link.descriptor.mapper.ITwoParameterLinkDescriptorMapper;
import org.iglooproject.wicket.more.link.descriptor.parameter.CommonParameters;
import org.iglooproject.wicket.more.markup.html.template.model.BreadCrumbElement;
import org.iglooproject.wicket.more.model.GenericEntityModel;
import org.iglooproject.wicket.more.util.model.Detachables;

public class SecurityPasswordCreationPage extends SecurityPasswordTemplate {

	private static final long serialVersionUID = 1L;
	
	public static final ITwoParameterLinkDescriptorMapper<IPageLinkDescriptor, User, String> MAPPER = 
			LinkDescriptorBuilder.start()
					.model(User.class)
					.model(String.class)
					.pickFirst().map(CommonParameters.ID).mandatory()
					.pickSecond().map(CommonParameters.TOKEN).mandatory()
					.page(SecurityPasswordCreationPage.class);

	private final IModel<User> userModel = new GenericEntityModel<Long, User>();

	public SecurityPasswordCreationPage(PageParameters parameters) {
		super(parameters);
		
		// Being connected here doesn't make any sense
		if (BasicApplicationSession.get().isSignedIn()) {
			BasicApplicationSession.get().invalidate();
		}
		
		final IModel<String> tokenModel = Model.of("");
		
		MAPPER.map(userModel, tokenModel).extractSafely(
				parameters, 
				BasicApplicationApplication.get().getHomePageLinkDescriptor(),
				getString("common.error.unexpected")
		);
		
		if (!tokenModel.getObject().equals(userModel.getObject().getPasswordRecoveryRequest().getToken())) {
			Session.get().error(getString("security.password.creation.wrongToken"));
			throw BasicApplicationApplication.get().getHomePageLinkDescriptor().newRestartResponseException();
		}
		
		addHeadPageTitlePrependedElement(
				new BreadCrumbElement(new ResourceModel("security.password.creation.pageTitle"))
		);
	}

	@Override
	protected IModel<String> getTitleModel() {
		return new ResourceModel("security.password.creation.pageTitle");
	}

	@Override
	protected Component getContentComponent(String wicketId) {
		return new SecurityPasswordCreationContentPanel(wicketId, userModel);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		Detachables.detach(userModel);
	}

}
