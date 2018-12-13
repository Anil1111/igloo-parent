package test.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.wicket.markup.html.link.Link;
import org.iglooproject.basicapp.web.application.administration.page.AdministrationBasicUserDetailPage;
import org.iglooproject.basicapp.web.application.administration.page.AdministrationBasicUserListPage;
import org.iglooproject.basicapp.web.application.administration.template.AdministrationUserDetailTemplate;
import org.iglooproject.jpa.exception.SecurityServiceException;
import org.iglooproject.jpa.exception.ServiceException;
import org.iglooproject.wicket.more.markup.html.template.component.BreadCrumbListView;
import org.iglooproject.wicket.more.markup.html.template.component.LinkGeneratorBreadCrumbElementPanel;
import org.iglooproject.wicket.more.model.GenericEntityModel;
import org.junit.Test;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@EnableWebSecurity
public class AdministrationBasicUserDetailPageTestCase extends AbstractBasicApplicationWebappTestCase {

	@Test
	public void initPage() throws ServiceException, SecurityServiceException {
		authenticateUser(administrator);
		
		String url = AdministrationUserDetailTemplate.mapper()
			.ignoreParameter2()
			.map(GenericEntityModel.of(basicUser)).url();
		tester.executeUrl(url);
		
		tester.assertRenderedPage(AdministrationBasicUserDetailPage.class);
	}

	@Test
	public void breadcrumb() throws ServiceException, SecurityServiceException {
		authenticateUser(administrator);
		
		String url = AdministrationUserDetailTemplate.mapper()
			.ignoreParameter2()
			.map(GenericEntityModel.of(basicUser)).url();
		tester.executeUrl(url);
		
		tester.assertRenderedPage(AdministrationBasicUserDetailPage.class);
		
		tester.assertVisible(tester.breadCrumbPath());
		tester.assertComponent(tester.breadCrumbPath(), BreadCrumbListView.class);
		
		String administrationBreadCrumbPath = tester.breadCrumbElementPath(0);
		tester.assertVisible(administrationBreadCrumbPath);
		tester.assertComponent(administrationBreadCrumbPath, LinkGeneratorBreadCrumbElementPanel.class);
		tester.assertVisible(administrationBreadCrumbPath + ":breadCrumbElementLink");
		tester.assertEnabled(administrationBreadCrumbPath + ":breadCrumbElementLink");
		tester.assertComponent(administrationBreadCrumbPath + ":breadCrumbElementLink", Link.class);
		@SuppressWarnings("unchecked")
		Link<Void> administrationLink = (Link<Void>) tester.getComponentFromLastRenderedPage(administrationBreadCrumbPath + ":breadCrumbElementLink");
		String administrationLabel = (String) administrationLink.getBody().getObject();
		assertEquals(localize("navigation.administration"), administrationLabel);
		
		String administrationBasicUserBreadCrumbPath = tester.breadCrumbElementPath(1);
		tester.assertVisible(administrationBasicUserBreadCrumbPath);
		tester.assertComponent(administrationBasicUserBreadCrumbPath, LinkGeneratorBreadCrumbElementPanel.class);
		tester.assertVisible(administrationBasicUserBreadCrumbPath + ":breadCrumbElementLink");
		tester.assertEnabled(administrationBasicUserBreadCrumbPath + ":breadCrumbElementLink");
		tester.assertComponent(administrationBasicUserBreadCrumbPath + ":breadCrumbElementLink", Link.class);
		@SuppressWarnings("unchecked")
		Link<Void> administrationBasicUserLink = (Link<Void>) tester.getComponentFromLastRenderedPage(administrationBasicUserBreadCrumbPath + ":breadCrumbElementLink");
		String administrationBasicUserLabel = (String) administrationBasicUserLink.getBody().getObject();
		assertEquals(localize("navigation.administration.user.basicUser"), administrationBasicUserLabel);
		
		tester.clickLink(administrationBreadCrumbPath + ":breadCrumbElementLink");
		tester.assertRenderedPage(AdministrationBasicUserListPage.class);
	}

	@Test
	public void desactivateUser() throws ServiceException, SecurityServiceException {
		authenticateUser(administrator);
		
		String url = AdministrationUserDetailTemplate.mapper()
			.ignoreParameter2()
			.map(GenericEntityModel.of(basicUser)).url();
		tester.executeUrl(url);
		
		assertTrue(basicUser.isActive());
		
		tester.assertInvisible("headerElementsSection:actionsContainer:enable");
		tester.assertVisible("headerElementsSection:actionsContainer:disable");
		tester.assertEnabled("headerElementsSection:actionsContainer:disable");
		
		tester.executeAjaxEvent("headerElementsSection:actionsContainer:disable", "confirm");
		
		tester.assertVisible("headerElementsSection:actionsContainer:enable");
		tester.assertEnabled("headerElementsSection:actionsContainer:enable");
		tester.assertInvisible("headerElementsSection:actionsContainer:disable");
		
		assertFalse(basicUser.isActive());
	}
}
