package fr.openwide.core.wicket.more.security.page;

import org.apache.wicket.Page;

import fr.openwide.core.spring.util.StringUtils;
import fr.openwide.core.wicket.more.AbstractCoreSession;
import fr.openwide.core.wicket.more.link.descriptor.IPageLinkDescriptor;
import fr.openwide.core.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import fr.openwide.core.wicket.more.markup.html.CoreWebPage;
import fr.openwide.core.wicket.more.request.cycle.RequestCycleUtils;

public class LoginSuccessPage extends CoreWebPage {
	
	private static final long serialVersionUID = -875304387617628398L;
	
	public static final String WICKET_BEHAVIOR_LISTENER_URL_FRAGMENT = "IBehaviorListener";
	
	public static final IPageLinkDescriptor linkDescriptor() {
		return new LinkDescriptorBuilder().page(LoginSuccessPage.class).build();
	}
	
	public LoginSuccessPage() {
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		redirectToSavedPage();
	}
	
	protected void redirectToSavedPage() {
		AbstractCoreSession<?> session = AbstractCoreSession.get();
		
		IPageLinkDescriptor pageLinkDescriptor = session.getRedirectPageLinkDescriptor();
		if (pageLinkDescriptor != null) {
			throw pageLinkDescriptor.newRestartResponseException();
		}
		
		String redirectUrl = session.consumeRedirectUrl();
		if (!StringUtils.hasText(redirectUrl)) {
			redirectUrl = RequestCycleUtils.getSpringSecuritySavedRequest();
		}
		if (isUrlValid(redirectUrl)) {
			redirect(redirectUrl);
		} else {
			redirect(getDefaultRedirectPage());
		}
	}
	
	protected Class<? extends Page> getDefaultRedirectPage() {
		return getApplication().getHomePage();
	}
	
	protected boolean isUrlValid(String url) {
		return StringUtils.hasText(url);
		//  && !StringUtils.contains(url, WICKET_BEHAVIOR_LISTENER_URL_FRAGMENT) : Wicket a l'air de s'en sortir et on a vite des problèmes sur Sitra si on vire ces éléments
	}

}