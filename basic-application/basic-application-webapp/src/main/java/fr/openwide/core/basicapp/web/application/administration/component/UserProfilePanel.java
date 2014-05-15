package fr.openwide.core.basicapp.web.application.administration.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.odlabs.wiquery.core.events.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.core.basicapp.core.business.user.model.User;
import fr.openwide.core.basicapp.core.business.user.service.IUserService;
import fr.openwide.core.basicapp.core.util.binding.Bindings;
import fr.openwide.core.basicapp.web.application.BasicApplicationSession;
import fr.openwide.core.basicapp.web.application.administration.form.ChangePasswordPopupPanel;
import fr.openwide.core.basicapp.web.application.administration.form.UserFormPopupPanel;
import fr.openwide.core.wicket.markup.html.link.EmailLink;
import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.basic.DateLabel;
import fr.openwide.core.wicket.more.markup.html.basic.LocaleLabel;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.image.BooleanIcon;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.component.AjaxConfirmLink;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.modal.behavior.AjaxModalOpenBehavior;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.util.DatePattern;

public class UserProfilePanel extends GenericPanel<User> {

	private static final long serialVersionUID = 8651898170121443991L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserProfilePanel.class);

	@SpringBean
	private IUserService userService;

	public UserProfilePanel(String id, final IModel<User> userModel) {
		super(id, userModel);
		
		add(new Label("userName", BindingModel.of(userModel, Bindings.user().userName())));
		add(new BooleanIcon("active", BindingModel.of(userModel, Bindings.user().active())));
		add(new EmailLink("email", BindingModel.of(userModel, Bindings.user().email())));
		add(new DateLabel("creationDate", BindingModel.of(userModel, Bindings.user().creationDate()),
				DatePattern.SHORT_DATETIME));
		add(new DateLabel("lastLoginDate", BindingModel.of(userModel, Bindings.user().lastLoginDate()),
				DatePattern.SHORT_DATETIME));
		add(new DateLabel("lastUpdateDate", BindingModel.of(userModel, Bindings.user().lastUpdateDate()),
				DatePattern.SHORT_DATETIME));
		add(new LocaleLabel("locale", BindingModel.of(userModel, Bindings.user().locale())));
		
		// User update popup
		UserFormPopupPanel<User> userUpdatePanel = new UserFormPopupPanel<>("userUpdatePopupPanel", getModel());
		add(userUpdatePanel);
		
		Button updateUser = new Button("updateUser");
		updateUser.add(new AjaxModalOpenBehavior(userUpdatePanel, MouseEvent.CLICK) {
			private static final long serialVersionUID = 5414159291353181776L;
			
			@Override
			protected void onShow(AjaxRequestTarget target) {
			}
		});
		add(updateUser);
		
		// Change password popup
		ChangePasswordPopupPanel changePasswordPanel = new ChangePasswordPopupPanel("changePasswordPopupPanel", getModel());
		add(changePasswordPanel);
		
		Button changeUserPassword = new Button("changeUserPassword");
		changeUserPassword.add(new AjaxModalOpenBehavior(changePasswordPanel, MouseEvent.CLICK) {
			private static final long serialVersionUID = -7179264122322968921L;
			
			@Override
			protected void onShow(AjaxRequestTarget target) {
			}
		});
		add(changeUserPassword);
		
		// Enable user link
		add(new Link<User>("enableUser", userModel) {
			private static final long serialVersionUID = 6157423807032594861L;
			
			@Override
			public void onClick() {
				try {
					userService.setActive(getModelObject(), true);
					getSession().success(getString("administration.user.enable.success"));
				} catch (Exception e) {
					LOGGER.error("Error occured while enabling user", e);
					getSession().error(getString("common.error"));
				}
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(!getModelObject().isActive());
			}
		});
		
		// Disable user link
		IModel<String> confirmationTextModel = new StringResourceModel(
				"administration.user.disable.confirmation.text", null, 
				new Object[] { userModel.getObject().getFullName() }
		);
		
		add(new AjaxConfirmLink<User>("disableUser", userModel,
				new ResourceModel("administration.user.disable.confirmation.title"),
				confirmationTextModel,
				new ResourceModel("common.confirm"),
				new ResourceModel("common.cancel"),
				null, false) {
			private static final long serialVersionUID = 6157423807032594861L;
			
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					userService.setActive(getModelObject(), false);
					getSession().success(getString("administration.user.disable.success"));
				} catch (Exception e) {
					LOGGER.error("Error occured while disabling user", e);
					getSession().error(getString("common.error"));
				}
				target.add(getPage());
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				User displayedUser = getModelObject();
				User currentUser = BasicApplicationSession.get().getUser();
				setVisible(!displayedUser.equals(currentUser) && displayedUser.isActive());
			}
		});
	}
}
