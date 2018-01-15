package org.iglooproject.showcase.web.application.widgets.page;

import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.iglooproject.showcase.core.business.user.model.User;
import org.iglooproject.showcase.core.business.user.service.IUserService;
import org.iglooproject.showcase.core.util.binding.Bindings;
import org.iglooproject.showcase.web.application.widgets.component.UserSelect2AjaxMultipleChoice;
import org.iglooproject.showcase.web.application.widgets.component.UserSelect2DropDownChoice;
import org.iglooproject.showcase.web.application.widgets.component.UserSelect2ListMultipleChoice;
import org.iglooproject.wicket.more.link.descriptor.IPageLinkDescriptor;
import org.iglooproject.wicket.more.link.descriptor.builder.LinkDescriptorBuilder;
import org.iglooproject.wicket.more.markup.html.template.model.BreadCrumbElement;
import org.iglooproject.wicket.more.model.BindingModel;

import com.google.common.collect.Lists;

public class SelectBoxPage extends WidgetsTemplate {

	private static final long serialVersionUID = -4802009584951257187L;

	public static final IPageLinkDescriptor linkDescriptor() {
		return LinkDescriptorBuilder.start()
				.page(SelectBoxPage.class);
	}
	
	@SpringBean
	private IUserService userService;
	
	private IModel<User> userModel = Model.of();
	
	private List<User> selectedUsers = Lists.newArrayList();
	
	private List<User> ajaxSelectedUsers = Lists.newArrayList();
	
	public SelectBoxPage(PageParameters parameters) {
		super(parameters);
		
		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("widgets.menu.selectbox"), SelectBoxPage.linkDescriptor()));

		List<User> userList = userService.list();
		
		// User DropDownChoice with optgroups
		add(new Label("dropDownSelectedUser", BindingModel.of(userModel, Bindings.user().fullName())));
		
		UserSelect2DropDownChoice dropDownChoice = new UserSelect2DropDownChoice("dropDownChoice", userModel, userList);
		add(newShowcaseForm("single", dropDownChoice));
		
		// User ListMultipleChoice with optgroups
		add(new ListView<User>("listSelectedUser", selectedUsers) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<User> item) {
				item.add(new Label("userName", BindingModel.of(item.getModel(), Bindings.user().fullName())));
			}
		});

		UserSelect2ListMultipleChoice listMultipleChoice = new UserSelect2ListMultipleChoice("listMultipleChoice",
				new CollectionModel<User>(selectedUsers), userList);
		add(newShowcaseForm("multi", listMultipleChoice));
		
		// Ajax
		add(new ListView<User>("ajaxSelectedUser", ajaxSelectedUsers) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<User> item) {
				item.add(new Label("userName", BindingModel.of(item.getModel(), Bindings.user().fullName())));
			}
		});
		
		UserSelect2AjaxMultipleChoice ajaxMultipleChoice = new UserSelect2AjaxMultipleChoice("ajaxMultipleChoice",
				new CollectionModel<User>(ajaxSelectedUsers));
		
		add(newShowcaseForm("ajax", ajaxMultipleChoice));
	}
	
	private <T> Form<T> newShowcaseForm(String id, FormComponent<T> child) {
		Form<T> form = new Form<T>(id, child.getModel());
		form.add(child);
		return form;
	}

	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return SelectBoxPage.class;
	}
}
