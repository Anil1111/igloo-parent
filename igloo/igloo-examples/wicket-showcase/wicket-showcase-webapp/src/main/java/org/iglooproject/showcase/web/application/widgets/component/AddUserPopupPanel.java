package org.iglooproject.showcase.web.application.widgets.component;

import javax.persistence.PersistenceException;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.iglooproject.showcase.core.business.user.model.User;
import org.iglooproject.showcase.core.business.user.service.IUserService;
import org.iglooproject.showcase.core.util.binding.Bindings;
import org.iglooproject.wicket.more.markup.html.feedback.FeedbackUtils;
import org.iglooproject.wicket.more.markup.html.link.BlankLink;
import org.iglooproject.wicket.more.markup.html.template.js.bootstrap.modal.component.AbstractAjaxModalPopupPanel;
import org.iglooproject.wicket.more.markup.html.template.js.bootstrap.modal.component.DelegatedMarkupPanel;
import org.iglooproject.wicket.more.model.BindingModel;
import org.iglooproject.wicket.more.model.GenericEntityModel;

public class AddUserPopupPanel extends AbstractAjaxModalPopupPanel<User> {

	private static final long serialVersionUID = 8434113733267773945L;
	
	@SpringBean
	private IUserService userService;
	
	private Form<User> addUserForm;
	
	public AddUserPopupPanel(String id) {
		super(id, new GenericEntityModel<Long, User>(new User()));
	}
	
	@Override
	protected Component createHeader(String wicketId) {
		return new Label(wicketId, new ResourceModel("widgets.modal.user.new"));
	}
	
	@Override
	protected Component createBody(String wicketId) {
		DelegatedMarkupPanel body = new DelegatedMarkupPanel(wicketId, AddUserPopupPanel.class);
		
		addUserForm = new Form<User>("addUserForm", getModel());
		body.add(addUserForm);
		
		TextField<String> userName = new RequiredTextField<String>("userName", BindingModel.of(addUserForm.getModel(), Bindings.user().userName()));
		userName.setLabel(new ResourceModel("widgets.modal.user.userName"));
		addUserForm.add(userName);
		
		TextField<String> firstName = new RequiredTextField<String>("firstName", BindingModel.of(addUserForm.getModel(), Bindings.user().firstName()));
		firstName.setLabel(new ResourceModel("widgets.modal.user.firstName"));
		addUserForm.add(firstName);
		
		TextField<String> lastName = new RequiredTextField<String>("lastName", BindingModel.of(addUserForm.getModel(), Bindings.user().lastName()));
		lastName.setLabel(new ResourceModel("widgets.modal.user.lastName"));
		addUserForm.add(lastName);
		
		EmailTextField email = new EmailTextField("email", BindingModel.of(addUserForm.getModel(), Bindings.user().email()));
		email.setRequired(true);
		email.setLabel(new ResourceModel("widgets.modal.user.email"));
		addUserForm.add(email);
		
		return body;
	}
	
	@Override
	protected Component createFooter(String wicketId) {
		DelegatedMarkupPanel footer = new DelegatedMarkupPanel(wicketId, AddUserPopupPanel.class);
		
		// Bouton valider
		AjaxButton valider = new AjaxButton("save", addUserForm) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					userService.create(addUserForm.getModelObject());
					Session.get().success(getString("widgets.modal.user.success"));
					closePopup(target);
				} catch (PersistenceException e) {
					Session.get().error(getString("widgets.modal.user.error.dataIntegrity"));
				} catch (Exception e) {
					Session.get().error(getString("widgets.modal.user.error.unexpected"));
				}
				
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				FeedbackUtils.refreshFeedback(target, getPage());
			}
		};
		footer.add(valider);
		
		BlankLink annuler = new BlankLink("cancel");
		addCancelBehavior(annuler);
		footer.add(annuler);
		
		return footer;
	}
	
	@Override
	protected void onShow(AjaxRequestTarget target) {
		super.onShow(target);
		addUserForm.setModelObject(new User());
	}
	
	@Override
	public IModel<String> getCssClassNamesModel() {
		return Model.of("modal-user");
	}
}

